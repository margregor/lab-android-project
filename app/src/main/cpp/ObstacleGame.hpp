//
// Created by Marcin on 03.12.2025.
//

#ifndef LABAP_OBSTACLEGAME_HPP
#define LABAP_OBSTACLEGAME_HPP

#include "raymob.h"

struct ObstacleGameState {
    float angle = 0;
};

void ObstacleGameRun(Vector2 windowSize, ObstacleGameState& state);

#endif //LABAP_OBSTACLEGAME_HPP
