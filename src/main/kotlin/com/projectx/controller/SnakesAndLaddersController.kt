package com.projectx.controller

import com.projectx.utils.GameUtils.setFixedWindowSize
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import kotlin.random.Random

class SnakesAndLaddersController {

    @FXML
    private lateinit var gameBoard: GridPane

    @FXML
    private lateinit var rollDiceButton: Button

    @FXML
    private lateinit var positionLabel: Label

    @FXML
    private lateinit var overlayPane: StackPane

    private lateinit var overlayController: OverlayController

    private var playerPosition = 1
    private val boardSize = 10
    private val totalSquares = boardSize * boardSize

    private val snakes = mapOf(17 to 7, 54 to 34, 62 to 19, 98 to 79)
    private val ladders = mapOf(3 to 22, 6 to 25, 20 to 38, 36 to 57, 51 to 72, 63 to 81, 71 to 91)

    @FXML
    fun initialize() {
        setupBoard()
        setFixedWindowSize(gameBoard.scene?.window as? Stage)
        overlayController = overlayPane.properties["controller"] as OverlayController
        rollDiceButton.setOnAction { rollDice() }
    }

    private fun setupBoard() {
        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val cellNumber = calculateCellNumber(row, col)
                val label = Label("$cellNumber")
                label.style = "-fx-border-color: black; -fx-padding: 10px; -fx-font-size: 14px;"
                gameBoard.add(label, col, boardSize - 1 - row)
            }
        }
        updatePlayerPosition()
    }

    private fun rollDice() {
        val diceRoll = Random.nextInt(1, 7)
        val newPosition = playerPosition + diceRoll

        if (newPosition > totalSquares) {
            positionLabel.text = "Rolled: $diceRoll. Need an exact roll to win!"
            return
        }

        playerPosition = newPosition
        positionLabel.text = "Rolled: $diceRoll, Current Position: $playerPosition"

        if (playerPosition in snakes) {
            playerPosition = snakes[playerPosition]!!
            positionLabel.text += " 🐍 Down to $playerPosition"
        } else if (playerPosition in ladders) {
            playerPosition = ladders[playerPosition]!!
            positionLabel.text += " ⬆️ Up to $playerPosition"
        }

        updatePlayerPosition()

        if (playerPosition == totalSquares) {
            overlayController.showOverlay("Congratulations! You've won Snakes & Ladders! 🎉", true,
                gameBoard.scene.window as Stage, "/Tiles.fxml")
        }
    }

    private fun updatePlayerPosition() {
        gameBoard.children.removeIf { it is javafx.scene.shape.Circle }

        val row = (playerPosition - 1) / boardSize

        val col = if (row % 2 == 0) {
            (playerPosition - 1) % boardSize // Left to right
        } else {
            boardSize - 1 - ((playerPosition - 1) % boardSize) // Right to left
        }
        val playerToken = javafx.scene.shape.Circle(10.0)
        playerToken.style = "-fx-fill: blue;"
        gameBoard.add(playerToken, col, boardSize - 1 - row)
    }

    private fun calculateCellNumber(row: Int, col: Int): Int {
        return if (row % 2 == 0) {
            (row * boardSize) + (col + 1)
        } else {
            ((row + 1) * boardSize) - col
        }
    }
}
