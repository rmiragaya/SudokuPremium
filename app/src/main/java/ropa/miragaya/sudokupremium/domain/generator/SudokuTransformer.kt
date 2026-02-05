package ropa.miragaya.sudokupremium.domain.generator

import ropa.miragaya.sudokupremium.domain.model.Board
import ropa.miragaya.sudokupremium.domain.model.Cell
import javax.inject.Inject
import kotlin.random.Random

class SudokuTransformer @Inject constructor() {

    fun transform(originalBoard: Board): Board {
        var grid = originalBoard.toGridList()

        val mapping = (1..9).toList().shuffled()
        grid = grid.map { row ->
            row.map { value -> if (value != null) mapping[value - 1] else null }
        }

        repeat(Random.nextInt(4)) {
            grid = rotate90(grid)
        }

        if (Random.nextBoolean()) grid = grid.reversed()
        if (Random.nextBoolean()) grid = grid.map { it.reversed() }

        grid = swapRowsWithinBands(grid)
        grid = swapColsWithinBands(grid)

        grid = swapBands(grid)
        grid = swapStacks(grid)

        return listToBoard(grid)
    }

    private fun rotate90(matrix: List<List<Int?>>): List<List<Int?>> {
        val size = 9
        val newMatrix = MutableList(size) { MutableList<Int?>(size) { null } }
        for (r in 0 until size) {
            for (c in 0 until size) {
                newMatrix[c][size - 1 - r] = matrix[r][c]
            }
        }
        return newMatrix
    }

    private fun swapRowsWithinBands(matrix: List<List<Int?>>): List<List<Int?>> {
        val newMatrix = matrix.toMutableList()
        for (band in 0..2) {
            val start = band * 3
            if (Random.nextBoolean()) {
                val rowA = start + Random.nextInt(3)
                val rowB = start + Random.nextInt(3)

                val temp = newMatrix[rowA]
                newMatrix[rowA] = newMatrix[rowB]
                newMatrix[rowB] = temp
            }
        }
        return newMatrix
    }

    private fun swapColsWithinBands(matrix: List<List<Int?>>): List<List<Int?>> {

        var temp = rotate90(matrix)
        temp = swapRowsWithinBands(temp)
        repeat(3) { temp = rotate90(temp) }
        return temp
    }

    private fun swapBands(matrix: List<List<Int?>>): List<List<Int?>> {
        val bands = mutableListOf(
            matrix.subList(0, 3),
            matrix.subList(3, 6),
            matrix.subList(6, 9)
        )
        bands.shuffle()
        return bands.flatten()
    }

    private fun swapStacks(matrix: List<List<Int?>>): List<List<Int?>> {
        var temp = rotate90(matrix)
        temp = swapBands(temp)
        repeat(3) { temp = rotate90(temp) }
        return temp
    }

    private fun Board.toGridList(): List<List<Int?>> {
        val grid = mutableListOf<List<Int?>>()

        for (r in 0 until 9) {
            val rowValues = (0 until 9).map { c ->
                this.cells.first { it.row == r && it.col == c }.value
            }
            grid.add(rowValues)
        }
        return grid
    }

    private fun listToBoard(grid: List<List<Int?>>): Board {
        val newCells = mutableListOf<Cell>()
        for (r in 0 until 9) {
            for (c in 0 until 9) {
                val value = grid[r][c]
                val id = r * 9 + c
                val box = (r / 3) * 3 + (c / 3)
                newCells.add(Cell(id, r, c, box, value, isGiven = value != null))
            }
        }
        return Board(newCells)
    }
}