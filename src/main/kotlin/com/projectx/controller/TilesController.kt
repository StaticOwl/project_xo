package com.projectx.controller

import com.projectx.utils.GameUtils.setFixedWindowSize
import javafx.animation.FadeTransition
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import javafx.util.Duration
import kotlin.random.Random

class TilesController {
    @FXML
    private lateinit var gameGrid: GridPane

    @FXML
    private lateinit var feedbackMessage: Label

    private val gridSize = 3 // 3x3 grid
    private var correctTileRow = 0
    private var correctTileCol = 0

    @FXML
    fun initialize() {
        setFixedWindowSize(gameGrid.scene?.window as? Stage)

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
                button.style = "-fx-font-size: 18px;"

                button.setOnAction {
                    if (row == correctTileRow && col == correctTileCol) {
                        showSuccess()
                    } else {
                        feedbackMessage.text = "Try again!"
                    }
                }

                gameGrid.add(button, col, row)
            }
        }
    }

    private fun showSuccess() {
        feedbackMessage.text = "Congratulations!"
        val fadeOut = FadeTransition(Duration.seconds(2.0), gameGrid)
        fadeOut.fromValue = 1.0
        fadeOut.toValue = 0.0
        fadeOut.setOnFinished {
            showBirthdayMessage()
        }
        fadeOut.play()
    }

    private fun showBirthdayMessage() {
        feedbackMessage.text = "🎉 Happy Birthday! 🎂🎊"
        feedbackMessage.style = "-fx-text-fill: green; -fx-font-size: 32px; -fx-font-weight: bold;"
    }
}