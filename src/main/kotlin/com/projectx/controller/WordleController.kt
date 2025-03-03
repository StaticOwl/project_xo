package com.projectx.controller

import com.projectx.utils.GameUtils.setFixedWindowSize
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class WordleController {
    @FXML
    private lateinit var wordGrid: GridPane

    @FXML
    private lateinit var wordleSubmit: Button

    @FXML
    private lateinit var overlayPane: StackPane

    private lateinit var overlayController: OverlayController

    private val correctWord = "APPLE"
    private var currentRow = 0
    private val maxAttempts = 6
    private val gridCells = Array(maxAttempts) { Array(5) { TextField() } }

    @FXML
    fun initialize() {
        setupGrid()
        setFixedWindowSize(wordleSubmit.scene?.window as? Stage)

        overlayController = overlayPane.properties["controller"] as OverlayController

        wordleSubmit.setOnAction {
            submitWord()
        }
    }

    private fun setupGrid() {
        for (row in 0 until maxAttempts) {
            for (col in 0 until 5) {
                val cell = TextField()
                cell.prefWidth = 60.0
                cell.prefHeight = 50.0
                cell.style = """
                -fx-font-size: 24px;
                -fx-font-weight: bold;
                -fx-alignment: center;
                -fx-border-color: black;
                -fx-border-width: 2px;
                -fx-text-fill: black;
                """.trimIndent()
                cell.isEditable = row == 0
                cell.textProperty().addListener { _, _, newValue ->
                    if (newValue.length > 1) {
                        cell.text = newValue.substring(0, 1).uppercase()
                    }
                    if (newValue.isNotEmpty() && col < 4) {
                        gridCells[row][col + 1].requestFocus()
                    }
                }
                cell.setOnKeyPressed { event ->
                    if (event.code.toString() == "BACK_SPACE" && col > 0 && cell.text.isEmpty()) {
                        gridCells[row][col - 1].requestFocus()
                    }
                }
                gridCells[row][col] = cell
                wordGrid.add(cell, col, row)
            }
        }
    }

    private fun submitWord() {
        if (currentRow >= maxAttempts) return

        val userWord = gridCells[currentRow].joinToString("") { it.text.trim().uppercase() }
        if (userWord.length != 5) {
            overlayController.showOverlay("Enter a full 5-letter word!", false, wordGrid.scene.window as Stage, null)
            return
        }

        for (i in userWord.indices) {
            val letter = userWord[i].toString()
            val cell = gridCells[currentRow][i]

            when {
                letter == correctWord[i].toString() -> cell.style = "-fx-background-color: green; -fx-text-fill: white;"
                letter in correctWord -> cell.style = "-fx-background-color: yellow; -fx-text-fill: black;"
                else -> cell.style = "-fx-background-color: gray; -fx-text-fill: white;"
            }
        }

        if (userWord == correctWord) {
            overlayController.showOverlay("Congratulations! Moving to the next checkpoint...", true, wordGrid.scene.window as Stage, "/SnakesAndLadders.fxml")
            return
        }

        if (currentRow + 1 < maxAttempts) {
            for (cell in gridCells[currentRow]) cell.isEditable = false
            for (cell in gridCells[currentRow + 1]) cell.isEditable = true
        } else {
            overlayController.showOverlay("Game Over! The word was $correctWord", false, wordGrid.scene.window as Stage, null)
        }

        currentRow++
    }
}
