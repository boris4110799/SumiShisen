package boris.sumishisen

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ServiceInfo
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import boris.sumishisen.databinding.SumiLayoutBinding
import java.lang.Thread.sleep
import kotlin.math.abs
import kotlin.math.roundToInt

class SumiWindow : Service() {
	//In below comment, 'data' means the string-style of problem
	//Ex:abcadefefbgghijklkzjgmlzjhfzdaekcnzgobmldiocmhoeohidbijzlkcanxnfmn

	//This is some arguments for the view
	private lateinit var windowManager: WindowManager
	private lateinit var accessibilityManager: AccessibilityManager
	private lateinit var iconView: View
	private lateinit var sumiView: View
	private var _binding: SumiLayoutBinding? = null
	private val binding get() = _binding!!
	private lateinit var metrics: DisplayMetrics
	private val screenWidth get() = metrics.widthPixels
	private val screenHeight get() = metrics.heightPixels
	private lateinit var iconLayoutParams: WindowManager.LayoutParams
	private lateinit var windowLayoutParams: WindowManager.LayoutParams
	private val actionCLOSE = "ACTION_CLOSE"

	/**
	 * The quantity of data
	 */
	private val dataSize = 76

	/**
	 * Is the icon being click
	 */
	private var isSumiViewShow = false

	/**
	 * Store the button id with map
	 */
	private val idMap = mutableMapOf<Int, Int>()

	/**
	 * The set of data that record the Minigame's problem
	 */
	private val dataSet = mutableSetOf<String>()

	/**
	 * The button id (A~P, Box, Del) that you choose
	 */
	private var chooseID = 0

	/**
	 * The counter of A~P that you mark above
	 */
	private val inputCounter = mutableMapOf<Int, Int>()

	/**
	 * Current data of the board
	 */
	private var currentStr = Array(66) { " " }

	/**
	 * Matched data of the board
	 */
	private var matchStr = Array(66) { " " }

	/**
	 * The queue of answer
	 */
	private val answerQueue get() = MinigamesSolver.getAnswer()

	/**
	 * The index of answerQueue
	 */
	private var answerQueueInd = 0

