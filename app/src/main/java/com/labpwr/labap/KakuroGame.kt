package com.labpwr.labap

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import android.os.Bundle
import android.util.Log
import com.gitlab.mvysny.konsumexml.childInt
import com.gitlab.mvysny.konsumexml.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.labpwr.labap.ui.theme.LabapTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.MutableLiveData
import com.gitlab.mvysny.konsumexml.konsumeXml
import kotlin.math.roundToInt


data class KakuroBoard(val rows: List<KakuroRow>, val width: Int, val height: Int) {
    var selectedCell: KakuroCell? = null
    var selectedCellX: MutableLiveData<Int?> = MutableLiveData(null)
    var selectedCellY: MutableLiveData<Int?> = MutableLiveData(null)
    var complete: MutableLiveData<Boolean> = MutableLiveData(false)

    companion object {
        fun xml(k: Konsumer): KakuroBoard {
            k.checkCurrent("root")
            return KakuroBoard(
                k.children("board") { KakuroRow.xml(this)},
                k.childInt("width"),
                k.childInt("height"))
        }
    }
}

data class KakuroRow(val cells: List<KakuroCell>) {
    companion object {
        fun xml(k: Konsumer): KakuroRow {
            k.checkCurrent("board")
            return KakuroRow(k.children("cell") { KakuroCell.xml(this)})
        }
    }
}


data class KakuroCell(val type: Int, val horizontalClue: Int?, val verticalClue: Int?, var value: MutableLiveData<Int?>) {
    companion object {
        fun xml(k: Konsumer): KakuroCell {
            k.checkCurrent("cell")
            val type = k.childInt("type")
            return KakuroCell(
                type,
                k.childIntOrNull("horizontalClue"),
                k.childIntOrNull("verticalClue"),
                if (type==1) MutableLiveData(0) else MutableLiveData(null)
            )
        }
    }
}

