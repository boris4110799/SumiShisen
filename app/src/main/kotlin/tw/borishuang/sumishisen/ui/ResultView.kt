package tw.borishuang.sumishisen.ui

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.provider.Settings
import android.view.LayoutInflater
import kotlinx.coroutines.*
import tw.borishuang.sumishisen.R
import tw.borishuang.sumishisen.databinding.ResultLayoutBinding
import tw.borishuang.sumishisen.minigame.*
import tw.borishuang.sumishisen.service.SumiService
import tw.borishuang.sumishisen.service.WindowService
import tw.borishuang.sumishisen.util.FileUtil
import tw.borishuang.sumishisen.util.PermissionUtil

class ResultView(context: Context) : BaseView<ResultLayoutBinding>(context) {

    override val binding = ResultLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    /**
     * Store the button id with map.
     */
    private val idMap = mutableMapOf<Int, Int>()

    /**
     * The queue of answer.
     */
    private var answerQueue = listOf<Pair<Int, Int>>()

    /**
     * The index of answerQueue.
     */
    private var answerQueueInd = 0

    init {
        setOnClickListener()
        setIdMap()
    }

    fun setData(data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            forMiniGame { i, j ->
                findButton(idMap[100 * i + j]!!).apply {
                    text = MiniGameUtil.hint2Text(data[(i - 1) * 11 + j - 1])
                }
            }

            val solver = MiniGameSolver(data)
            val isSolved = solver.cal()
            answerQueue = solver.getAnswer()

            if (isSolved && answerQueue.isNotEmpty()) {
                binding.tvInfo.text = context.getText(R.string.text_success)
                binding.btnAuto.visibility = VISIBLE
                binding.btnPre.visibility = VISIBLE
                binding.btnNext.visibility = VISIBLE
                showAnswer(answerQueueInd)

                // Save when the data is completed
                if (data.filter { c -> c == 'x' }.length < 4) {
                    val dataList = FileUtil.readData(context)

                    FileUtil.writeData(context, dataList.toMutableSet().apply { add(MiniGameUtil.formatData(data)) })
                }
            }
            else {
                binding.tvInfo.text = context.getText(R.string.text_fail)
                binding.btnAuto.visibility = INVISIBLE
                binding.btnPre.visibility = INVISIBLE
                binding.btnNext.visibility = INVISIBLE
            }
        }
    }

    fun setOnCloseClick(onClick: () -> Unit) {
        binding.btnClose.setOnClickListener { onClick() }
    }

    private fun setOnClickListener() {
        binding.btnAuto.setOnClickListener {
            if (PermissionUtil.isAccessibilityServiceEnabled(context)) {
                context.sendBroadcast(Intent().setAction(WindowService.HIDE_SCREEN).setPackage(context.packageName))

                // Send the answer
                CoroutineScope(Dispatchers.Main).launch {
                    for (answer in answerQueue) {
                        context.sendBroadcast(Intent().setAction(SumiService.ACTION_SUMI)
                            .setPackage(context.packageName)
                            .putExtra("x", answer.first)
                            .putExtra("y", answer.second))
                        delay(100)
                    }
                }
                // Send the start signal
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    context.sendBroadcast(Intent().setAction(SumiService.ACTION_START).setPackage(context.packageName))
                }
            }
            else {
                binding.tvInfo.text = context.getString(R.string.text_unable)
            }
        }
        binding.btnAuto.setOnLongClickListener {
            context.startActivity(
                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return@setOnLongClickListener true
        }
        binding.btnPre.setOnClickListener {
            hideAnswer(answerQueueInd)
            if (answerQueueInd > 0) answerQueueInd -= 1
            showAnswer(answerQueueInd)
        }
        binding.btnNext.setOnClickListener {
            hideAnswer(answerQueueInd)
            if (answerQueueInd < answerQueue.size / 2 - 1) answerQueueInd += 1
            showAnswer(answerQueueInd)
        }
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
    }

    /**
     * Show the answer.
     */
    private fun showAnswer(ind: Int) {
        if (ind in answerQueue.indices) {
            val btnId1 = answerQueue[ind * 2].first * 100 + answerQueue[ind * 2].second
            val btnId2 = answerQueue[ind * 2 + 1].first * 100 + answerQueue[ind * 2 + 1].second

            if (idMap.contains(btnId1)) {
                findButton(idMap[btnId1]!!).backgroundTintList = ColorStateList.valueOf(
                    context.getColor(R.color.md_theme_error))
            }
            if (idMap.contains(btnId2)) {
                findButton(idMap[btnId2]!!).backgroundTintList = ColorStateList.valueOf(
                    context.getColor(R.color.md_theme_error))
            }
        }
    }

    /**
     * Hide the answer.
     */
    private fun hideAnswer(ind: Int) {
        if (ind in answerQueue.indices) {
            val btnId1 = answerQueue[ind * 2].first * 100 + answerQueue[ind * 2].second
            val btnId2 = answerQueue[ind * 2 + 1].first * 100 + answerQueue[ind * 2 + 1].second

            if (idMap.contains(btnId1)) {
                findButton(idMap[btnId1]!!).backgroundTintList = ColorStateList.valueOf(
                    context.getColor(R.color.md_theme_secondary))
            }
            if (idMap.contains(btnId2)) {
                findButton(idMap[btnId2]!!).backgroundTintList = ColorStateList.valueOf(
                    context.getColor(R.color.md_theme_secondary))
            }
        }
    }
}