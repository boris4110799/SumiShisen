package tw.borishuang.sumishisen.minigame

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class MiniGameSolverTest {
    @Test
    fun test_all_problem() {
        val result = MiniGameProblems.problemList.map {
            test_problem(it)
        }.count { it }

        println("$result, ${MiniGameProblems.problemList.size}")
        assertEquals(result, MiniGameProblems.problemList.size)
    }

    @Test
    fun test_problem_failed() {
        val problem = "abcadefefbgghijklkzjgmlzjhfzdaekcnzgobmldiocmhoeohidbijzlkcanxnfma"

        val result = MiniGameSolver(problem).cal()

        assertFalse(result)
    }

    private fun test_problem(problem: String): Boolean {
        val result = MiniGameSolver(problem).cal()

        if (!result) {
            println(problem)
        }

        return result
    }
}