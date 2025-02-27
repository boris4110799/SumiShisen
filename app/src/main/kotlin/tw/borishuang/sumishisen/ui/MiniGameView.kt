package tw.borishuang.sumishisen.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.*
import android.view.View.OnClickListener
import android.widget.Button
import kotlinx.coroutines.*
import tw.borishuang.sumishisen.R
import tw.borishuang.sumishisen.databinding.MiniGameLayoutBinding
import tw.borishuang.sumishisen.matrix.Matrix2D
import tw.borishuang.sumishisen.minigame.MiniGameUtil
import tw.borishuang.sumishisen.minigame.forMiniGame
import tw.borishuang.sumishisen.util.DataStoreUtil
import tw.borishuang.sumishisen.util.FileUtil

class MiniGameView(context: Context) : BaseView<MiniGameLayoutBinding>(context) {

    override val binding = MiniGameLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    /**
     * The quantity of data, used to determine whether need to update the data set.
     */
    private val dataSize = 120

    /**
     * The data set that record the Minigame's problem.
     */
    private val dataSet = mutableSetOf<String>()

    /**
     * Store the button id with map.
     */
    private val idMap = mutableMapOf<Int, Int>()

    /**
     * The button id (A~P, Box, Del) that you choose.
     */
    private var chooseID = 0

    /**
     * The counter of A~P that you mark above.
     */
    private val inputCounter = mutableMapOf<Int, Int>()

    /**
     * Current data of the board.
     */
    private var currentMatrix = Matrix2D(6, 11, " ")

    /**
     * Matched data of the board.
     */
    private var matchMatrix = Matrix2D(6, 11, " ")

