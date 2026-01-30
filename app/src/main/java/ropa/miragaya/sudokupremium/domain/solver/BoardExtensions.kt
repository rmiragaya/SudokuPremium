package ropa.miragaya.sudokupremium.domain.solver

import ropa.miragaya.sudokupremium.domain.model.Board

// calcula los posibles números (1-9) para una celda específica
fun Board.calculateCandidates(cellId: Int): Set<Int> {
    val targetCell = cells.find { it.id == cellId } ?: return emptySet()

    if (targetCell.value != null) return emptySet()

    val candidates = (1..9).toMutableSet()

    val peerIds = this.getPeers(cellId)

    peerIds.forEach { peerId ->
        val peerValue = cells[peerId].value
        if (peerValue != null) {
            candidates.remove(peerValue)
        }
    }

    return candidates
}