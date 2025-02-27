package tw.borishuang.sumishisen.matrix

import org.junit.Assert.assertEquals
import org.junit.Test

class Matrix2DTest {
    @Test
    fun constructor_with_size() {
        val matrix = Matrix2D(3, 4)

        assertEquals(3, matrix.rowSize)
        assertEquals(3, matrix.colSize)
        assertEquals("[4, 4, 4]\n[4, 4, 4]\n[4, 4, 4]\n", matrix.toString())
    }

    @Test
    fun constructor_with_rowSize_colSize() {
        val matrix = Matrix2D(2, 3, 4)

        assertEquals(2, matrix.rowSize)
        assertEquals(3, matrix.colSize)
        assertEquals("[4, 4, 4]\n[4, 4, 4]\n", matrix.toString())
    }

    @Test
    fun test_get() {
        val matrix = Matrix2D(2, 3, 4)

        assertEquals(4, matrix[0, 0])
        assertEquals(4, matrix[0, 1])
        assertEquals(4, matrix[1, 0])
        assertEquals(4, matrix[1, 2])
    }

    @Test
    fun test_set() {
        val matrix = Matrix2D(2, 3, 4)

        matrix[0, 0] = 1
        matrix[0, 1] = 2
        matrix[1, 0] = 3
        matrix[1, 2] = 5

        assertEquals("[1, 2, 4]\n[3, 4, 5]\n", matrix.toString())
    }

    @Test
    fun test_copy() {
        val matrix = Matrix2D(2, 3, 4)
        val copy = matrix.copy()

        assertEquals(matrix.toString(), copy.toString())
        assert(matrix.hashCode() == copy.hashCode())
    }

    @Test
    fun test_getRow() {
        val matrix = Matrix2D(3, 4) { row, col -> row + col }
        val rowList = matrix.getRow(2)

        assertEquals(listOf(2, 3, 4, 5), rowList)
    }

    @Test
    fun test_getCol() {
        val matrix = Matrix2D(3, 4) { row, col -> row + col }
        val colList = matrix.getCol(2)

        assertEquals(listOf(2, 3, 4), colList)
    }

    @Test
    fun test_contains() {
        val matrix = Matrix2D(2, 3, 4)

        assertEquals(true, matrix.contains(4))
        assertEquals(false, matrix.contains(5))
    }
}