    /**
     * The listener when the A~P, Box, Del button is being clicked.
     */
    private val chooseListener = OnClickListener { view ->
        if (chooseID != 0) {
            findButton(chooseID).backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.md_theme_primary))
        }
        view.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.md_theme_error))
        chooseID = view.id
    }

    /**
     * The listener when the board being clicked.
     */
    private val fillListener = OnClickListener { view ->
        val button = view as Button

        if (chooseID != 0) {
            when (chooseID) {
                binding.btnDel.id -> {
                    button.text = "-"
                    button.hint = "x"
                    refreshView()
                    currentMatrix = outputText()
                }

                binding.btnBox.id -> {
                    if (button.hint == "x") {
                        button.text = binding.btnBox.text
                        button.hint = binding.btnBox.hint
                        refreshView()
                        currentMatrix = outputText()
                    }
                }

                else              -> {
                    if (button.hint == "x") {
                        if (inputCounter[chooseID] == 4) {
                            chooseID = 0
                        }
                        else {
                            button.text = findButton(chooseID).text
                            button.hint = findButton(chooseID).hint
                            refreshView()
                            currentMatrix = outputText()
                        }
                    }
                }
            }
        }
    }

    init {
        setDataSet()
        setIdMap()
        setOnClickListener()
    }

    /**
     * Read data.txt if exist, otherwise create new file, then setup data set.
     */
    private fun setDataSet() {
        val dataList = FileUtil.readData(context)

        CoroutineScope(Dispatchers.IO).launch {
            val count = DataStoreUtil.readData(context, DataStoreUtil.SUMISHISEN_DATA_COUNT, 0)

            if (count < dataSize) {
                DataStoreUtil.writeData(context, DataStoreUtil.SUMISHISEN_DATA_COUNT, dataSize)

                val problems = context.assets.open("mini_game_problems.txt").bufferedReader().useLines {
                    it.toList()
                }

                dataSet.addAll(MiniGameUtil.combineData(problems, dataList))
                FileUtil.writeData(context, dataSet)
            }
            else {
                dataSet.addAll(dataList)
            }
            refreshView()
        }
    }

    fun setOnBackClick(onClick: () -> Unit) {
        binding.btnBack.setOnClickListener { onClick() }
    }

    fun setOnOkClick(onClick: (String) -> Unit) {
        binding.btnOk.setOnClickListener { onClick(outputHint()) }
    }

    /**
     * Record the button id with map.
     */
    private fun setIdMap() {
        idMap[101] = binding.btn101.id
        idMap[102] = binding.btn102.id
        idMap[103] = binding.btn103.id
        idMap[104] = binding.btn104.id
        idMap[105] = binding.btn105.id
        idMap[106] = binding.btn106.id
        idMap[107] = binding.btn107.id
        idMap[108] = binding.btn108.id
        idMap[109] = binding.btn109.id
        idMap[110] = binding.btn110.id
        idMap[111] = binding.btn111.id
        idMap[201] = binding.btn201.id
        idMap[202] = binding.btn202.id
        idMap[203] = binding.btn203.id
        idMap[204] = binding.btn204.id
        idMap[205] = binding.btn205.id
        idMap[206] = binding.btn206.id
        idMap[207] = binding.btn207.id
        idMap[208] = binding.btn208.id
        idMap[209] = binding.btn209.id
        idMap[210] = binding.btn210.id
        idMap[211] = binding.btn211.id
        idMap[301] = binding.btn301.id
        idMap[302] = binding.btn302.id
        idMap[303] = binding.btn303.id
        idMap[304] = binding.btn304.id
        idMap[305] = binding.btn305.id
        idMap[306] = binding.btn306.id
        idMap[307] = binding.btn307.id
        idMap[308] = binding.btn308.id
        idMap[309] = binding.btn309.id
        idMap[310] = binding.btn310.id
        idMap[311] = binding.btn311.id
        idMap[401] = binding.btn401.id
        idMap[402] = binding.btn402.id
        idMap[403] = binding.btn403.id
        idMap[404] = binding.btn404.id
        idMap[405] = binding.btn405.id
        idMap[406] = binding.btn406.id
        idMap[407] = binding.btn407.id
        idMap[408] = binding.btn408.id
        idMap[409] = binding.btn409.id
        idMap[410] = binding.btn410.id
        idMap[411] = binding.btn411.id
        idMap[501] = binding.btn501.id
        idMap[502] = binding.btn502.id
        idMap[503] = binding.btn503.id
        idMap[504] = binding.btn504.id
        idMap[505] = binding.btn505.id
        idMap[506] = binding.btn506.id
        idMap[507] = binding.btn507.id
        idMap[508] = binding.btn508.id
        idMap[509] = binding.btn509.id
        idMap[510] = binding.btn510.id
        idMap[511] = binding.btn511.id
        idMap[601] = binding.btn601.id
        idMap[602] = binding.btn602.id
        idMap[603] = binding.btn603.id
        idMap[604] = binding.btn604.id
        idMap[605] = binding.btn605.id
        idMap[606] = binding.btn606.id
        idMap[607] = binding.btn607.id
        idMap[608] = binding.btn608.id
        idMap[609] = binding.btn609.id
        idMap[610] = binding.btn610.id
        idMap[611] = binding.btn611.id
        idMap[0] = binding.btnA.id
        idMap[1] = binding.btnB.id
        idMap[2] = binding.btnC.id
        idMap[3] = binding.btnD.id
        idMap[4] = binding.btnE.id
        idMap[5] = binding.btnF.id
        idMap[6] = binding.btnG.id
        idMap[7] = binding.btnH.id
        idMap[8] = binding.btnI.id
        idMap[9] = binding.btnJ.id
        idMap[10] = binding.btnK.id
        idMap[11] = binding.btnL.id
        idMap[12] = binding.btnM.id
        idMap[13] = binding.btnN.id
        idMap[14] = binding.btnO.id
        idMap[15] = binding.btnP.id
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListener() {
        // Set up listener
        for (i in 0..15) {
            inputCounter[idMap[i]!!] = 0
            findButton(idMap[i]!!).setOnClickListener(chooseListener)
        }
        forMiniGame { i, j ->
            findButton(idMap[100 * i + j]!!).setOnClickListener(fillListener)
        }
        binding.btnBox.setOnClickListener(chooseListener)
        binding.btnDel.setOnClickListener(chooseListener)
        // Clear the board when long clicked
        binding.btnDel.setOnLongClickListener {
            forMiniGame { i, j ->
                findButton(idMap[100 * i + j]!!).apply {
                    text = "-"
                    hint = "x"
                }
            }
            for (i in 0..15) {
                inputCounter[idMap[i]!!] = 0
            }
            binding.btnView.visibility = View.INVISIBLE
            binding.btnApply.visibility = View.INVISIBLE
            refreshView()
            return@setOnLongClickListener true
        }
        // Preview the match data
        binding.btnView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    forMiniGame { i, j ->
                        findButton(idMap[100 * i + j]!!).text = matchMatrix[i - 1, j - 1]
                    }
                }

                MotionEvent.ACTION_UP   -> {
                    forMiniGame { i, j ->
                        findButton(idMap[100 * i + j]!!).text = currentMatrix[i - 1, j - 1]
                    }
                }
            }
            false
        }
        // Apply the match data
        binding.btnApply.setOnClickListener {
            forMiniGame { i, j ->
                findButton(idMap[100 * i + j]!!).apply {
                    text = matchMatrix[i - 1, j - 1]
                    hint = MiniGameUtil.text2Hint(matchMatrix[i - 1, j - 1])
                }
            }
            refreshView()
            currentMatrix = outputText()
        }
    }

    /**
     * Refresh the board and update the A~P button status
     */
    private fun refreshView() {
        val keyList = inputCounter.keys.toList()

        // Clear the counter
        for (i in keyList) {
            inputCounter[i] = 0
        }

        // Refresh the counter
        forMiniGame { i, j ->
            val btnInd = findButton(idMap[100 * i + j]!!).hint[0].minus('a')
            if (btnInd in 0..15) {
                val btnId = idMap[btnInd]!!
                inputCounter[btnId] = inputCounter[btnId]!! + 1
            }
        }

        // Control the A~P button's visibility
        for ((id, count) in inputCounter) {
            if (count >= 4) {
                findButton(id).apply {
                    backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.md_theme_primary))
                    visibility = View.INVISIBLE
                }
            }
            else {
                findButton(id).visibility = View.VISIBLE
            }
        }

        // Update the match count
        val matchCount = MiniGameUtil.matchData(outputHint(), dataSet, {
            matchMatrix = it
            binding.btnView.visibility = View.VISIBLE
            binding.btnApply.visibility = View.VISIBLE
            binding.btnBack.visibility = View.INVISIBLE
        }, {
            binding.btnView.visibility = View.INVISIBLE
            binding.btnApply.visibility = View.INVISIBLE
            binding.btnBack.visibility = View.VISIBLE
        })
        binding.tvMatch.text = context.getString(R.string.text_match, matchCount)
    }

    /**
     * Transform board to array.
     */
    private fun outputText() = Matrix2D(6, 11) { i, j ->
        findButton(idMap[100 * (i + 1) + j + 1]!!).text.toString()
    }

    /**
     * Transform board to data.
     */
    private fun outputHint(): String {
        var str = ""
        forMiniGame { i, j ->
            str += findButton(idMap[100 * i + j]!!).hint
        }
        return str
    }
}