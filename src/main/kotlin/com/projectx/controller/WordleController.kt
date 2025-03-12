package com.projectx.controller

import com.projectx.utils.GameUtils.setFixedWindowSize
import javafx.animation.FadeTransition
import javafx.animation.RotateTransition
import javafx.animation.SequentialTransition
import javafx.animation.TranslateTransition
import javafx.fxml.FXML
import javafx.geometry.Point3D
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.util.Duration

class WordleController {
    @FXML
    private lateinit var wordGrid: GridPane

    @FXML
    private lateinit var overlayPane: StackPane

    @FXML
    private lateinit var hintButton: Button

    private lateinit var overlayController: OverlayController

    private val correctWord = "APPLE"
    private var currentRow = 0
    private val maxAttempts = 6
    private val gridCells = Array(maxAttempts) { Array(5) { TextField() } }

    private val hintMessages = listOf(
        "Hmm... You don’t trust your brain that easily? 😉",
        "Maybe a kiss would help you think better? 😘",
        "Alright alright… focus, Bubu 😏 you can do this!",
        "The word starts with '${correctWord.substring(0, 3)}'... I'm not telling more \uD83D\uDE18",
        "It's ${correctWord.uppercase()}. I gave up. You're too cute to suffer 😂"
    )

    @FXML
    fun initialize() {
        setupGrid()
        setFixedWindowSize(wordGrid.scene?.window as? Stage)

        overlayController = overlayPane.properties["controller"] as OverlayController
        hintButton.isVisible = false

        hintButton.setOnAction {
            val hintIndex = (currentRow - 1).coerceIn(0, hintMessages.lastIndex)
            val hintMessage = hintMessages[hintIndex]
            overlayController.showOverlay(hintMessage, false, null)
        }
    }

    private fun setupGrid() {
        for (row in 0 until maxAttempts) {
            for (col in 0 until 5) {
                val cell = TextField().apply {
                    prefWidth = 60.0
                    prefHeight = 50.0
                    styleClass.add("wordle-cell")
                    isEditable = row == 0
                }

                cell.textProperty().addListener { _, _, newValue ->
                    if (newValue.length > 1) {
                        cell.text = newValue.substring(0, 1).uppercase()
                    }
                    if (newValue.isNotEmpty() && col < 4) {
                        gridCells[row][col + 1].requestFocus()
                    }
                }

                cell.setOnKeyPressed { event ->
                    when (event.code) {
                        KeyCode.ENTER -> {
                            if (row == currentRow) {
                                val userWord = gridCells[currentRow].joinToString("") { it.text.trim().uppercase() }
                                if (userWord.length == 5) {
                                    submitWord(userWord)
                                }
                                else{
                                    shakeRow(currentRow)
                                }
                            }
                        }

                        KeyCode.BACK_SPACE -> {
                            if (col > 0 && cell.text.isEmpty()) {
                                gridCells[row][col - 1].requestFocus()
                            }
                        }

                        else -> {}
                    }
                }


                gridCells[row][col] = cell
                wordGrid.add(cell, col, row)
            }
        }
    }

    private fun submitWord(userWord:String) {
        if (currentRow >= maxAttempts) return

//        if (userWord.length != 5) {
//            overlayController.showOverlay("Enter a full 5-letter word!", false, null, retryFrom = this::class)
//            return
//        }

        for (i in userWord.indices) {
            val letter = userWord[i].toString()
            val cell = gridCells[currentRow][i]

            val staticText = Label(cell.text).apply {
                style = "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: black;"
                prefWidth = cell.width
                prefHeight = cell.height
                alignment = Pos.CENTER
                isVisible = false
            }

            (cell.parent as GridPane).add(staticText, GridPane.getColumnIndex(cell), GridPane.getRowIndex(cell))

            val flipToMid = RotateTransition(Duration.millis(200.0), cell).apply {
                fromAngle = 0.0
                toAngle = 90.0
                axis = Point3D(1.0, 0.0, 0.0)
            }

            val flipToEnd = RotateTransition(Duration.millis(200.0), cell).apply {
                fromAngle = 90.0
                toAngle = 180.0
                axis = Point3D(1.0, 0.0, 0.0)
            }

            flipToMid.setOnFinished {
                cell.text = ""
                cell.isEditable = false
                when (letter) {
                    correctWord[i].toString() -> cell.style =
                        "-fx-background-color: green; -fx-text-fill: white;"
                    in correctWord -> cell.style = "-fx-background-color: yellow; -fx-text-fill: black;"
                    else -> cell.style = "-fx-background-color: gray; -fx-text-fill: white;"
                }
                staticText.isVisible = true
                flipToEnd.play()
            }

            flipToMid.delay = Duration.millis((i * 150).toDouble())
            flipToMid.play()
        }

        if (userWord == correctWord) {
            overlayController.showOverlay(
                "I knew you'd crack it \uD83D\uDE0F Off to the next checkpoint, genius girl ❤",
                true,
                "/SnakesAndLadders.fxml",
                backgroundImage = "/assets/letsgo.gif"
            )
            return
        }

        if (currentRow + 1 < maxAttempts) {
            for (cell in gridCells[currentRow]) cell.isEditable = false
            for (cell in gridCells[currentRow + 1]) cell.isEditable = true
            gridCells[currentRow + 1][0].requestFocus()
        } else {
            overlayController.showOverlay(
                "Alright! I'm gifting you the bf promo! The first 2 letters are ${correctWord.substring(0, 2)}!",
                false,
                null,
                retryFrom = this::class,
                resetScene = true
            )
        }

        val newOpacity = ((currentRow + 1) * 0.2).coerceAtMost(1.0)
        hintButton.isVisible = true

        val fade = FadeTransition(Duration.millis(300.0), hintButton).apply {
            fromValue = hintButton.opacity
            toValue = newOpacity
        }
        fade.play()

        currentRow++
    }

    private fun shakeRow(rowIndex: Int) {
        val rowCells = gridCells[rowIndex]

        rowCells.forEach { cell ->
            val moveRight = TranslateTransition(Duration.millis(50.0), cell).apply {
                byX = 10.0
            }

            val moveLeft = TranslateTransition(Duration.millis(50.0), cell).apply {
                byX = -20.0
            }

            val moveCenter = TranslateTransition(Duration.millis(50.0), cell).apply {
                byX = 10.0
            }

            cell.style += "; -fx-border-color: red;"

            val shake = SequentialTransition(moveRight, moveLeft, moveCenter)
            shake.play()
        }
    }


}
