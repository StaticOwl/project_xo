package com.projectx

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

class GameApp : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/MainView.fxml"))
        val root: AnchorPane = fxmlLoader.load()

        val scene = Scene(root, 800.0, 600.0)
        scene.stylesheets.add(javaClass.getResource("/style.css")!!.toExternalForm())
        stage.title = "Project X"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(GameApp::class.java)
}