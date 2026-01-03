//
// Created by Marcin on 03.12.2025.
//

#ifndef LABAP_OBSTACLEGAME_HPP
#define LABAP_OBSTACLEGAME_HPP

#include <raymob.h>
#include <functional>

struct ObstacleGameState {
    float angle = 0;
    bool acceleratorEnabled = false;
    unsigned long long int score = 0;
    std::function<void(std::string, unsigned long long int)>& highscoreUpdater;
};

void ObstacleGameRun(Vector2 windowSize, ObstacleGameState& state);
void ObstacleGameClose(Vector2 windowSize, ObstacleGameState& state);

#endif //LABAP_OBSTACLEGAME_HPP
