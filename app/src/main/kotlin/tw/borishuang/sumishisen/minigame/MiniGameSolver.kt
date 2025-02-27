package tw.borishuang.sumishisen.minigame

import tw.borishuang.sumishisen.matrix.Matrix2D

/**
 * The solver of mini game.
 */
class MiniGameSolver(inputStr: String) {
    companion object {
        val xzList = listOf('x', 'z')
        private val dx = intArrayOf(0, -1, 0, 1)
        private val dy = intArrayOf(1, 0, -1, 0)
    }

    /** The matrix that transform from the input string. */
    private val inputMatrix = Matrix2D(8, 13) { i, j ->
        if (i in 1..6 && j in 1..11) inputStr[(i - 1) * 11 + j - 1]
        else 'x'
    }

    /** The matrix that will be used to compute. */
    private var computeMatrix = inputMatrix.copy()

    /** The queue of next pending point. */
    private var pendingQueue = mutableListOf<Pair<Int, Int>>()

    /** The queue of answer. */
    private var answerQueue = mutableListOf<Pair<Int, Int>>()

    /** The x-coordinate of the point that will be connected. */
    private var rx = 0

    /** The y-coordinate of the point that will be connected. */
    private var ry = 0

    /**
     * Return answer after calculate the problem.
     */
    fun getAnswer(): List<Pair<Int, Int>> = answerQueue

    /**
     * The top function of computing the answer.
     * @return
     * - True: The problem can be solved.
     * - False: Can't be solved.
     */
    fun cal(): Boolean {
        forMiniGame { i, j ->
            // New start point
            pendingQueue.add(Pair(i, j))
            answerQueue.clear()

            // Recursive pending
            while (pendingQueue.isNotEmpty()) {
                // Extract the point
                val (x, y) = pendingQueue.removeAt(0)

                // If the point is box or blank, continue
                if (computeMatrix[x, y] in xzList) continue

                repeat(4) { dir ->
                    if (check(x, y, computeMatrix[x, y], dir, 0)) {
                        connect(x, y)
                        return@repeat
                    }
                }
            }

            if (isEmpty()) return true

            // Next loop with new matrix
            computeMatrix = inputMatrix.copy()
        }
        return false
    }

    /**
     * Check whether the given sumi can connect to another same sumi.
     * @param x x-coordinate of start position.
     * @param y y-coordinate of start position.
     * @param value sumi value (a~p).
     * @param dir direction that currently check for.
     * @param corner how many corner already used now.
     * @return
     * - True: Found the sumi that can connect.
     * - False: Can't find.
     */
    private fun check(x: Int, y: Int, value: Char, dir: Int, corner: Int): Boolean {
        // Three corners are not allowed
        if (corner == 3) return false

        // Calculate the next position
        val posX = x + dx[dir]
        val posY = y + dy[dir]

        // Out of bound
        if (posX !in 0..7 || posY !in 0..12) return false

        // Continue to check if the point is blank
        if (computeMatrix[posX, posY] == 'x') {
            // Go straight
            if (check(posX, posY, value, dir, corner)) return true
            // Turn left
            if (check(posX, posY, value, (dir + 1) % 4, corner + 1)) return true
            // Turn right
            if (check(posX, posY, value, (dir + 3) % 4, corner + 1)) return true
        }
        else if (computeMatrix[posX, posY] == value) {
            rx = posX
            ry = posY
            return true
        }
        return false
    }

    /**
     * Connect two point and add the pending sumi.
     * @param x x-coordinate of point.
     * @param y y-coordinate of point.
     */
    private fun connect(x: Int, y: Int) {
        computeMatrix[x, y] = 'x'
        computeMatrix[rx, ry] = 'x'
        answerQueue.add(Pair(x, y))
        answerQueue.add(Pair(rx, ry))

        // Add the point on the cross line of point x, y
        for (i in 0 until 4) {
            var posX = x
            var posY = y

            while (true) {
                posX += dx[i]
                posY += dy[i]

                if (posX !in 1..6 || posY !in 1..11) break
                if (computeMatrix[posX, posY] !in xzList) {
                    pendingQueue.add(Pair(posX, posY))
                }
            }
        }

        // Add the point on the cross line of point rx, ry
        for (i in 0 until 4) {
            var posX = rx
            var posY = ry

            while (true) {
                posX += dx[i]
                posY += dy[i]

                if (posX !in 1..6 || posY !in 1..11) break
                if (computeMatrix[posX, posY] !in xzList) {
                    pendingQueue.add(Pair(posX, posY))
                }
            }
        }
    }

    /**
     * Check the problem is solved or not.
     * @return
     * - True: All sumi has been connected.
     * - False: There are sumi remained.
     */
    private fun isEmpty(): Boolean {
        forMiniGame { i, j ->
            if (computeMatrix[i, j] !in xzList) return false
        }
        return true
    }
}