	/**
	 * The status of board
	 */
	private var boardStatus = -1

	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		metrics = applicationContext.resources.displayMetrics
		iconLayoutParams.apply {
			x = (x/abs(x))*screenWidth/2
			y = (y.toDouble()/screenWidth.toDouble()*screenHeight.toDouble()).toInt()
		}
		windowManager.updateViewLayout(iconView, iconLayoutParams)
	}

	@SuppressLint("InflateParams", "ClickableViewAccessibility")
	override fun onCreate() {
		super.onCreate()

		//Set up the variable
		metrics = applicationContext.resources.displayMetrics
		windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
		accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

		setNotification()
		setDataSet()
		updateDataSet()
		setView()
		setIdMap()

		//The listener when the A~P, Box, Del button is being clicked
		val chooseListener = View.OnClickListener { view ->
			if (chooseID != 0) binding.root.findViewById<Button>(chooseID).backgroundTintList = ColorStateList.valueOf(
				getColor(R.color.purple_200))
			view.backgroundTintList = ColorStateList.valueOf(getColor(R.color.accent))
			chooseID = view.id
		}

		//The listener when the board being clicked
		val fillListener = View.OnClickListener { view ->
			if (chooseID != 0) {
				when (chooseID) {
					binding.btnDel.id -> {
						(view as Button).text = "-"
						view.hint = "x"
						refreshView()
						currentStr = outputText()
					}

					binding.btnBox.id -> {
						if ((view as Button).hint == "x") {
							view.text = binding.btnBox.text
							view.hint = binding.btnBox.hint
							refreshView()
							currentStr = outputText()
						}
					}

					else              -> {
						if ((view as Button).hint == "x") {
							if (inputCounter[chooseID] == 4) {
								chooseID = 0
							}
							else {
								view.text = binding.root.findViewById<Button>(chooseID).text
								view.hint = binding.root.findViewById<Button>(chooseID).hint
								refreshView()
								currentStr = outputText()
							}
						}
					}
				}
			}
		}

		//Set up listener
		for (i in 0..15) {
			inputCounter[idMap[i]!!] = 0
			binding.root.findViewById<Button>(idMap[i]!!).setOnClickListener(chooseListener)
		}
		for (i in 1..6) {
			for (j in 1..11) {
				binding.root.findViewById<Button>(idMap[100*i+j]!!).setOnClickListener(fillListener)
			}
		}
		binding.btnBox.setOnClickListener(chooseListener)
		binding.btnDel.setOnClickListener(chooseListener)
		//Clear the board when long clicked
		binding.btnDel.setOnLongClickListener {
			for (i in 1..6) {
				for (j in 1..11) {
					binding.root.findViewById<Button>(idMap[100*i+j]!!).apply {
						text = "-"
						hint = "x"
					}
				}
			}
			for (i in 0..15) {
				inputCounter[idMap[i]!!] = 0
				binding.root.findViewById<Button>(idMap[i]!!).visibility = View.VISIBLE
			}
			binding.btnView.visibility = View.INVISIBLE
			binding.btnApply.visibility = View.INVISIBLE
			refreshView()
			return@setOnLongClickListener true
		}
		//Calculate the answer and show it
		binding.btnOk.setOnClickListener {
			viewOutput()
			if (chooseID != 0) binding.root.findViewById<Button>(chooseID).backgroundTintList = ColorStateList.valueOf(
				getColor(R.color.purple_200))
			chooseID = 0
			binding.textViewInfo.text = getString(R.string.text_waiting)

			val inputStr = outputHint()
			boardStatus = if (MinigamesSolver.cal(inputStr)) 1 else 0
			if (boardStatus == 1) {
				if (answerQueue.size > 0) {
					binding.textViewInfo.text = getString(R.string.text_success)
					showAnswer(answerQueueInd)

					//Save when the inputStr is completed
					if (inputStr.filter { c -> c == 'x' }.length < 4) {
						dataSet.add(formattingStr(inputStr))
						var outputStr = ""
						for (i in dataSet) {
							outputStr += "${i}\n"
						}
						applicationContext.openFileOutput("data.txt", Context.MODE_PRIVATE).use {
							it.write(outputStr.toByteArray())
						}
					}
				}
				else boardStatus = 0
			}
			if (boardStatus == 0) binding.textViewInfo.text = getString(R.string.text_fail)
		}
		//Run auto click
		binding.btnAuto.setOnClickListener {
			if (isAccessibilityServiceEnabled()) {
				isSumiViewShow = false
				windowManager.removeView(sumiView)
				Thread {
					sleep(1000)
					for (p in answerQueue) {
						sendBroadcast(Intent().setAction(SumiService.ACTION_CLICK)
							.setPackage(packageName)
							.putExtra("x", p.first)
							.putExtra("y", p.second))
						sleep(100)
					}
				}.start()
			}
			else binding.textViewInfo.text = getString(R.string.text_unable)
		}
		//Jump to accessibility setting
		binding.btnAuto.setOnLongClickListener {
			startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
			return@setOnLongClickListener true
		}
		//Show previous answer
		binding.btnPre.setOnClickListener {
			if (boardStatus == 1) {
				hideAnswer(answerQueueInd)
				if (answerQueueInd > 0) answerQueueInd -= 1
				showAnswer(answerQueueInd)
			}
		}
		//Show next answer
		binding.btnNext.setOnClickListener {
			if (boardStatus == 1) {
				hideAnswer(answerQueueInd)
				if (answerQueueInd < answerQueue.size/2-1) answerQueueInd += 1
				showAnswer(answerQueueInd)
			}
		}
		//Close the answer mode
		binding.btnClose.setOnClickListener {
			viewInput()
			if (boardStatus == 1) hideAnswer(answerQueueInd)
			boardStatus = -1
			answerQueueInd = 0
			refreshView()
		}
		//Preview the match data
		binding.btnView.setOnTouchListener { _, event ->
			when (event.action) {
				MotionEvent.ACTION_DOWN -> {
					for (i in 1..6) {
						for (j in 1..11) {
							binding.root.findViewById<Button>(idMap[100*i+j]!!).text = matchStr[(i-1)*11+j-1]
						}
					}
				}

				MotionEvent.ACTION_UP   -> {
					for (i in 1..6) {
						for (j in 1..11) {
							binding.root.findViewById<Button>(idMap[100*i+j]!!).text = currentStr[(i-1)*11+j-1]
						}
					}
				}
			}
			false
		}
		//Apply the match data
		binding.btnApply.setOnClickListener {
			for (i in 1..6) {
				for (j in 1..11) {
					binding.root.findViewById<Button>(idMap[100*i+j]!!).text = matchStr[(i-1)*11+j-1]
					binding.root.findViewById<Button>(idMap[100*i+j]!!).hint = text2Hint(matchStr[(i-1)*11+j-1])
				}
			}
			refreshView()
			currentStr = outputText()
		}
		binding.textViewMatch.text = getString(R.string.text_match, dataSet.size)

		viewInput()
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		//When user clicked the 'Close' button in notification, stop the service
		if (intent != null) {
			val action = intent.action
			if (action != null) {
				when (action) {
					actionCLOSE -> {
						stopSelf()
					}
				}
			}
		}
		return super.onStartCommand(intent, flags, startId)
	}

	override fun onBind(intent: Intent): IBinder? {
		return null
	}

	override fun onDestroy() {
		super.onDestroy()
		stopSelf()
		windowManager.removeView(iconView)
		if (isSumiViewShow) windowManager.removeView(sumiView)
	}

	/**
	 * Set up the notification
	 */
	private fun setNotification() {
		val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		val channel = NotificationChannel("SumiShisen", "Notification", NotificationManager.IMPORTANCE_DEFAULT).apply {
			setShowBadge(false)
		}
		notificationManager.createNotificationChannel(channel)
		val builder = NotificationCompat.Builder(this, "SumiShisen")
			.addAction(R.drawable.sumikko_cat, "Close", PendingIntent.getService(applicationContext, 1,
				Intent(applicationContext, SumiWindow::class.java).setAction(actionCLOSE),
				PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))
			.setSmallIcon(R.drawable.sumikko_cat)
			.setContentText("SumiShisen is running")
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setOngoing(true)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) startForeground(1, builder.build(),
			ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
		else startForeground(1, builder.build())
	}

	/**
	 * Read data.txt if exist, otherwise create new file
	 */
	private fun setDataSet() {
		//If file exist then load data, otherwise create the file
		if (applicationContext.getFileStreamPath("data.txt").exists()) {
			applicationContext.openFileInput("data.txt").bufferedReader().useLines {
				dataSet.addAll(it)
			}
		}
		else {
			applicationContext.getFileStreamPath("data.txt").createNewFile()
			applicationContext.openFileOutput("data.txt", Context.MODE_PRIVATE).use {
				it.write("".toByteArray())
			}
		}
	}

	/**
	 * Update and format data
	 */
	private fun updateDataSet() {
		//If Count not equal to dataSize, which means the new data are in the asset, then add it
		val sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE)
		val num = sharedPreferences.getInt("Count", 0)
		if (num < dataSize) {
			val editor = sharedPreferences.edit()
			editor.putInt("Count", dataSize)
			editor.apply()

			val assetFd = assets.openFd("dataSet.txt")
			var dataList: List<String> = listOf()
			assetFd.createInputStream().bufferedReader().useLines {
				dataList = it.toList()
			}
			assetFd.close()

			for (i in dataList) {
				var comparedData = ""
				for (j in dataSet) {
					comparedData = compareStr(i, j)
					if (comparedData.isNotEmpty()) {
						if (comparedData == i) {
							dataSet.remove(j)
							dataSet.add(i)
						}
						break
					}
				}
				if (comparedData.isEmpty()) {
					dataSet.add(i)
				}
			}

			applicationContext.openFileOutput("data.txt", Context.MODE_PRIVATE).use {
				it.write(dataSet.joinToString(separator = "\n") { data -> data }.toByteArray())
			}
		}
	}

	@SuppressLint("InflateParams", "ClickableViewAccessibility")
	private fun setView() {
		iconView = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.sumi_icon, null)
		sumiView = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.sumi_layout, null)
		_binding = SumiLayoutBinding.bind(sumiView)

		//Set the icon's size and position
		iconLayoutParams = WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT)
		iconLayoutParams.apply {
			gravity = Gravity.CENTER
			x = screenWidth/2
			y = -screenHeight/4
			this.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50F, metrics).roundToInt()
			this.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50F, metrics).roundToInt()
		}

		//Set the board size and position
		windowLayoutParams = WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.RGBA_8888)
		windowLayoutParams.apply {
			gravity = Gravity.CENTER
			x = 0
			y = 0
			screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
			if (screenHeight > screenWidth) {
				this.height = (screenWidth*0.85f).toInt()
				this.width = (screenHeight*0.85f).toInt()
			}
			else {
				this.height = (screenHeight*0.85f).toInt()
				this.width = (screenWidth*0.85f).toInt()
			}
		}

		//Add view to window
		windowManager.addView(iconView, iconLayoutParams)

		//Set the icon's style and actions when touched
		iconView.setBackgroundColor(getColor(R.color.trans))
		iconView.background = AppCompatResources.getDrawable(this, R.drawable.ic_sumi)
		iconView.setOnTouchListener(object : View.OnTouchListener {
			val iconLayoutUpdateParams: WindowManager.LayoutParams = iconLayoutParams
			var sx = 0.0
			var sy = 0.0
			var px = 0.0
			var py = 0.0

			override fun onTouch(v: View?, event: MotionEvent): Boolean {
				when (event.action) {
					MotionEvent.ACTION_DOWN -> {
						sx = iconLayoutUpdateParams.x.toDouble()
						sy = iconLayoutUpdateParams.y.toDouble()
						px = event.rawX.toDouble()
						py = event.rawY.toDouble()
					}

					MotionEvent.ACTION_MOVE -> {
						iconLayoutUpdateParams.x = (sx+event.rawX-px).toInt()
						iconLayoutUpdateParams.y = (sy+event.rawY-py).toInt()
						windowManager.updateViewLayout(iconView, iconLayoutUpdateParams)
					}

					MotionEvent.ACTION_UP   -> {
						if (iconLayoutUpdateParams.x in -100..100 && iconLayoutUpdateParams.y in screenHeight/2-300..screenHeight/2) {
							stopSelf()
						}
						else {
							if (iconLayoutUpdateParams.x < 0) {
								iconLayoutUpdateParams.x = -screenWidth/2
								windowManager.updateViewLayout(iconView, iconLayoutUpdateParams)
							}
							else if (iconLayoutUpdateParams.x >= 0) {
								iconLayoutUpdateParams.x = screenWidth/2
								windowManager.updateViewLayout(iconView, iconLayoutUpdateParams)
							}
							if (abs(event.rawX.toDouble()-px) < 10 && abs(event.rawY.toDouble()-py) < 10) {
								if (isSumiViewShow) {
									isSumiViewShow = false
									windowManager.removeView(sumiView)
								}
								else {
									isSumiViewShow = true
									windowManager.addView(sumiView, windowLayoutParams)
								}
								windowManager.updateViewLayout(iconView, iconLayoutUpdateParams)
							}
						}
					}
				}
				return false
			}
		})
	}

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

	/**
	 * Refresh the board and update the A~P button status
	 */
	private fun refreshView() {
		val keyList = inputCounter.keys.toList()
		for (i in keyList) {
			inputCounter[i] = 0
		}
		for (i in 1..6) {
			for (j in 1..11) {
				val btnInd = binding.root.findViewById<Button>(idMap[100*i+j]!!).hint.toString()[0].minus('a')
				if (btnInd in 0..15) {
					val btnID = idMap[btnInd]!!
					inputCounter[btnID] = inputCounter[btnID]!!+1
				}
			}
		}
		for (i in inputCounter) {
			if (i.value >= 4) {
				binding.root.findViewById<Button>(i.key).apply {
					backgroundTintList = ColorStateList.valueOf(getColor(R.color.purple_200))
					visibility = View.INVISIBLE
				}
			}
			else {
				binding.root.findViewById<Button>(i.key).visibility = View.VISIBLE
			}
		}
		binding.textViewMatch.text = getString(R.string.text_match, matchData(outputHint()))
	}

	/**
	 * Calculate the current board matched the data in data set or not
	 */
	private fun matchData(inputStr: String): Int {
		var matchCount = 0
		for (i in dataSet) {
			val dataMap = mutableMapOf<Char, Char>()
			var isMatch = true
			for (j in i.indices) {
				if (!isMatch) break
				if (inputStr[j] == 'x') continue
				if (inputStr[j] == 'z') {
					if (i[j] != 'z') isMatch = false
				}
				else {
					if (dataMap.containsKey(i[j])) {
						if (dataMap[i[j]] != inputStr[j]) isMatch = false
					}
					else {
						if (i[j] in MinigamesSolver.xzList) isMatch = false
						else dataMap[i[j]] = inputStr[j]
					}
				}
			}

			val inputMap = mutableMapOf<Char, Char>()
			for (j in i.indices) {
				if (!isMatch) break
				if (i[j] == 'x') {
					if (inputStr[j] != 'x') isMatch = false
				}
				else if (i[j] == 'z') {
					if (inputStr[j] !in MinigamesSolver.xzList) isMatch = false
				}
				else {
					if (inputMap.containsKey(inputStr[j])) {
						if (inputMap[inputStr[j]] != i[j]) isMatch = false
					}
					else {
						if (inputStr[j] !in MinigamesSolver.xzList) inputMap[inputStr[j]] = i[j]
					}
				}
			}

			if (isMatch) {
				matchCount += 1
				for (j in i.indices) {
					matchStr[j] = hint2Text(i[j])
				}
			}
		}
		if (matchCount == 1) {
			binding.btnView.visibility = View.VISIBLE
			binding.btnApply.visibility = View.VISIBLE
		}
		else {
			binding.btnView.visibility = View.INVISIBLE
			binding.btnApply.visibility = View.INVISIBLE
		}
		return matchCount
	}

	/**
	 * Transform the board-style string into data-style string
	 */
	private fun text2Hint(inputStr: String): CharSequence {
		return when (inputStr) {
			"-"   -> "x"
			"Box" -> "z"
			else  -> inputStr.lowercase()
		}
	}

	/**
	 * Transform the data-style string into board-style string
	 */
	private fun hint2Text(inputChar: Char): String {
		return when (inputChar) {
			'x'  -> "-"
			'z'  -> "Box"
			else -> inputChar.uppercase()
		}
	}

	/**
	 * Compare two string and return the smaller one
	 */
	private fun compareStr(aStr: String, bStr: String): String {
		val compareMap = mutableMapOf<Char, Char>()
		compareMap['x'] = 'x'
		compareMap['z'] = 'z'
		for (i in aStr.indices) {
			if (compareMap.containsKey(aStr[i])) {
				if (compareMap[aStr[i]] != bStr[i]) return ""
			}
			else {
				if (bStr[i] != 'x') {
					compareMap[aStr[i]] = bStr[i]
				}
			}
		}
		return if (aStr <= bStr) aStr else bStr
	}

	/**
	 * Change the board to input mode
	 */
	private fun viewInput() {
		for (i in 0..15) {
			binding.root.findViewById<Button>(idMap[i]!!).visibility = View.VISIBLE
		}
		binding.btnBox.visibility = View.VISIBLE
		binding.btnDel.visibility = View.VISIBLE
		binding.btnOk.visibility = View.VISIBLE
		binding.textViewInfo.visibility = View.INVISIBLE
		binding.btnAuto.visibility = View.INVISIBLE
		binding.btnPre.visibility = View.INVISIBLE
		binding.btnNext.visibility = View.INVISIBLE
		binding.btnClose.visibility = View.INVISIBLE
		binding.textViewMatch.visibility = View.VISIBLE
	}

	/**
	 * Change the board to answer mode
	 */
	private fun viewOutput() {
		for (i in 0..15) {
			binding.root.findViewById<Button>(idMap[i]!!).visibility = View.INVISIBLE
		}
		binding.btnBox.visibility = View.INVISIBLE
		binding.btnDel.visibility = View.INVISIBLE
		binding.btnOk.visibility = View.INVISIBLE
		binding.textViewInfo.visibility = View.VISIBLE
		binding.btnAuto.visibility = View.VISIBLE
		binding.btnPre.visibility = View.VISIBLE
		binding.btnNext.visibility = View.VISIBLE
		binding.btnClose.visibility = View.VISIBLE
		binding.textViewMatch.visibility = View.INVISIBLE
		binding.btnView.visibility = View.INVISIBLE
		binding.btnApply.visibility = View.INVISIBLE
	}

	/**
	 * Show the answer
	 */
	private fun showAnswer(ind: Int) {
		if (ind in 0 until answerQueue.size) {
			val btnID1 = answerQueue[ind*2].first*100+answerQueue[ind*2].second
			val btnID2 = answerQueue[ind*2+1].first*100+answerQueue[ind*2+1].second
			if (idMap.contains(btnID1)) binding.root.findViewById<Button>(
				idMap[btnID1]!!).backgroundTintList = ColorStateList.valueOf(getColor(R.color.accent))
			if (idMap.contains(btnID2)) binding.root.findViewById<Button>(
				idMap[btnID2]!!).backgroundTintList = ColorStateList.valueOf(getColor(R.color.accent))
		}
	}

	/**
	 * Hide the answer
	 */
	private fun hideAnswer(ind: Int) {
		if (ind in 0 until answerQueue.size) {
			val btnID1 = answerQueue[ind*2].first*100+answerQueue[ind*2].second
			val btnID2 = answerQueue[ind*2+1].first*100+answerQueue[ind*2+1].second
			if (idMap.contains(btnID1)) binding.root.findViewById<Button>(
				idMap[btnID1]!!).backgroundTintList = ColorStateList.valueOf(getColor(R.color.btn_input))
			if (idMap.contains(btnID2)) binding.root.findViewById<Button>(
				idMap[btnID2]!!).backgroundTintList = ColorStateList.valueOf(getColor(R.color.btn_input))
		}
	}

	/**
	 * Transform board to array
	 */
	private fun outputText(): Array<String> {
		val array = Array(66) { "" }
		for (i in 1..6) {
			for (j in 1..11) {
				array[(i-1)*11+j-1] = binding.root.findViewById<Button>(idMap[100*i+j]!!).text.toString()
			}
		}
		return array
	}

	/**
	 * Transform board to data
	 */
	private fun outputHint(): String {
		var str = ""
		for (i in 1..6) {
			for (j in 1..11) {
				str += binding.root.findViewById<Button>(idMap[100*i+j]!!).hint
			}
		}
		return str
	}

	/**
	 * Format to the smallest string
	 */
	private fun formattingStr(inputStr: String): String {
		val dataMap = mutableMapOf<Char, Char>()
		var outputStr = ""
		var char = 'a'

		for (i in inputStr.indices) {
			if (inputStr[i] == 'x') outputStr += 'x'
			else if (inputStr[i] == 'z') outputStr += 'z'
			else {
				if (dataMap.containsKey(inputStr[i])) {
					outputStr += dataMap[inputStr[i]]
				}
				else {
					dataMap[inputStr[i]] = char
					outputStr += char
					char += 1
				}
			}
		}
		return outputStr
	}

	/**
	 * Check accessibility service is enabled or not
	 */
	private fun isAccessibilityServiceEnabled(): Boolean {
		val serviceList = accessibilityManager.getEnabledAccessibilityServiceList(
			AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
		for (serviceInfo in serviceList) {
			if (serviceInfo.packageNames != null && serviceInfo.packageNames[0] == packageName) return true
		}
		return false
	}
}