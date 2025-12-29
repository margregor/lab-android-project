//
// Created by Marcin on 03.12.2025.
//

#include "BirdGame.hpp"
#include "rlgl.h"
#include "raymath.h"
#include <string>

void BirdGameRun(Vector2 windowSize, BirdGameState& state) {

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

    DrawTextPro(
            GetFontDefault(),
            text.c_str(),
            windowSize*0.5f,
            textMeasure*0.5f,
            fmodf(++state.angle, 360),
            fontSize,
            fontSpacing,
            BLACK);

}
