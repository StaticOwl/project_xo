package com.projectx.utils

import com.projectx.GameApp
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.Window
import javafx.util.Duration

object GameUtils {
    fun setFixedWindowSize(stage: Stage?) {
        if (stage != null) {
            // If the stage is already available, set the size immediately
            stage.width = 1200.0
            stage.height = 800.0
            stage.isResizable = false
        } else {
            // If stage is not yet available, use a Timeline to check periodically
            val timeline = Timeline()
            timeline.cycleCount = Timeline.INDEFINITE
            timeline.keyFrames.add(KeyFrame(Duration.millis(100.0), { _ ->
                val currentStage = javafx.stage.Window.getWindows().firstOrNull { it.isShowing } as? Stage
                if (currentStage != null) {
                    setFixedWindowSize(currentStage) // Recursively call once stage is available
                    timeline.stop() // Stop checking once done
                }
            }))
            timeline.play()
        }
    }

    fun applyGlobalStyle(scene: Scene) {
        scene.stylesheets.add(GameApp::class.java.getResource("/style.css")!!.toExternalForm())
    }

    fun buildButtonStyle(color: String, alpha: Double = 0.85): String {
        return if (color.startsWith("#")) {
            """
        -fx-border-color: $color;
        -fx-effect: dropshadow(gaussian, $color, 10, 0.6, 0, 0);
        """.trimIndent()
        } else {
            """
        -fx-border-color: rgba($color, $alpha);
        -fx-effect: dropshadow(gaussian, rgba($color, $alpha), 10, 0.6, 0, 0);
        """.trimIndent()
        }
    }

}
