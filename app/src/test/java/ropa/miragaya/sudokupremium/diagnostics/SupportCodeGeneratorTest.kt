package ropa.miragaya.sudokupremium.diagnostics

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SupportCodeGeneratorTest {

    @Test
    fun `generated support code uses readable grouped format`() {
        val code = SupportCodeGenerator.generate(Random(seed = 7))

        assertTrue(code.matches(Regex("SM-[A-Z2-9]{4}-[A-Z2-9]{2}")))
    }

    @Test
    fun `generated support code avoids ambiguous characters`() {
        repeat(200) { seed ->
            val code = SupportCodeGenerator.generate(Random(seed))

            ambiguousCharacters.forEach { character ->
                assertTrue("$code contains ambiguous character $character", character !in code)
            }
        }
    }

    @Test
    fun `support code length is stable`() {
        val code = SupportCodeGenerator.generate(Random(seed = 11))

        assertEquals(10, code.length)
    }

    private companion object {
        val ambiguousCharacters = setOf('0', '1', 'I', 'L', 'O')
    }
}
