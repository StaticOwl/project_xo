package com.projectx.controller

import com.projectx.utils.GameUtils.applyGlobalStyle
import com.projectx.utils.GameUtils.buildButtonStyle
import javafx.animation.FadeTransition
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.util.Duration
import kotlin.reflect.KClass

class OverlayController {

    @FXML
    private lateinit var overlayPane: StackPane

    @FXML
    private lateinit var overlayMessage: Label

    @FXML
    private lateinit var overlayButton: Button

    @FXML
    private lateinit var backgroundView: ImageView

    private var nextCheckpointFXML: String? = null

    private var retryControllerClass: KClass<*>? = null
    private var resetScene: Boolean = false

    @FXML
    fun initialize() {
        overlayPane.properties["controller"] = this@OverlayController

        overlayButton.style = buildButtonStyle("#982304")
        overlayButton.setOnMouseEntered {
            overlayButton.style = buildButtonStyle("#000000")
        }
        overlayButton.setOnMouseExited {
            overlayButton.style = buildButtonStyle("#982304")
        }

        overlayButton.setOnAction { handleOverlayButton() }
    }

    fun showOverlay(
        message: String,
        isSuccess: Boolean,
        nextCheckpointFXML: String?,
        putButton: Boolean = true,
        backgroundImage: String? = null,
        retryFrom: KClass<*>? = null,
        resetScene: Boolean = false
    ) {
        this.nextCheckpointFXML = nextCheckpointFXML
        this.retryControllerClass = retryFrom
        this.resetScene = resetScene


        overlayMessage.text = message
        overlayButton.text = if (isSuccess) "Let's Go" else "Try Again"
        overlayButton.isVisible = putButton

        val isGif = backgroundImage?.endsWith(".gif") == true

        if (isGif) {
            backgroundView.image = Image(backgroundImage?.let { javaClass.getResource(it) }!!.toExternalForm())
            backgroundView.isVisible = true
            overlayPane.style = ""
        } else {
            backgroundView.isVisible = false
            val bgUrl = (backgroundImage ?: "/assets/default_overlay.jpg").let {
                javaClass.getResource(it)?.toExternalForm()
            }
            overlayPane.style = ""
            overlayPane.style = """
            -fx-background-image: url('${bgUrl}');
                """.trimIndent()
        }

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
            if (resetScene){
                reloadSceneFromControllerClass()
            }
            else{
                hideOverlay()
            }
        }
    }

    private fun hideOverlay() {
        overlayButton.isVisible = false
        val fadeOut = FadeTransition(Duration.seconds(0.5), overlayPane).apply {
            fromValue = 1.0
            toValue = 0.0
            setOnFinished {
                overlayPane.isVisible = false
            }
        }
        fadeOut.play()
    }

    private fun moveToNextCheckpoint() {
        overlayButton.isVisible = false
        val fadeOut = FadeTransition(Duration.seconds(0.5), overlayPane)
        fadeOut.fromValue = 1.0
        fadeOut.toValue = 0.0
        fadeOut.setOnFinished {
            overlayPane.isVisible = false
            val fxmlLoader = FXMLLoader(nextCheckpointFXML?.let { it1 -> javaClass.getResource(it1) })
            val scene = Scene(fxmlLoader.load())
            applyGlobalStyle(scene)
            val stage = overlayPane.scene.window as Stage
            stage.scene = scene
        }
        fadeOut.play()
    }

    private fun reloadSceneFromControllerClass() {
        val fxmlPath = when (retryControllerClass) {
            TilesController::class -> "/TilesView.fxml"
            SnakesAndLaddersController::class -> "/SnakesAndLaddersView.fxml"
            WordleController::class -> "/WordleView.fxml"
            MainController::class -> "/MainView.fxml"
            else -> null
        }

        println("Retrying from: $retryControllerClass")
        println("Retrying to: $fxmlPath")

        if (fxmlPath != null) {
            val loader = FXMLLoader(javaClass.getResource(fxmlPath))
            val root = loader.load<javafx.scene.Parent>()
            val scene = Scene(root)
            applyGlobalStyle(scene)
            val stage = overlayPane.scene.window as Stage
            stage.scene = scene
        }
    }
}
