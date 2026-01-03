//
// Created by Marcin on 03.12.2025.
//

#ifndef LABAP_BIRDGAME_HPP
#define LABAP_BIRDGAME_HPP

#include <raymob.h>
#include <vector>
#include <functional>

enum class GameStatus {
    Running,
    GameOver
};

struct BirdGameState {
    std::vector<Rectangle> pillars;
    Vector2 birdPosition{NAN, NAN};
    Texture birdTexture;
    float birdSpeed;
    unsigned long long int score;
    GameStatus status = GameStatus::Running;
    std::function<void(std::string, unsigned long long int)>& highscoreUpdater;
};

void BirdGameRun(const Vector2& windowSize, BirdGameState& state);
void BirdGameClose(const Vector2& windowSize, BirdGameState& state);

#endif //LABAP_BIRDGAME_HPP
