package com.projectx

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.stage.Stage

class App : Application() {
    override fun start(stage: Stage) {
        val button = Button("Start Game")
        button.setOnAction { println("Game Started!") }

        val root = VBox(button)
        val scene = Scene(root, 400.0, 300.0)

        stage.scene = scene
        stage.title = "Project X"
        stage.show()
    }
}

fun main() {
    Application.launch(App::class.java)
}
