package com.projectx.utils

import javafx.animation.KeyFrame
import javafx.animation.Timeline
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
}
