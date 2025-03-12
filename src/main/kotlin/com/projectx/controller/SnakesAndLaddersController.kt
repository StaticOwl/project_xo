package com.projectx.controller

import com.projectx.utils.GameUtils.setFixedWindowSize
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Polygon
import javafx.stage.Stage
import java.lang.StrictMath.toRadians
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class SnakesAndLaddersController {

    @FXML
    private lateinit var gameBoard: GridPane

    @FXML
    private lateinit var arrowOverlayPane: Pane

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
        gameBoard.children.clear()
        arrowOverlayPane.children.clear()

        arrowOverlayPane.prefWidthProperty().bind(gameBoard.widthProperty())
        arrowOverlayPane.prefHeightProperty().bind(gameBoard.heightProperty())

        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val cellNumber = calculateCellNumber(row, col)
                val label = Label("$cellNumber").apply {
                    style = "-fx-font-size: 18px; -fx-text-fill: greenyellow; -fx-font-weight: bold"
                    prefWidth = 60.0
                    prefHeight = 60.0
                    alignment = javafx.geometry.Pos.CENTER
                }
                gameBoard.add(label, col, boardSize - 1 - row)
            }
        }

        // Draw connecting arrows
        Platform.runLater {
            snakes.forEach { (head, tail) -> drawConnectingArrow(head, tail, isLadder = false) }
            ladders.forEach { (start, end) -> drawConnectingArrow(start, end, isLadder = true) }
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
            positionLabel.text += " ⬆ Up to $playerPosition"
        }

        updatePlayerPosition()

        if (playerPosition == totalSquares) {
            overlayController.showOverlay(
                "Victory is yours! You danced through ladders and dodged every snake \uD83D\uDC0D\uD83D\uDC83 Next level awaits!", true,
                "/Tiles.fxml", backgroundImage = "/assets/snl_yay.gif"
            )
        }
    }

    private fun updatePlayerPosition() {
        gameBoard.children.removeIf { it is javafx.scene.shape.Circle }

        val row = (playerPosition - 1) / boardSize
        val col = if (row % 2 == 0) (playerPosition - 1) % boardSize else boardSize - 1 - ((playerPosition - 1) % boardSize)

        val playerToken = javafx.scene.shape.Circle(10.0).apply {
            fill = Color.BLUE
        }
        gameBoard.add(playerToken, col, boardSize - 1 - row)
    }

    private fun calculateCellNumber(row: Int, col: Int): Int {
        return if (row % 2 == 0) (row * boardSize) + (col + 1) else ((row + 1) * boardSize) - col
    }

    private fun drawConnectingArrow(from: Int, to: Int, isLadder: Boolean) {
        val fromCoords = getCellCenterCoords(from)
        val toCoords = getCellCenterCoords(to)

        val line = Line(fromCoords.first, fromCoords.second, toCoords.first, toCoords.second).apply {
            stroke = if (isLadder) Color.GREEN else Color.RED
            strokeWidth = 3.0
        }

        arrowOverlayPane.children.add(line)
        val arrowHead = createArrowHead(fromCoords, toCoords, isLadder)
        arrowOverlayPane.children.add(arrowHead)
    }

    private fun getCellCenterCoords(cellNumber: Int): Pair<Double, Double> {
        val row = (cellNumber - 1) / boardSize
        val col = if (row % 2 == 0) {
            (cellNumber - 1) % boardSize
        } else {
            boardSize - 1 - ((cellNumber - 1) % boardSize)
        }

        val cellNode = gameBoard.children.find {
            GridPane.getRowIndex(it) == boardSize - 1 - row && GridPane.getColumnIndex(it) == col
        }

        return if (cellNode != null) {
            val bounds = cellNode.boundsInParent
            Pair(bounds.minX + bounds.width / 2, bounds.minY + bounds.height / 2)
        } else {
            // fallback if no node is found
            val cellWidth = gameBoard.width / boardSize
            val cellHeight = gameBoard.height / boardSize
            Pair(col * cellWidth + cellWidth / 2, row * cellHeight + cellHeight / 2)
        }
    }


    private fun createArrowHead(start: Pair<Double, Double>, end: Pair<Double, Double>, isLadder: Boolean): Polygon {
        val angle = atan2(end.second - start.second, end.first - start.first)

        val length = 10.0
        val angle1 = angle - toRadians(20.0)
        val angle2 = angle + toRadians(20.0)

        val x1 = end.first - length * cos(angle1)
        val y1 = end.second - length * sin(angle1)
        val x2 = end.first - length * cos(angle2)
        val y2 = end.second - length * sin(angle2)

        return Polygon(
            end.first, end.second,
            x1, y1,
            x2, y2
        ).apply {
            fill = if (isLadder) Color.GREEN else Color.RED
        }
    }
}