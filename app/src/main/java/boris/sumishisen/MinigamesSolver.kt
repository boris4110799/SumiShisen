package boris.sumishisen

import java.util.*

object MinigamesSolver {
	private val dx = intArrayOf(0, -1, 0, 1)
	private val dy = intArrayOf(1, 0, -1, 0)
	private val m = Array(8) { Array(13) { 'x' } }
	private val mm = Array(8) { Array(13) { 'x' } }
	private val n = Array(8) { Array(13) { false } }
	private var rx : Int = 0
	private var ry : Int = 0
	private var q = LinkedList<Pair<Int, Int>>()
	private var ans = LinkedList<Pair<Int, Int>>()
	val xzList get() = listOf('x', 'z')
	
	/**
	 * Return answer after calculate the problem
	 */
	fun getAnswer() : LinkedList<Pair<Int, Int>> {
		return ans
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
				m[i][j] = inputStr[(i-1)*11+j-1]
				mm[i][j] = inputStr[(i-1)*11+j-1]
			}
		}
		
		for (i in 1..6) {
			for (j in 1..11) {
				for (d in 0 until 4) {
					q.addLast(Pair(i, j))
					ans.clear()
					while (q.isNotEmpty()) {
						val (x, y) = q.removeFirst()
						if (m[x][y] in xzList) continue
						for (k in 0 until 8) {
							for (l in 0 until 13) {
								n[k][l] = false
							}
						}
						var k = d
						var l = 0
						while (l < 4) {
							if (check(x, y, m[x][y], k%4, 0)) {
								connect(x, y)
								break
							}
							k += 1
							l += 1
						}
					}
					if (isEmpty()) return true
					for (k in 1..6) {
						for (l in 1..11) {
							m[k][l] = mm[k][l]
						}
					}
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
		var posx = x
		var posy = y
		n[posx][posy] = true
		while (true) {
			posx += dx[dir]
			posy += dy[dir]
			if (posx !in 0..7 || posy !in 0..12) break
			if (n[posx][posy]) break
			if (posx in 1..6 && posy in 1..11) n[posx][posy] = true
			if (m[posx][posy] == 'x') {
				if (corner < 2) {
					for (i in 0 until 4) {
						if (i == dir) {
							if (check(posx, posy, value, i, corner)) return true
						}
						else {
							if (check(posx, posy, value, i, corner+1)) return true
						}
					}
				}
			}
			else if (m[posx][posy] == value) {
				rx = posx
				ry = posy
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
		m[x][y] = 'x'
		m[rx][ry] = 'x'
		ans.addLast(Pair(x, y))
		ans.addLast(Pair(rx, ry))
		for (i in 0 until 4) {
			var posx = x
			var posy = y
			while (true) {
				posx += dx[i]
				posy += dy[i]
				if (posx !in 1..6 || posy !in 1..11) break
				if (m[posx][posy] !in xzList) {
					q.addLast(Pair(posx, posy))
				}
			}
		}
		for (i in 0 until 4) {
			var posx = rx
			var posy = ry
			while (true) {
				posx += dx[i]
				posy += dy[i]
				if (posx !in 1..6 || posy !in 1..11) break
				if (m[posx][posy] !in xzList) {
					q.addLast(Pair(posx, posy))
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
				if (m[i][j] !in xzList) return false
			}
		}
		return true
	}
}