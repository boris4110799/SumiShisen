package tw.borishuang.sumishisen.minigame

import io.mockk.mockkObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MiniGameUtilTest {
    @Before
    fun setup() {
        mockkObject(MiniGameUtil)
    }

    @Test
    fun test_text2Hint_dash() {
        val output = MiniGameUtil.text2Hint("-")

        assertEquals("x", output)
    }

    @Test
    fun test_text2Hint_box() {
        val output = MiniGameUtil.text2Hint("Box")

        assertEquals("z", output)
    }

    @Test
    fun test_text2Hint_a() {
        val output = MiniGameUtil.text2Hint("A")

        assertEquals("a", output)
    }

    @Test
    fun test_hint2Text_x() {
        val output = MiniGameUtil.hint2Text('x')

        assertEquals("-", output)
    }

    @Test
    fun test_hint2Text_z() {
        val output = MiniGameUtil.hint2Text('z')

        assertEquals("Box", output)
    }

    @Test
    fun test_hint2Text_b() {
        val output = MiniGameUtil.hint2Text('b')

        assertEquals("B", output)
    }

    @Test
    fun test_combineData_same() {
        val problem1 = "aabcdefzaxgehijkhlmmbmbelneoffzkfonddcomhhajgxccbizkkligjgnojndzil"
        val problem2 = "bbacdefzbxgehijkhlmmamaelneoffzkfonddcomhhbjgxccaizkkligjgnojndzil"
        val output = MiniGameUtil.combineData(listOf(problem1), listOf(problem2))

        assertEquals(output, listOf(problem1))
    }

    @Test
    fun test_combineData_diff() {
        val problem1 = "aabcdefzaxgehijkhlmmbmbelneoffzkfonddcomhhajgxccbizkkligjgnojndzil"
        val problem2 = "aabczdedfzzgbhicjkljbmadannihnflihjxojlmgkmezlemdkceiohggbnfofcozk"
        val output = MiniGameUtil.combineData(listOf(problem1), listOf(problem2))

        assertEquals(output, listOf(problem1, problem2))
    }

    @Test
    fun test_formatData() {
        val problem = "bbaczdedfzzgahicjkljambdbnnihnflihjxojlmgkmezlemdkceiohgganfofcozk"
        val answer = "aabczdedfzzgbhicjkljbmadannihnflihjxojlmgkmezlemdkceiohggbnfofcozk"
        val output = MiniGameUtil.formatData(problem)

        assertEquals(output, answer)
    }
}