//
// Created by Marcin on 03.12.2025.
//

#ifndef LABAP_BIRDGAME_HPP
#define LABAP_BIRDGAME_HPP

#include "raymob.h"

struct BirdGameState {
    float angle = 0;
};

void BirdGameRun(Vector2 windowSize, BirdGameState& state);

#endif //LABAP_BIRDGAME_HPP
