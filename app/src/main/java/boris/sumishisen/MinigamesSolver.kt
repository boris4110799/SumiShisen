package boris.sumishisen

import java.util.*

object MinigamesSolver {
	private val dx = intArrayOf(0, -1, 0, 1)
	private val dy = intArrayOf(1, 0, -1, 0)
	private val inputMatrix = Array(8) { Array(13) { 'x' } }
	private var computeMatrix = Array(8) { Array(13) { 'x' } }
	private val finishedMatrix = Array(8) { Array(13) { false } }
	private var processQueue = LinkedList<Pair<Int, Int>>()
	private var answerQueue = LinkedList<Pair<Int, Int>>()
	private var rx : Int = 0
	private var ry : Int = 0
	val xzList get() = listOf('x', 'z')
	
	/**
	 * Return answer after calculate the problem
	 */
	fun getAnswer() : LinkedList<Pair<Int, Int>> {
		return answerQueue
	}
	
	/**
	 * The top function of calculating the answer
	 * @param inputStr Game data
	 * @return True: The current board can be cleared,
	 * False: Can't be cleared
	 */
	fun cal(inputStr : String) : Boolean {
		for (i in 1..6) {
			for (j in 1..11) {
				inputMatrix[i][j] = inputStr[(i-1)*11+j-1]
			}
		}
		computeMatrix = inputMatrix.map { it.clone() }.toTypedArray()
		
		for (i in 1..6) {
			for (j in 1..11) {
				for (d in 0 until 4) {
					processQueue.addLast(Pair(i, j))
					answerQueue.clear()
					while (processQueue.isNotEmpty()) {
						val (x, y) = processQueue.removeFirst()
						if (computeMatrix[x][y] in xzList) continue
						for (k in 0 until 8) {
							for (l in 0 until 13) {
								finishedMatrix[k][l] = false
							}
						}
						var k = d
						var l = 0
						while (l < 4) {
							if (check(x, y, computeMatrix[x][y], k%4, 0)) {
								connect(x, y)
								break
							}
							k += 1
							l += 1
						}
					}
					if (isEmpty()) return true
					computeMatrix = inputMatrix.map { it.clone() }.toTypedArray()
				}
			}
		}
		return false
	}
	
	/**
	 * Check whether the given sumi can connect to another same sumi
	 * @param x x-coordinate of start position
	 * @param y y-coordinate of start position
	 * @param value sumi value(a~p)
	 * @param dir direction that currently check for
	 * @param corner how many corner already used now
	 * @return True: Found the sumi that can connect,
	 * False: Can't find
	 */
	private fun check(x : Int, y : Int, value : Char, dir : Int, corner : Int) : Boolean {
		var posX = x
		var posY = y
		finishedMatrix[posX][posY] = true
		while (true) {
			posX += dx[dir]
			posY += dy[dir]
			if (posX !in 0..7 || posY !in 0..12) break
			if (finishedMatrix[posX][posY]) break
			if (posX in 1..6 && posY in 1..11) finishedMatrix[posX][posY] = true
			if (computeMatrix[posX][posY] == 'x') {
				if (corner < 2) {
					for (i in 0 until 4) {
						if (i == dir) {
							if (check(posX, posY, value, i, corner)) return true
						}
						else {
							if (check(posX, posY, value, i, corner+1)) return true
						}
					}
				}
			}
			else if (computeMatrix[posX][posY] == value) {
				rx = posX
				ry = posY
				return true
			}
			else break
		}
		return false
	}
	
	/**
	 * Connect two point and add the pending sumi
	 * @param x x-coordinate of point
	 * @param y y-coordinate of point
	 */
	private fun connect(x : Int, y : Int) {
		computeMatrix[x][y] = 'x'
		computeMatrix[rx][ry] = 'x'
		answerQueue.addLast(Pair(x, y))
		answerQueue.addLast(Pair(rx, ry))
		for (i in 0 until 4) {
			var posX = x
			var posY = y
			while (true) {
				posX += dx[i]
				posY += dy[i]
				if (posX !in 1..6 || posY !in 1..11) break
				if (computeMatrix[posX][posY] !in xzList) {
					processQueue.addLast(Pair(posX, posY))
				}
			}
		}
		for (i in 0 until 4) {
			var posX = rx
			var posY = ry
			while (true) {
				posX += dx[i]
				posY += dy[i]
				if (posX !in 1..6 || posY !in 1..11) break
				if (computeMatrix[posX][posY] !in xzList) {
					processQueue.addLast(Pair(posX, posY))
				}
			}
		}
	}
	
	/**
	 * Check the board is solved or not
	 * @return True: Solved it,
	 * False: There are sumi remained
	 */
	private fun isEmpty() : Boolean {
		for (i in 1..6) {
			for (j in 1..11) {
				if (computeMatrix[i][j] !in xzList) return false
			}
		}
		return true
	}
}