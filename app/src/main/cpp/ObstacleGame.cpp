//
// Created by Marcin on 03.12.2025.
//

#include "ObstacleGame.hpp"
#include <rlgl.h>
#include <raymath.h>
#include <string>

void ObstacleGameRun(Vector2 windowSize, ObstacleGameState& state) {
    rlDrawRenderBatchActive();
    rlMatrixMode(RL_PROJECTION);
    rlLoadIdentity();
    rlOrtho(0, windowSize.x, windowSize.y, 0, -1, 1);
    rlMatrixMode(RL_MODELVIEW);
    rlLoadIdentity();

    ClearBackground(RAYWHITE);
    static const std::string text = "Congrats! You created your first window!";
    float fontSize = 50;
    float fontSpacing = 2;
    auto textMeasure = MeasureTextEx(
            GetFontDefault(),
            text.c_str(),
            fontSize,
            fontSpacing
    );

    if (!state.acceleratorEnabled)
    {
        InitSensorManager();
        EnableSensor(SENSOR_ACCELEROMETER);
        state.acceleratorEnabled = true;
    }

    auto axes = GetAccelerotmerAxis();

    state.angle -= axes.x;
    float preModAngle = state.angle;
    state.angle = fmodf(state.angle, 360);
    if (preModAngle != state.angle) {
        state.score++;
        state.highscoreUpdater("obstacleGame", state.score);
    }


    DrawTextPro(
            GetFontDefault(),
            text.c_str(),
            windowSize*0.5f,
            textMeasure*0.5f,
            state.angle,
            fontSize,
            fontSpacing,
            BLACK);
    std::string scoreText = "Score: " + std::to_string(state.score);
    DrawTextPro(
            GetFontDefault(),
            scoreText.c_str(),
            windowSize*0.1f,
            {0,0},
            0,
            fontSize,
            fontSpacing,
            BLACK);
}

void ObstacleGameClose(Vector2 windowSize, ObstacleGameState &state) {
    if (state.acceleratorEnabled)
    {
        DisableSensor(SENSOR_ACCELEROMETER);
    }
}
