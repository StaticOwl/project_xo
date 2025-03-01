package com.projectx.controller

import com.projectx.utils.GameUtils.setFixedWindowSize
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.stage.Stage

class MainController {

    @FXML
    private lateinit var startButton: Button

    @FXML
    fun initialize() {
        setFixedWindowSize(startButton.scene?.window as? Stage)
        startButton.setOnAction {
            switchToWordle()
        }
    }

    private fun switchToWordle() {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/WordleView.fxml"))
        val scene = Scene(fxmlLoader.load())
        val stage = startButton.scene.window as Stage
        stage.scene = scene
    }
}
