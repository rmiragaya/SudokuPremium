package ropa.miragaya.sudokupremium.domain.generator

import ropa.miragaya.sudokupremium.domain.model.Board
import kotlin.random.Random

object BoardGenerator {

    /**
     * Genera un tablero 9x9 completo y válido totalmente al azar.
     */
    fun generateFilledBoard(): Board {
        val emptyBoard = Board.createEmpty()
        val (success, filledBoard) = fillBoardRecursive(emptyBoard, 0)

        if (!success) throw IllegalStateException("No se pudo generar un tablero (raaaaaaaaaaaaro)")

        return filledBoard
    }

    private fun fillBoardRecursive(board: Board, cellIndex: Int): Pair<Boolean, Board> {
        if (cellIndex == 81) {
            return Pair(true, board)
        }

        val cell = board.cells[cellIndex]

        val numbersToTry = (1..9).shuffled(Random)

        for (number in numbersToTry) {
            if (isValidPlacement(board, cell.id, number)) {

                val newBoard = board.withCellValue(cell.id, number)

                val (solved, resultBoard) = fillBoardRecursive(newBoard, cellIndex + 1)

                if (solved) {
                    return Pair(true, resultBoard)
                }
            }
        }

        return Pair(false, board)
    }

    private fun isValidPlacement(board: Board, cellId: Int, number: Int): Boolean {
        val peers = board.getPeers(cellId)

        for (peerId in peers) {
            if (board.cells[peerId].value == number) {
                return false
            }
        }
        return true
    }
}