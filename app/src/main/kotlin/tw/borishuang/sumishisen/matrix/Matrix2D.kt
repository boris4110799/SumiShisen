package tw.borishuang.sumishisen.matrix

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class Matrix2D<T> {
    /** The row size of matrix. */
    private var rSize = 0

    /** The column size of matrix. */
    private var cSize = 0

    /** The data of matrix. */
    private val data: MutableMap<Int, MutableList<T>> = mutableMapOf()

    /** The row size of matrix. */
    val rowSize: Int
        get() = rSize

    /** The column size of matrix. */
    val colSize: Int
        get() = cSize

    /**
     * Create a square matrix with all elements initialized to [init].
     * @param size The size of matrix.
     */
    constructor(size: Int, init: T) : this(size, size, { _, _ -> init })

    /**
     * Create a square matrix with custom initialization.
     * @param size The size of matrix.
     * @param init Initialize each element with row and column index.
     */
    constructor(size: Int, init: (row: Int, col: Int) -> T) : this(size, size, init)

    /**
     * Create a matrix with all elements initialized to [init].
     * @param rowSize The row size of matrix.
     * @param colSize The column size of matrix.
     */
    constructor(rowSize: Int, colSize: Int, init: T) : this(rowSize, colSize, { _, _ -> init })

    /**
     * Create a matrix with custom initialization.
     * @param rowSize The row size of matrix.
     * @param colSize The column size of matrix.
     * @param init Initialize each element with row and column index.
     */
    constructor(rowSize: Int, colSize: Int, init: (row: Int, col: Int) -> T) {
        this.rSize = rowSize
        this.cSize = colSize

        repeat(rowSize) { row ->
            data[row] = mutableListOf<T>().apply {
                repeat(colSize) { col ->
                    add(init(row, col))
                }
            }
        }
    }

    /**
     * Return the value corresponding to the given [row] and [col].
     * @param row 0-based row index.
     * @param col 0-based column index.
     */
    operator fun get(row: Int, col: Int): T {
        checkBound(row in 0 until rSize) { "row index out of bound." }
        checkBound(col in 0 until cSize) { "column index out of bound." }

        return data[row]!![col]
    }

    /**
     * Set the [value] corresponding to the given [row] and [col].
     * @param row 0-based row index.
     * @param col 0-based column index.
     */
    operator fun set(row: Int, col: Int, value: T) {
        checkBound(row in 0 until rSize) { "row index out of bound." }
        checkBound(col in 0 until cSize) { "column index out of bound." }

        data[row]!![col] = value
    }

    /**
     * Throws an [IndexOutOfBoundsException] with the result of calling [lazyMessage] if the [value] is false.
     */
    @OptIn(ExperimentalContracts::class)
    private fun checkBound(value: Boolean, lazyMessage: () -> String) {
        contract {
            returns() implies value
        }
        if (!value) {
            val message = lazyMessage()
            throw IndexOutOfBoundsException(message)
        }
    }

    /**
     * Return the row of matrix.
     * @param row 0-based row index.
     * @return The list of row.
     */
    fun getRow(row: Int): List<T> {
        checkBound(row in 0 until rSize) { "row index out of bound." }

        return data[row]!!
    }

    /**
     * Return the column of matrix.
     * @param col 0-based column index.
     * @return The list of column.
     */
    fun getCol(col: Int): List<T> {
        checkBound(col in 0 until cSize) { "column index out of bound." }

        val list = mutableListOf<T>()
        for (row in 0 until rSize) {
            list.add(data[row]!![col])
        }
        return list
    }

    /**
     * Return a copy of matrix.
     */
    fun copy(): Matrix2D<T> {
        val matrix = Matrix2D(rSize, cSize) { row, col ->
            this[row, col]
        }
        return matrix
    }

    /**
     * Check whether the matrix contains the given [value].
     */
    fun contains(value: T): Boolean {
        for (row in 0 until rSize) {
            if (data[row]!!.contains(value)) return true
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Matrix2D<*>) {
            return false
        }
        else if (rSize != other.rSize || cSize != other.cSize) {
            return false
        }
        else {
            for (i in 0 until rSize) {
                for (j in 0 until cSize) {
                    if (data[i]!![j] != other.data[i]!![j]) return false
                }
            }
        }
        return true
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (i in 0 until rSize) {
            sb.append(data[i]!!.joinToString(separator = ", ", prefix = "[", postfix = "]\n"))
        }
        return sb.toString()
    }

    override fun hashCode(): Int {
        var result = rSize
        result = 31 * result + cSize
        result = 31 * result + data.hashCode()
        return result
    }
}