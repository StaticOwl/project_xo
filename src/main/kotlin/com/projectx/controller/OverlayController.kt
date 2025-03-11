package com.projectx.controller

import javafx.animation.FadeTransition
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.util.Duration

class OverlayController {

    @FXML
    private lateinit var overlayPane: StackPane

    @FXML
    private lateinit var overlayMessage: Label

    @FXML
    private lateinit var overlayButton: Button

    private var nextCheckpointFXML: String? = null

    @FXML
    fun initialize() {
        overlayPane.properties["controller"] = this@OverlayController
        overlayButton.setOnAction { handleOverlayButton() }
    }

    fun showOverlay(message: String, isSuccess: Boolean, stage: Stage, nextCheckpointFXML: String?, putButton: Boolean = true) {
        this.nextCheckpointFXML = nextCheckpointFXML

        overlayMessage.text = message
        overlayButton.text = if (isSuccess) "Let's Go" else "Try Again"
        overlayButton.isVisible = putButton

        overlayPane.isVisible = true
        val fadeIn = FadeTransition(Duration.seconds(0.5), overlayPane)
        fadeIn.fromValue = 0.0
        fadeIn.toValue = 1.0
        fadeIn.play()
    }

    private fun handleOverlayButton() {
        if (overlayButton.text == "Let's Go" && nextCheckpointFXML != null) {
            moveToNextCheckpoint()
        } else {
            hideOverlay()
        }
    }

    private fun hideOverlay() {
        val fadeOut = FadeTransition(Duration.seconds(0.5), overlayPane)
        fadeOut.fromValue = 1.0
        fadeOut.toValue = 0.0
        fadeOut.setOnFinished { overlayPane.isVisible = false }
        fadeOut.play()
    }

    private fun moveToNextCheckpoint() {
        overlayButton.isVisible = false
        val fadeOut = FadeTransition(Duration.seconds(0.5), overlayPane)
        fadeOut.fromValue = 1.0
        fadeOut.toValue = 0.0
        fadeOut.setOnFinished {
            overlayPane.isVisible = false
            val fxmlLoader = javafx.fxml.FXMLLoader(nextCheckpointFXML?.let { it1 -> javaClass.getResource(it1) })
            val scene = javafx.scene.Scene(fxmlLoader.load())
            val stage = overlayPane.scene.window as Stage
            stage.scene = scene
        }
        fadeOut.play()
    }
}
