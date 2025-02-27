package tw.borishuang.sumishisen.minigame

import tw.borishuang.sumishisen.matrix.Matrix2D
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * The utility of mini game.
 */
object MiniGameUtil {
    /**
     * Transform the board-style string into data-style string.
     */
    fun text2Hint(inputStr: String): CharSequence {
        return when (inputStr) {
            "-"   -> "x"
            "Box" -> "z"
            else  -> inputStr.lowercase()
        }
    }

    /**
     * Transform the data-style string into board-style string.
     */
    fun hint2Text(char: Char): String {
        return when (char) {
            'x'  -> "-"
            'z'  -> "Box"
            else -> char.uppercase()
        }
    }

    /**
     * Transform the data-style string into matrix.
     */
    private fun hint2Matrix(input: String) = Matrix2D(6, 11) { i, j ->
        hint2Text(input[i * 11 + j])
    }

    /**
     * Calculate the current board matched the data in data set or not.
     */
    fun matchData(
        input: String,
        dataSet: Set<String>,
        onMatch: (Matrix2D<String>) -> Unit,
        onUnMatch: () -> Unit): Int {
        var matchCount = 0
        var matchStr = Matrix2D(0, "")

        for (data in dataSet) {
            val dataMap = mutableMapOf<Char, Char>()
            var isMatch = true
            for (j in data.indices) {
                if (!isMatch) break
                if (input[j] == 'x') continue
                if (input[j] == 'z') {
                    if (data[j] != 'z') isMatch = false
                }
                else {
                    if (dataMap.containsKey(data[j])) {
                        if (dataMap[data[j]] != input[j]) isMatch = false
                    }
                    else {
                        if (data[j] in MiniGameSolver.xzList) isMatch = false
                        else dataMap[data[j]] = input[j]
                    }
                }
            }

            val inputMap = mutableMapOf<Char, Char>()
            for (j in data.indices) {
                if (!isMatch) break
                if (data[j] == 'x') {
                    if (input[j] != 'x') isMatch = false
                }
                else if (data[j] == 'z') {
                    if (input[j] !in MiniGameSolver.xzList) isMatch = false
                }
                else {
                    if (inputMap.containsKey(input[j])) {
                        if (inputMap[input[j]] != data[j]) isMatch = false
                    }
                    else {
                        if (input[j] !in MiniGameSolver.xzList) inputMap[input[j]] = data[j]
                    }
                }
            }

            if (isMatch) {
                matchCount += 1
                matchStr = hint2Matrix(data)
            }
        }
        if (matchCount == 1) {
            onMatch(matchStr)
        }
        else {
            onUnMatch()
        }
        return matchCount
    }

    /**
     * Combine two data set.
     */
    fun combineData(dataSet: List<String>, dataList: List<String>): List<String> {
        val newDataList = mutableListOf<String>()

        newDataList.addAll(dataSet)

        for (data in dataList) {
            val formatedData = formatData(data)
            if (!dataSet.contains(formatedData)) {
                newDataList.add(formatedData)
            }
        }

        return newDataList.sorted()
    }

    /**
     * Convert the data string into minimized format.
     */
    fun formatData(data: String): String {
        val dataMap = mutableMapOf<Char, Char>()
        var output = ""
        var char = 'a'

        for (c in data) {
            if (c == 'x') output += 'x'
            else if (c == 'z') output += 'z'
            else {
                if (dataMap.containsKey(c)) {
                    output += dataMap[c]
                }
                else {
                    dataMap[c] = char
                    output += char
                    char += 1
                }
            }
        }
        return output
    }
}

/**
 * Wrap up double for-loop.
 */
@OptIn(ExperimentalContracts::class)
inline fun <T> forMiniGame(action: (i: Int, j: Int) -> T) {
    contract { callsInPlace(action) }

    for (i in 1..6) {
        for (j in 1..11) {
            action(i, j)
        }
    }
}