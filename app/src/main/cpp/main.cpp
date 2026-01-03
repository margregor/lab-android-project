#include <raymob.h>
#include <rlgl.h>
#include <raymath.h>
#include "BirdGame.hpp"
#include "ObstacleGame.hpp"
#include <string>
#include <variant>
#include <functional>
#include <GLES3/gl32.h>
#include <filesystem>
#include "sqlite/sqlite3.h"


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

struct stateCloser {
    Vector2 windowSize;

    void operator()(BirdGameState& state) const {
        BirdGameClose(windowSize, state);
    }

    void operator()(ObstacleGameState& state) const {
        ObstacleGameClose(windowSize, state);
    }

    void operator()(std::monostate&) const {}
};

int getHighScore(sqlite3* db, const std::string& game_name) {
    sqlite3_stmt* stmt;
    std::string sql = "SELECT score FROM Highscores WHERE game_name = '"+game_name+"';";
    int rc = sqlite3_prepare_v2(db, sql.c_str(), -1, &stmt, nullptr);
    if (rc != SQLITE_OK) {
        TraceLog(LOG_ERROR, "Error reading high score");
        return 0;
    }
    if (sqlite3_step(stmt) == SQLITE_ROW) {
        return sqlite3_column_int(stmt, 0);
    }
    return 0;
}

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

    std::filesystem::path databasePath =
            std::filesystem::path(GetAndroidApp()->activity->internalDataPath)
            .parent_path()
            / "databases"
            / "GameSaveData.db";

    sqlite3* db = nullptr;
    int rc = sqlite3_open(databasePath.c_str(), &db);
    if (rc != SQLITE_OK) {
        TraceLog(LOG_ERROR, "Error opening database");
        sqlite3_close(db);
        db = nullptr;
    }

    std::function<void(std::string, unsigned long long int)> updateHighscore =
            [db]
            (const std::string& game_name, unsigned long long int score){
        if (db) {
            if (getHighScore(db, game_name) > score) return;
            std::string sql =
                    "INSERT OR REPLACE INTO Highscores VALUES('"
                    +game_name
                    +"', "
                    +std::to_string(score)
                    +");"
                ;
            int rc = sqlite3_exec(db, sql.c_str(), nullptr, nullptr, nullptr);
            if (rc != SQLITE_OK) {
                TraceLog(LOG_ERROR, "Error updating highscore");
            }
        }
    };

    auto visitor = stateUpdater{{0, 0}};

    while (!WindowShouldClose() && !shouldStop)
    {
        BeginDrawing();

        if (gameId != 0 && std::holds_alternative<std::monostate>(state))
        {
            switch (gameId) {
                case 1:
                    state.emplace<BirdGameState>(BirdGameState{.highscoreUpdater=updateHighscore});
                    break;
                case 2:
                    state.emplace<ObstacleGameState>(ObstacleGameState{.highscoreUpdater=updateHighscore});
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

    std::visit(stateCloser{{0, 0}}, state);

    if (db) {
        sqlite3_close(db);
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