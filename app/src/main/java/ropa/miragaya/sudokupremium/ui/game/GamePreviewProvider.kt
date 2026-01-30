package ropa.miragaya.sudokupremium.ui.game

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Cell

class GamePreviewProvider : PreviewParameterProvider<GameUiState> {

    private val dummyBoard = Board(
        cells = List(81) { index ->
            Cell(
                id = index,
                row = index / 9,
                col = index % 9,
                box = (index / 9 / 3) * 3 + (index % 9 / 3),
                value = if (index % 7 == 0) (index % 9) + 1 else null, // algunos números
                isGiven = index % 7 == 0,
                isError = index == 10 || index == 11 // error simulado
            )
        }
    )

    override val values = sequenceOf(

        GameUiState(
            board = dummyBoard,
            selectedCellId = null
        ),

        GameUiState(
            board = dummyBoard,
            selectedCellId = 0,
            highlightedCellIds = setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 27),
            sameValueCellIds = setOf(14, 21),
            isNoteMode = false
        )
    )
}