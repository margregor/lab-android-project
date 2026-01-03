//
// Created by Marcin on 03.12.2025.
//

#include "BirdGame.hpp"
#include <rlgl.h>
#include <raymath.h>
#include <string>

static constexpr float PILLAR_SPEED = 400;
static constexpr float PILLAR_WIDTH = 200;
static constexpr float GAP_SIZE = 600;
static constexpr float MIN_PILLAR_SIZE = 100;
static constexpr float PILLAR_GAP_SIZE = 900;
static constexpr Color MY_SKYBLUE{0x87, 0xCE, 0xEB, 0xFF};
static constexpr float BIRD_ACCELERATION = 999;


void updatePillars(const Vector2& windowSize, BirdGameState &state) {
    if (state.status == GameStatus::GameOver) return;
    for (auto&& pillar : state.pillars) {
        pillar.x -= PILLAR_SPEED * GetFrameTime();
    }

    bool addedScore = false;
    for (auto it = state.pillars.begin(); it != state.pillars.end();) {
        auto& pillar = *it;
        if (pillar.x+pillar.width >= 0)
        {
            ++it;
        } else {
            it = state.pillars.erase(it);
            if (!addedScore && state.status == GameStatus::Running)
            {
                state.score++;
                state.highscoreUpdater("birdGame", state.score);
                addedScore = true;
            }
        }
    }

    const size_t pillarsTargetCount =
            static_cast<int>(windowSize.x / (PILLAR_WIDTH + PILLAR_GAP_SIZE)) * 2 + 2;

    if (state.pillars.size() < pillarsTargetCount)
    {
        auto gapPosition = static_cast<float>(
                GetRandomValue(
                        MIN_PILLAR_SIZE,
                        static_cast<int>(windowSize.y-GAP_SIZE-MIN_PILLAR_SIZE)));

        auto position = state.pillars.empty() ?
                windowSize.x :
                state.pillars.back().x + PILLAR_GAP_SIZE + PILLAR_WIDTH;

        state.pillars.emplace_back(position, -windowSize.y,
                             PILLAR_WIDTH ,gapPosition+windowSize.y);
        state.pillars.emplace_back(position, gapPosition+GAP_SIZE,
                             PILLAR_WIDTH ,windowSize.y-gapPosition-GAP_SIZE);
    }
}

void drawPillars(std::vector<Rectangle> &pillars) {
    for (const auto &pillar: pillars) {
        DrawRectangleRec(pillar, GREEN);
    }
}

void initialize(const Vector2 &windowSize, BirdGameState &state) {
    if (isnan(state.birdPosition.x)) {
        state.birdPosition.x = windowSize.x * 0.1f;
    }

    if (isnan(state.birdPosition.y)) {
        state.birdPosition.y = windowSize.y * 0.5f;
    }

    if (!IsTextureValid(state.birdTexture)) {
        state.birdTexture = LoadTexture("bird.png");
    }
}

void updateBird(BirdGameState &state) {
    if (state.status == GameStatus::GameOver) return;
    state.birdSpeed += BIRD_ACCELERATION * GetFrameTime();
    if (state.birdPosition.y >= 0 && IsGestureDetected(GESTURE_TAP))
    {
        state.birdSpeed = -600;
    }
    state.birdPosition.y += state.birdSpeed * GetFrameTime();
}

void drawBird(BirdGameState &state) {
    if (IsTextureValid(state.birdTexture)) {
        DrawTexture(state.birdTexture, state.birdPosition.x, state.birdPosition.y, WHITE);
    } else {
        DrawRectangle(state.birdPosition.x, state.birdPosition.y, 112, 88, RED);
    }
}

void drawScore(const Vector2 &windowSize, BirdGameState &state) {
    std::string scoreText = "Score: " + std::to_string(state.score);
    const float fontSize = 50;
    const float fontSpacing = 2;

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

void detectCollisions(const Vector2& windowSize, BirdGameState &state) {
    if (state.status == GameStatus::GameOver) return;

    if (state.birdPosition.y + state.birdTexture.height >= windowSize.y) {
        state.status = GameStatus::GameOver;
    }

    for (const auto &pillar: state.pillars) {
        if (CheckCollisionRecs(
                {
                        state.birdPosition.x,
                        state.birdPosition.y,
                        static_cast<float>(state.birdTexture.width),
                        static_cast<float>(state.birdTexture.height)},
                pillar)) {
            state.status = GameStatus::GameOver;
        }
    }


}

void BirdGameRun(const Vector2& windowSize, BirdGameState& state) {

    rlDrawRenderBatchActive();
    rlMatrixMode(RL_PROJECTION);
    rlLoadIdentity();
    rlOrtho(0, windowSize.x, windowSize.y, 0, -1, 1);
    rlMatrixMode(RL_MODELVIEW);
    rlLoadIdentity();


    initialize(windowSize, state);

    ClearBackground(MY_SKYBLUE);

    updatePillars(windowSize, state);

    updateBird(state);

    drawPillars(state.pillars);

    drawBird(state);

    detectCollisions(windowSize, state);

    drawScore(windowSize, state);

    if (state.status == GameStatus::GameOver)
    {
        std::string gameOverText = "Game Over";

        const float fontSize = 120;
        const float fontSpacing = 2;
        auto textMeasure = MeasureTextEx(
                GetFontDefault(),
                gameOverText.c_str(),
                fontSize,
                fontSpacing
        );

        DrawTextPro(
                GetFontDefault(),
                gameOverText.c_str(),
                windowSize*0.5f,
                textMeasure*0.5,
                0,
                fontSize,
                fontSpacing,
                BLACK);

        if (IsGestureDetected(GESTURE_TAP))
        {
            state.status = GameStatus::Running;
            state.score = 0;
            state.pillars.clear();
            state.birdPosition = {NAN, NAN};
            state.birdSpeed = 0;
        }
    }
}

void BirdGameClose(const Vector2 &windowSize, BirdGameState &state) {

}


