package com.projectx.utils

import javafx.animation.FadeTransition
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.util.Duration

object OverlayUtil {

    fun showOverlay(overlayPane: StackPane, overlayMessage: Label, overlayButton: Button, message: String, isSuccess: Boolean, stage: Stage, nextCheckpointFXML: String?) {
        overlayMessage.text = message
        overlayButton.text = if (isSuccess) "Let's Go" else "Try Again"

        overlayPane.isVisible = true
        val fadeIn = FadeTransition(Duration.seconds(0.5), overlayPane)
        fadeIn.fromValue = 0.0
        fadeIn.toValue = 1.0
        fadeIn.play()

        overlayButton.setOnAction {
            if (isSuccess && nextCheckpointFXML != null) {
                moveToNextCheckpoint(overlayPane, stage, nextCheckpointFXML)
            } else {
                hideOverlay(overlayPane)
            }
        }
    }

    private fun hideOverlay(overlayPane: StackPane) {
        val fadeOut = FadeTransition(Duration.seconds(0.5), overlayPane)
        fadeOut.fromValue = 1.0
        fadeOut.toValue = 0.0
        fadeOut.setOnFinished { overlayPane.isVisible = false }
        fadeOut.play()
    }

    private fun moveToNextCheckpoint(overlayPane: StackPane, stage: Stage, nextCheckpointFXML: String) {
        val fadeOut = FadeTransition(Duration.seconds(0.5), overlayPane)
        fadeOut.fromValue = 1.0
        fadeOut.toValue = 0.0
        fadeOut.setOnFinished {
            overlayPane.isVisible = false
            val fxmlLoader = FXMLLoader(OverlayUtil::class.java.getResource(nextCheckpointFXML))
            val scene = Scene(fxmlLoader.load())
            stage.scene = scene
        }
        fadeOut.play()
    }
}
