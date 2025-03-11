package com.projectx.controller

import com.projectx.utils.GameUtils.setFixedWindowSize
import javafx.animation.*
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.stage.Stage
import javafx.util.Duration

class MainController {


    @FXML
    private lateinit var welcomeLabel: Label

    @FXML
    private lateinit var subtitleLabel: Label

    @FXML
    private lateinit var startButton: Button

    @FXML
    private lateinit var nayButton: Button

    @FXML
    fun initialize() {

        Platform.runLater {
            (startButton.scene?.window as? Stage)?.let {
                setFixedWindowSize(it)
            }
        }

        startButton.isDisable = true
        startButton.style = buildButtonStyle("0, 255, 255")
        startButton.opacity = 0.0

        nayButton.isDisable = true
        nayButton.style = buildButtonStyle("255, 0, 0")
        nayButton.opacity = 0.0

        subtitleLabel.opacity = 0.0

        val fadeInWelcome = FadeTransition(Duration.seconds(2.0), welcomeLabel).apply {
            fromValue = 0.0
            toValue = 1.0
        }

        val moveUpWelcome = TranslateTransition(Duration.seconds(1.0), welcomeLabel).apply {
            byY = -50.0
        }

        val fadeInSubs = FadeTransition(Duration.seconds(2.0), subtitleLabel).apply {
            fromValue = 0.0
            toValue = 1.0
        }

        val moveUpSubs = TranslateTransition(Duration.seconds(1.0), subtitleLabel).apply {
            moveUpWelcome.play()
            byY = -30.0
        }

        val buttonFadeIn = FadeTransition(Duration.seconds(1.0), startButton).apply {
            fromValue = 0.0
            toValue = 1.0
        }

        moveUpSubs.setOnFinished {
            startButton.isDisable = false
            nayButton.isDisable = false

            val fadeInYay = FadeTransition(Duration.seconds(1.0), startButton).apply {
                fromValue = 0.0
                toValue = 1.0
            }

            val fadeInNay = FadeTransition(Duration.seconds(1.0), nayButton).apply {
                fromValue = 0.0
                toValue = 1.0
            }

            fadeInYay.play()
            fadeInNay.play()
        }

        nayButton.setOnAction {
            val fadeOutSub = FadeTransition(Duration.seconds(1.0), subtitleLabel).apply {
                fromValue = 1.0
                toValue = 0.0
            }

            fadeOutSub.setOnFinished {
                subtitleLabel.text = "Let's play please?"

                val fadeInSub = FadeTransition(Duration.seconds(1.0), subtitleLabel).apply {
                    fromValue = 0.0
                    toValue = 1.0
                }

                fadeInSub.setOnFinished {
                    val fadeInYay = FadeTransition(Duration.seconds(1.0), startButton).apply {
                        fromValue = 0.0
                        toValue = 1.0
                    }
                    startButton.isDisable = false
                    startButton.layoutX = 550.0
                    fadeInYay.play()
                }

                fadeInSub.play()
            }

            val fadeOutYay = FadeTransition(Duration.seconds(0.5), startButton).apply {
                fromValue = 1.0
                toValue = 0.0
            }
            val fadeOutNay = FadeTransition(Duration.seconds(0.5), nayButton).apply {
                fromValue = 1.0
                toValue = 0.0
            }

            fadeOutYay.play()
            fadeOutNay.play()
            startButton.isDisable = true
            nayButton.isDisable = true

            fadeOutSub.play()
        }

        val sequence = SequentialTransition(fadeInWelcome, moveUpWelcome, fadeInSubs, moveUpSubs, buttonFadeIn)
        sequence.play()

        startButton.setOnAction {
            switchToWordle()
        }

        nayButton.setOnMouseEntered {
            nayButton.style = buildButtonStyle("0,255,0")
        }
        nayButton.setOnMouseExited {
            nayButton.style = buildButtonStyle("255,0,0")
        }


        startButton.setOnMouseEntered {
            startButton.style = buildButtonStyle("0, 255, 0")
        }

        startButton.setOnMouseExited {
            startButton.style = buildButtonStyle("0, 255, 255")
        }

    }

    private fun switchToWordle() {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/WordleView.fxml"))
        val scene = Scene(fxmlLoader.load())
        val stage = startButton.scene.window as Stage
        stage.scene = scene
    }

    private fun buildButtonStyle(rgb: String): String {
        return """
        -fx-border-color: rgba($rgb, 0.85);
        -fx-effect: dropshadow(gaussian, rgba($rgb, 0.85), 10, 0.6, 0, 0);
    """.trimIndent()
    }

}