@Composable
fun KakuroBoardComponent(board : KakuroBoard, cellSize : Dp, scale: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(unbounded = true)) {
        board.rows.forEachIndexed { y, row ->
            Row(modifier = Modifier
                .wrapContentWidth(unbounded = true)

                .graphicsLayer()) {
                row.cells.forEachIndexed { x, cell ->
                    when (cell.type) {
                        0 -> Box(
                            Modifier
                                .requiredSize(cellSize * scale)
                                .border(0.25.dp * scale, Color.Black)
                        ){
                            Canvas (
                                Modifier
                                    .requiredSize(cellSize * scale)
                                    .background(Color.Black)
                                    .clipToBounds()
                            ) {
                                if (cell.horizontalClue != 0 || cell.verticalClue != 0) {
                                    drawLine(
                                        color = Color.White,
                                        start = Offset(-size.width, -size.height),
                                        end = Offset(2 * size.width, 2 * size.height),
                                        cap = StrokeCap.Butt,
                                        strokeWidth = 5.0f * scale
                                    )
                                }
                            }
                            if (cell.verticalClue != 0) {
                                Text(
                                    cell.verticalClue.toString(),
                                    color = Color.White,
                                    fontSize = (LocalDensity.current.density * (cellSize * scale * 0.16f).value).sp,
                                    style = TextStyle(
                                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                )
                            }
                            if (cell.horizontalClue != 0) {
                                Text(
                                    cell.horizontalClue.toString(),
                                    color = Color.White,
                                    fontSize = (LocalDensity.current.density * (cellSize * scale * 0.16f).value).sp,
                                    style = TextStyle(
                                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                        1 -> {
                            val cellValue = cell.value.observeAsState().value
                            val selectedX = board.selectedCellX.observeAsState().value
                            val selectedY = board.selectedCellY.observeAsState().value
                            val boardComplete = board.complete.observeAsState().value

                            var color = if (x == selectedX && y == selectedY) Color.Yellow else Color.White
                            if (boardComplete!!) {
                                color = Color.Green
                            }

                            Box(
                                Modifier
                                    .requiredSize(cellSize * scale)
                                    .background(color)
                                    .border(0.25.dp * scale, Color.Black)
                                    .clickable(onClick = {
                                        board.selectedCell = cell
                                        board.selectedCellX.value = x
                                        board.selectedCellY.value = y
                                    }),
                                contentAlignment = Alignment.Center
                            ) {
                                if (cellValue != 0) {
                                    Text(
                                        cellValue.toString(),
                                        fontSize = (LocalDensity.current.density * (cellSize * scale * 0.3f).value).sp,
                                        color = Color.Black,
                                        style = TextStyle(
                                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                                        ),
                                        modifier = Modifier.align(Alignment.Center),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun checkBoard(board: KakuroBoard) : Boolean {
    board.rows.forEachIndexed {
            y, row ->
        row.cells.forEachIndexed {
                x, cell ->
            if (cell.type == 0) {
                if (cell.horizontalClue!! > 0) {
                    val runValues : MutableSet<Int> = mutableSetOf()
                    var i = x+1
                    while (i < board.width && row.cells[i].type == 1) {
                        if (row.cells[i].value.value == 0) {
                            i++
                            continue
                        }
                        if (runValues.contains(row.cells[i].value.value)) {
                            return false
                        }
                        runValues.add(row.cells[i].value.value!!)
                        i++
                    }
                    if (runValues.sum() != cell.horizontalClue) {
                        return false
                    }
                }
                if (cell.verticalClue!! > 0) {
                    val runValues : MutableSet<Int> = mutableSetOf()
                    var i = y+1
                    while (i < board.height && board.rows[i].cells[x].type == 1) {
                        if (board.rows[i].cells[x].value.value == 0) {
                            i++
                            continue
                        }
                        if (runValues.contains(board.rows[i].cells[x].value.value)) {
                            return false
                        }
                        runValues.add(board.rows[i].cells[x].value.value!!)
                        i++
                    }
                    if (runValues.sum() != cell.verticalClue) {
                        return false
                    }
                }
            }
        }
    }
    return true
}

class KakuroGame : ComponentActivity() {
    lateinit var db : SQLiteDatabase

    override fun onDestroy() {
        db.close();
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = GameSaveDataDbHelper(this).writableDatabase;

        val boardToLoad = intent.getStringExtra("board")
        val boardId = boardToLoad!!.split('/').last().split(".").first()
        Log.d("KAKURO", "boardToLoad $boardToLoad")
        val text = assets.open(boardToLoad!!)
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }

        val board = text.konsumeXml().use {
            k -> k.child("root") {
                KakuroBoard.xml(this)
            }
        }



        with(db.query("KakuroInputs", arrayOf("x_pos", "y_pos", "value"), "board_id = ?", arrayOf(boardId) , null, null, null)) {
            while (moveToNext()) {
                board.rows[getInt(1)].cells[getInt(0)].value.value = getInt(2)
            }
            close()
        }

        with(db.query("KakuroCompletedBoards", arrayOf("board_id"), "board_id = ?", arrayOf(boardId) , null, null, null)) {
            while (moveToNext()) {
                board.complete.value = true
            }
            close()
        }

        enableEdgeToEdge()
        setContent {
            LabapTheme {
                val cellSize = 50.dp
                val boardWidth = board.width * cellSize
                val boardHeight = board.height * cellSize

                val screenHeight = LocalWindowInfo.current.containerDpSize.height
                val screenWidth = LocalWindowInfo.current.containerDpSize.width

                val scale = minOf(screenWidth / boardWidth, screenHeight / boardHeight)

                Scaffold  (
                    modifier = Modifier.background(Color.White),
                    bottomBar = {
                        Surface (modifier = Modifier.background(Color.White).fillMaxWidth()) {
                            Column (
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(modifier =
                                    Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),

                                ) {
                                    (0..4).forEach {
                                        i -> Button(modifier =
                                        Modifier
                                            .background(Color.White)
                                            .size(50.dp)
                                            .aspectRatio(1f),
                                        onClick = {
                                            if (!board.complete.value!!) {
                                                board.selectedCell?.value?.value = i
                                                val values = ContentValues()
                                                values.put("board_id", boardId)
                                                values.put("x_pos", board.selectedCellX.value)
                                                values.put("y_pos", board.selectedCellY.value)
                                                values.put("value", i)
                                                db.insertWithOnConflict("KakuroInputs", null, values, CONFLICT_REPLACE)
                                            }
                                                  },
                                        contentPadding = PaddingValues(0.dp)
                                        ) {
                                                Text(if (i > 0) i.toString() else "Del")
                                        }
                                    }
                                    Button(modifier =
                                        Modifier
                                            .background(Color.White)
                                            .height(50.dp)
                                            .width(75.dp),
                                        onClick = {
                                            if (!board.complete.value!! && checkBoard(board)) {
                                                board.complete.value = true
                                                val values = ContentValues()
                                                values.put("board_id", boardId)
                                                values.put("width", board.width)
                                                values.put("height", board.height)
                                                db.insertWithOnConflict("KakuroCompletedBoards", null, values, CONFLICT_REPLACE)
                                            }
                                        },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Check")
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier =
                                    Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),

                                    ) {
                                        (5..9).forEach {
                                                i -> Button(modifier =
                                            Modifier
                                                .background(Color.White)
                                                .size(50.dp)
                                                .aspectRatio(1f),
                                            onClick = {
                                                if (!board.complete.value!!) {
                                                    board.selectedCell?.value?.value = i
                                                    val values = ContentValues()
                                                    values.put("board_id", boardId)
                                                    values.put("x_pos", board.selectedCellX.value)
                                                    values.put("y_pos", board.selectedCellY.value)
                                                    values.put("value", i)
                                                    db.insertWithOnConflict("KakuroInputs", null, values, CONFLICT_REPLACE)
                                                }
                                                      },
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(i.toString())
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(55.dp))
                            }
                        }
                }) { paddingValues ->
                    Column (Modifier.background(Color.White).padding(paddingValues)) {
                        var zoom by remember { mutableFloatStateOf(1f) }
                        var offset by remember { mutableStateOf(Offset.Zero) }


                        val state = rememberTransformableState { zoomChange, offsetChange, _ ->
                            // note: scale goes by factor, not an absolute difference, so we need to multiply it
                            // for this example, we don't allow downscaling, so cap it to 1f
                            zoom *= zoomChange
                            offset += offsetChange
                        }


                        Box(
                            Modifier
                            .transformable(state)
                            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        zoom = 1.0f
                                        offset = Offset.Zero
                                    }
                                )
                            }
                        ) {
                            KakuroBoardComponent(board, cellSize, scale * zoom)
                        }
                    }
                }


            }
        }
    }
}