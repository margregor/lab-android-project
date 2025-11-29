#include "raymob.h"
#include "rlgl.h"
#include "raymath.h"
#include <string_view>
#include <string>
#include <GLES3/gl32.h>

static int windowWidth = 0;
static int windowHeight = 0;

bool shouldStop = false;

void setPaused() {
    shouldStop = true;
}

int main()
{
    InitWindow(0, 0, "labap");
    SetTargetFPS(60);
    shouldStop = false;

    float angle = 0;
    InitCallBacks();
    SetOnStopCallBack(setPaused);
    SetOnPauseCallBack(setPaused);

    //SetExitKey(KEY_BACK);

    while (!WindowShouldClose() && !shouldStop)
    {
        if (IsKeyPressed(KEY_BACK)) {
            TraceLog(LOG_WARNING, "KEY_BACK pressed");
        }
        BeginDrawing();

        Vector2 windowSize = {
                static_cast<float>(windowWidth),
                static_cast<float>(windowHeight)
        };
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
                fmodf(++angle, 360),
                fontSize,
                fontSpacing,
                BLACK);

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