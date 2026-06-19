package ropa.miragaya.sudokupremium.diagnostics

import kotlin.random.Random

object SupportCodeGenerator {
    fun generate(random: Random = Random.Default): String {
        val firstGroup = randomCodePart(length = 4, random = random)
        val secondGroup = randomCodePart(length = 2, random = random)
        return "SM-$firstGroup-$secondGroup"
    }

    private fun randomCodePart(length: Int, random: Random): String {
        return buildString {
            repeat(length) {
                append(readableAlphabet[random.nextInt(readableAlphabet.length)])
            }
        }
    }

    private const val readableAlphabet = "23456789ABCDEFGHJKMNPQRSTUVWXYZ"
}
