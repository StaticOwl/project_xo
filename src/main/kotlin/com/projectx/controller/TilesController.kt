package com.projectx.controller

import com.projectx.utils.GameUtils.setFixedWindowSize
import javafx.animation.FadeTransition
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.util.Duration
import kotlin.random.Random

class TilesController {
    @FXML
    private lateinit var gameGrid: GridPane

    @FXML
    private lateinit var feedbackMessage: Label

    @FXML
    private lateinit var overlayPane: StackPane

    private lateinit var overlayController: OverlayController

    private val gridSize = 3 // 3x3 grid
    private var correctTileRow = 0
    private var correctTileCol = 0

    @FXML
    fun initialize() {
        setFixedWindowSize(gameGrid.scene?.window as? Stage)

        overlayController = overlayPane.properties["controller"] as OverlayController

        // Randomly select the correct tile
        correctTileRow = Random.nextInt(gridSize)
        correctTileCol = Random.nextInt(gridSize)

        setupGrid()
    }

    private fun setupGrid() {
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val button = Button("Click")
                button.prefWidth = 100.0
                button.prefHeight = 100.0
                button.style = "-fx-font-size: 18px; -fx-background-radius: 12px;"

                button.setOnAction {
                    if (row == correctTileRow && col == correctTileCol) {
                        showSuccess()
                    } else {
                        triggerWrongFlip(button)
                    }
                }

                gameGrid.add(button, col, row)
            }
        }
    }

    private fun triggerWrongFlip(button: Button) {
        val flipToMid = javafx.animation.RotateTransition(Duration.millis(200.0), button).apply {
            fromAngle = 0.0
            toAngle = 90.0
            axis = javafx.geometry.Point3D(0.0, 1.0, 0.0)
        }

        val flipToEnd = javafx.animation.RotateTransition(Duration.millis(200.0), button).apply {
            fromAngle = 90.0
            toAngle = 180.0
            axis = javafx.geometry.Point3D(0.0, 1.0, 0.0)
        }

        flipToMid.setOnFinished {
            button.text = "\uD83C\uDF3A" // 🌺 or try 🌸 or 🌼
            button.style = "-fx-font-size: 30px; -fx-background-color: #ffe0f0; -fx-background-radius: 12px;"
            feedbackMessage.text = "Try again 🌼"
            button.isDisable = true
            flipToEnd.play()
        }

        flipToMid.play()
    }

    private fun showSuccess() {
        feedbackMessage.text = "You got it, smarty! ✨"
        feedbackMessage.style = "-fx-text-fill: blue; -fx-font-size: 24px; -fx-font-weight: bold;"
        val fadeOut = FadeTransition(Duration.seconds(2.0), gameGrid)
        fadeOut.fromValue = 1.0
        fadeOut.toValue = 0.0
        fadeOut.setOnFinished {
            overlayController.showOverlay(
                "\uD83C\uDF89 Happy Birthday, my girl! \uD83D\uDC96 Life is tough, growing up is tougher. But it's just like this game. You finish one, another comes! I am there with you, just like that hint button in Wordle, to support you whenever you need it. This is the final checkpoint, and you aced it, like everything else! \uD83C\uDF82\uD83C\uDF88",
                true,
                null,
                putButton = false,
                backgroundImage = "/assets/final.jpg"
            )
        }
        fadeOut.play()
    }

}