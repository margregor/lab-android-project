#include "raymob.h"
#include "rlgl.h"
#include "raymath.h"
#include "BirdGame.hpp"
#include "ObstacleGame.hpp"
#include <string>
#include <variant>
#include <GLES3/gl32.h>

static int windowWidth = 0;
static int windowHeight = 0;

static int gameId = 0;

bool shouldStop = false;

void setPaused() {
    shouldStop = true;
}

struct stateUpdater {
    Vector2 windowSize;

    void operator()(BirdGameState& state) const {
        BirdGameRun(windowSize, state);
    }

    void operator()(ObstacleGameState& state) const {
        ObstacleGameRun(windowSize, state);
    }

    void operator()(std::monostate&) const {}
};

int main()
{
    gameId = 0;
    InitWindow(0, 0, "labap");
    SetTargetFPS(60);
    shouldStop = false;

    std::variant<std::monostate, BirdGameState, ObstacleGameState> state;

    InitCallBacks();
    SetOnStopCallBack(setPaused);
    SetOnPauseCallBack(setPaused);
    auto visitor = stateUpdater{{0, 0}};

    while (!WindowShouldClose() && !shouldStop)
    {
        BeginDrawing();

        if (gameId != 0 && std::holds_alternative<std::monostate>(state))
        {
            switch (gameId) {
                case 1:
                    state = BirdGameState();
                    break;
                case 2:
                    state = ObstacleGameState();
                    break;
                default:
                    state = std::monostate();
            }
        }

        visitor.windowSize = {
                static_cast<float>(windowWidth),
                static_cast<float>(windowHeight)
        };
        std::visit(visitor, state);

        if (shouldStop) break;
        EndDrawing();
    }

    CloseWindow();

    return 0;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_labpwr_labap_NativeLoader_onWindowResized([[maybe_unused]] JNIEnv *env,
                             [[maybe_unused]] jobject obj,
                                              jint width,
                                              jint height) {
    windowWidth = width;
    windowHeight = height;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_labpwr_labap_NativeLoader_setGameId([[maybe_unused]] JNIEnv *env,
                                               [[maybe_unused]] jobject obj,
                                                                jint id) {
    gameId = id;
}