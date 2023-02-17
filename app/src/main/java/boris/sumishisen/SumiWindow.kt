package boris.sumishisen

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources
import boris.sumishisen.databinding.SumiLayoutBinding
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

class SumiWindow : Service() {
	private var windowManager : WindowManager? = null
	private var iconView : View? = null
	private var sumiView : View? = null
	private var _binding : SumiLayoutBinding? = null
	private val binding get() = _binding!!
	private lateinit var metrics : DisplayMetrics
	private val screenWidth get() = metrics.widthPixels
	private val screenHeight get() = metrics.heightPixels
	private var iconLayoutParams : WindowManager.LayoutParams? = null
	private var windowLayoutParams : WindowManager.LayoutParams? = null
	private var isClick = false
	private val idMap = mutableMapOf<Int, Int>()
	private var chooseID = 0
	private val inputCounter = mutableMapOf<Int, Int>()
	private val dx = intArrayOf(0, -1, 0, 1)
	private val dy = intArrayOf(1, 0, -1, 0)
	private val m = Array(8) { Array(13) { 'x' } }
	private val mm = Array(8) { Array(13) { 'x' } }
	private val n = Array(8) { Array(13) { false } }
	private var rx : Int = 0
	private var ry : Int = 0
	private var q = LinkedList<Pair<Int, Int>>()
	private var ans = LinkedList<Pair<Int, Int>>()
	private var ansInd = 0
	private var status = -1
	
	override fun onBind(intent : Intent) : IBinder? {
		return null
	}
	
	override fun onConfigurationChanged(newConfig : Configuration) {
		super.onConfigurationChanged(newConfig)
		metrics = applicationContext.resources.displayMetrics
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.d("config", "land")
		}
		else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.d("config", "port")
			
		}
		iconLayoutParams!!.apply {
			x = (x/abs(x))*screenWidth/2
			y = (y.toDouble()/screenWidth.toDouble()*screenHeight.toDouble()).toInt()
		}
		windowManager?.updateViewLayout(iconView, iconLayoutParams)
	}
	
	@SuppressLint("InflateParams", "ClickableViewAccessibility")
	override fun onCreate() {
		super.onCreate()
		metrics = applicationContext.resources.displayMetrics
		Log.d("width", screenWidth.toString())
		Log.d("height", screenHeight.toString())
		
		windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
		iconView = (baseContext.getSystemService(
			LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.sumi_icon, null)
		sumiView = (baseContext.getSystemService(
			LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.sumi_layout, null)
		_binding = SumiLayoutBinding.bind(sumiView!!)
		WindowManager.LayoutParams.TYPE_APPLICATION
		iconLayoutParams = WindowManager.LayoutParams(
			WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT)
		iconLayoutParams!!.apply {
			gravity = Gravity.CENTER
			x = screenWidth/2
			y = 0
			this.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50F, metrics)
				.roundToInt()
			this.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50F, metrics)
				.roundToInt()
		}
		
		windowLayoutParams = WindowManager.LayoutParams(
			WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.RGBA_F16)
		windowLayoutParams!!.apply {
			gravity = Gravity.CENTER
			x = 0
			y = 0
			screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
			this.height = (screenWidth*0.85f).toInt()
			this.width = (screenHeight*0.85f).toInt()
		}
		
		windowManager?.addView(iconView, iconLayoutParams)
		
		iconView?.setBackgroundColor(getColor(R.color.trans))
		iconView?.background = AppCompatResources.getDrawable(this, R.drawable.ic_sumi)
		iconView?.setOnTouchListener(object : View.OnTouchListener {
			val iconLayoutUpdateParams : WindowManager.LayoutParams = iconLayoutParams!!
			var sx = 0.0
			var sy = 0.0
			var px = 0.0
			var py = 0.0
			
			override fun onTouch(v : View?, event : MotionEvent) : Boolean {
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
						windowManager?.updateViewLayout(iconView, iconLayoutUpdateParams)
					}
					MotionEvent.ACTION_UP   -> {
						//Log.d("move", "${iconLayoutUpdateParams.x}, ${iconLayoutUpdateParams.y}")
						if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
							if (iconLayoutUpdateParams.x in -75..75 && iconLayoutUpdateParams.y in screenHeight/2-300..screenHeight/2) {
								stopSelf()
							}
						}
						if (iconLayoutUpdateParams.x < 0) {
							iconLayoutUpdateParams.x = -screenWidth/2
							windowManager?.updateViewLayout(iconView, iconLayoutUpdateParams)
						}
						else if (iconLayoutUpdateParams.x >= 0) {
							iconLayoutUpdateParams.x = screenWidth/2
							windowManager?.updateViewLayout(iconView, iconLayoutUpdateParams)
						}
						if (abs(event.rawY.toDouble()-py) < 50) {
							if (isClick) {
								isClick = false
								windowManager?.removeView(sumiView)
							}
							else {
								isClick = true
								windowManager?.addView(sumiView, windowLayoutParams)
							}
							windowManager?.updateViewLayout(iconView, iconLayoutUpdateParams)
						}
					}
				}
				return false
			}
		})
		
		val chooseListener = View.OnClickListener { view ->
			if (chooseID != 0) binding.root.findViewById<Button>(
				chooseID).backgroundTintList = ColorStateList.valueOf(getColor(R.color.purple_200))
			view.backgroundTintList = ColorStateList.valueOf(getColor(R.color.accent))
			chooseID = view.id
		}
		
		val fillListener = View.OnClickListener { view ->
			if (chooseID != 0) {
				when (chooseID) {
					binding.btnDel.id -> {
						if ((view as Button).hint.toString()[0].minus('a') < 14) {
							val btnID = idMap[view.hint.toString()[0].minus('a')]
							if (inputCounter[btnID!!]!! > 0) inputCounter[btnID] = inputCounter[btnID]!!-1
							binding.root.findViewById<Button>(btnID).visibility = View.VISIBLE
						}
						view.text = "-"
						view.hint = "x"
					}
					binding.btnBox.id -> {
						(view as Button).text = binding.root.findViewById<Button>(chooseID).text
						view.hint = binding.root.findViewById<Button>(chooseID).hint
					}
					else              -> {
						(view as Button).text = binding.root.findViewById<Button>(chooseID).text
						view.hint = binding.root.findViewById<Button>(chooseID).hint
						inputCounter[chooseID] = inputCounter[chooseID]!!+1
						if (inputCounter[chooseID] == 4) {
							binding.root.findViewById<Button>(chooseID).apply {
								backgroundTintList = ColorStateList.valueOf(
									getColor(R.color.purple_200))
								visibility = View.INVISIBLE
							}
							chooseID = 0
						}
					}
				}
			}
		}
		
		binding.btnA.setOnClickListener(chooseListener)
		binding.btnB.setOnClickListener(chooseListener)
		binding.btnC.setOnClickListener(chooseListener)
		binding.btnD.setOnClickListener(chooseListener)
		binding.btnE.setOnClickListener(chooseListener)
		binding.btnF.setOnClickListener(chooseListener)
		binding.btnG.setOnClickListener(chooseListener)
		binding.btnH.setOnClickListener(chooseListener)
		binding.btnI.setOnClickListener(chooseListener)
		binding.btnJ.setOnClickListener(chooseListener)
		binding.btnK.setOnClickListener(chooseListener)
		binding.btnL.setOnClickListener(chooseListener)
		binding.btnM.setOnClickListener(chooseListener)
		binding.btnN.setOnClickListener(chooseListener)
		binding.btnO.setOnClickListener(chooseListener)
		binding.btnBox.setOnClickListener(chooseListener)
		binding.btnDel.setOnClickListener(chooseListener)
		binding.btnDel.setOnLongClickListener {
			binding.btn101.apply { text = "-"; hint = "x" }
			binding.btn102.apply { text = "-"; hint = "x" }
			binding.btn103.apply { text = "-"; hint = "x" }
			binding.btn104.apply { text = "-"; hint = "x" }
			binding.btn105.apply { text = "-"; hint = "x" }
			binding.btn106.apply { text = "-"; hint = "x" }
			binding.btn107.apply { text = "-"; hint = "x" }
			binding.btn108.apply { text = "-"; hint = "x" }
			binding.btn109.apply { text = "-"; hint = "x" }
			binding.btn110.apply { text = "-"; hint = "x" }
			binding.btn111.apply { text = "-"; hint = "x" }
			binding.btn201.apply { text = "-"; hint = "x" }
			binding.btn202.apply { text = "-"; hint = "x" }
			binding.btn203.apply { text = "-"; hint = "x" }
			binding.btn204.apply { text = "-"; hint = "x" }
			binding.btn205.apply { text = "-"; hint = "x" }
			binding.btn206.apply { text = "-"; hint = "x" }
			binding.btn207.apply { text = "-"; hint = "x" }
			binding.btn208.apply { text = "-"; hint = "x" }
			binding.btn209.apply { text = "-"; hint = "x" }
			binding.btn210.apply { text = "-"; hint = "x" }
			binding.btn211.apply { text = "-"; hint = "x" }
			binding.btn301.apply { text = "-"; hint = "x" }
			binding.btn302.apply { text = "-"; hint = "x" }
			binding.btn303.apply { text = "-"; hint = "x" }
			binding.btn304.apply { text = "-"; hint = "x" }
			binding.btn305.apply { text = "-"; hint = "x" }
			binding.btn306.apply { text = "-"; hint = "x" }
			binding.btn307.apply { text = "-"; hint = "x" }
			binding.btn308.apply { text = "-"; hint = "x" }
			binding.btn309.apply { text = "-"; hint = "x" }
			binding.btn310.apply { text = "-"; hint = "x" }
			binding.btn311.apply { text = "-"; hint = "x" }
			binding.btn401.apply { text = "-"; hint = "x" }
			binding.btn402.apply { text = "-"; hint = "x" }
			binding.btn403.apply { text = "-"; hint = "x" }
			binding.btn404.apply { text = "-"; hint = "x" }
			binding.btn405.apply { text = "-"; hint = "x" }
			binding.btn406.apply { text = "-"; hint = "x" }
			binding.btn407.apply { text = "-"; hint = "x" }
			binding.btn408.apply { text = "-"; hint = "x" }
			binding.btn409.apply { text = "-"; hint = "x" }
			binding.btn410.apply { text = "-"; hint = "x" }
			binding.btn411.apply { text = "-"; hint = "x" }
			binding.btn501.apply { text = "-"; hint = "x" }
			binding.btn502.apply { text = "-"; hint = "x" }
			binding.btn503.apply { text = "-"; hint = "x" }
			binding.btn504.apply { text = "-"; hint = "x" }
			binding.btn505.apply { text = "-"; hint = "x" }
			binding.btn506.apply { text = "-"; hint = "x" }
			binding.btn507.apply { text = "-"; hint = "x" }
			binding.btn508.apply { text = "-"; hint = "x" }
			binding.btn509.apply { text = "-"; hint = "x" }
			binding.btn510.apply { text = "-"; hint = "x" }
			binding.btn511.apply { text = "-"; hint = "x" }
			binding.btn601.apply { text = "-"; hint = "x" }
			binding.btn602.apply { text = "-"; hint = "x" }
			binding.btn603.apply { text = "-"; hint = "x" }
			binding.btn604.apply { text = "-"; hint = "x" }
			binding.btn605.apply { text = "-"; hint = "x" }
			binding.btn606.apply { text = "-"; hint = "x" }
			binding.btn607.apply { text = "-"; hint = "x" }
			binding.btn608.apply { text = "-"; hint = "x" }
			binding.btn609.apply { text = "-"; hint = "x" }
			binding.btn610.apply { text = "-"; hint = "x" }
			binding.btn611.apply { text = "-"; hint = "x" }
			binding.btnA.visibility = View.VISIBLE
			binding.btnB.visibility = View.VISIBLE
			binding.btnC.visibility = View.VISIBLE
			binding.btnD.visibility = View.VISIBLE
			binding.btnE.visibility = View.VISIBLE
			binding.btnF.visibility = View.VISIBLE
			binding.btnG.visibility = View.VISIBLE
			binding.btnH.visibility = View.VISIBLE
			binding.btnI.visibility = View.VISIBLE
			binding.btnJ.visibility = View.VISIBLE
			binding.btnK.visibility = View.VISIBLE
			binding.btnL.visibility = View.VISIBLE
			binding.btnM.visibility = View.VISIBLE
			binding.btnN.visibility = View.VISIBLE
			binding.btnO.visibility = View.VISIBLE
			inputCounter[binding.btnA.id] = 0
			inputCounter[binding.btnB.id] = 0
			inputCounter[binding.btnC.id] = 0
			inputCounter[binding.btnD.id] = 0
			inputCounter[binding.btnE.id] = 0
			inputCounter[binding.btnF.id] = 0
			inputCounter[binding.btnG.id] = 0
			inputCounter[binding.btnH.id] = 0
			inputCounter[binding.btnI.id] = 0
			inputCounter[binding.btnJ.id] = 0
			inputCounter[binding.btnK.id] = 0
			inputCounter[binding.btnL.id] = 0
			inputCounter[binding.btnM.id] = 0
			inputCounter[binding.btnN.id] = 0
			inputCounter[binding.btnO.id] = 0
			return@setOnLongClickListener true
		}
		binding.btnOk.setOnClickListener {
			viewOutput()
			binding.textViewInfo.text = getString(R.string.text_waiting)
			
			status = if (cal(output())) 1
			else 0
			if (status == 1) {
				if (ans.size > 0) {
					binding.textViewInfo.text = getString(R.string.text_success)
					showAnswer(ansInd)
				}
				else status = 0
			}
			if (status == 0) binding.textViewInfo.text = getString(R.string.text_fail)
			
		}
		binding.btnPre.setOnClickListener {
			if (status == 1) {
				hideAnswer(ansInd)
				if (ansInd > 0) ansInd -= 1
				showAnswer(ansInd)
			}
		}
		binding.btnNext.setOnClickListener {
			if (status == 1) {
				hideAnswer(ansInd)
				if (ansInd < ans.size/2-1) ansInd += 1
				showAnswer(ansInd)
			}
		}
		binding.btnClose.setOnClickListener {
			viewInput()
			if (status == 1) hideAnswer(ansInd)
			status = -1
			ansInd = 0
		}
		
		viewInput()
		kotlin.run {
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
			inputCounter[binding.btnA.id] = 0
			inputCounter[binding.btnB.id] = 0
			inputCounter[binding.btnC.id] = 0
			inputCounter[binding.btnD.id] = 0
			inputCounter[binding.btnE.id] = 0
			inputCounter[binding.btnF.id] = 0
			inputCounter[binding.btnG.id] = 0
			inputCounter[binding.btnH.id] = 0
			inputCounter[binding.btnI.id] = 0
			inputCounter[binding.btnJ.id] = 0
			inputCounter[binding.btnK.id] = 0
			inputCounter[binding.btnL.id] = 0
			inputCounter[binding.btnM.id] = 0
			inputCounter[binding.btnN.id] = 0
			inputCounter[binding.btnO.id] = 0
			binding.btn101.setOnClickListener(fillListener)
			binding.btn102.setOnClickListener(fillListener)
			binding.btn103.setOnClickListener(fillListener)
			binding.btn104.setOnClickListener(fillListener)
			binding.btn105.setOnClickListener(fillListener)
			binding.btn106.setOnClickListener(fillListener)
			binding.btn107.setOnClickListener(fillListener)
			binding.btn108.setOnClickListener(fillListener)
			binding.btn109.setOnClickListener(fillListener)
			binding.btn110.setOnClickListener(fillListener)
			binding.btn111.setOnClickListener(fillListener)
			binding.btn201.setOnClickListener(fillListener)
			binding.btn202.setOnClickListener(fillListener)
			binding.btn203.setOnClickListener(fillListener)
			binding.btn204.setOnClickListener(fillListener)
			binding.btn205.setOnClickListener(fillListener)
			binding.btn206.setOnClickListener(fillListener)
			binding.btn207.setOnClickListener(fillListener)
			binding.btn208.setOnClickListener(fillListener)
			binding.btn209.setOnClickListener(fillListener)
			binding.btn210.setOnClickListener(fillListener)
			binding.btn211.setOnClickListener(fillListener)
			binding.btn301.setOnClickListener(fillListener)
			binding.btn302.setOnClickListener(fillListener)
			binding.btn303.setOnClickListener(fillListener)
			binding.btn304.setOnClickListener(fillListener)
			binding.btn305.setOnClickListener(fillListener)
			binding.btn306.setOnClickListener(fillListener)
			binding.btn307.setOnClickListener(fillListener)
			binding.btn308.setOnClickListener(fillListener)
			binding.btn309.setOnClickListener(fillListener)
			binding.btn310.setOnClickListener(fillListener)
			binding.btn311.setOnClickListener(fillListener)
			binding.btn401.setOnClickListener(fillListener)
			binding.btn402.setOnClickListener(fillListener)
			binding.btn403.setOnClickListener(fillListener)
			binding.btn404.setOnClickListener(fillListener)
			binding.btn405.setOnClickListener(fillListener)
			binding.btn406.setOnClickListener(fillListener)
			binding.btn407.setOnClickListener(fillListener)
			binding.btn408.setOnClickListener(fillListener)
			binding.btn409.setOnClickListener(fillListener)
			binding.btn410.setOnClickListener(fillListener)
			binding.btn411.setOnClickListener(fillListener)
			binding.btn501.setOnClickListener(fillListener)
			binding.btn502.setOnClickListener(fillListener)
			binding.btn503.setOnClickListener(fillListener)
			binding.btn504.setOnClickListener(fillListener)
			binding.btn505.setOnClickListener(fillListener)
			binding.btn506.setOnClickListener(fillListener)
			binding.btn507.setOnClickListener(fillListener)
			binding.btn508.setOnClickListener(fillListener)
			binding.btn509.setOnClickListener(fillListener)
			binding.btn510.setOnClickListener(fillListener)
			binding.btn511.setOnClickListener(fillListener)
			binding.btn601.setOnClickListener(fillListener)
			binding.btn602.setOnClickListener(fillListener)
			binding.btn603.setOnClickListener(fillListener)
			binding.btn604.setOnClickListener(fillListener)
			binding.btn605.setOnClickListener(fillListener)
			binding.btn606.setOnClickListener(fillListener)
			binding.btn607.setOnClickListener(fillListener)
			binding.btn608.setOnClickListener(fillListener)
			binding.btn609.setOnClickListener(fillListener)
			binding.btn610.setOnClickListener(fillListener)
			binding.btn611.setOnClickListener(fillListener)
		}
	}
	
	override fun onDestroy() {
		super.onDestroy()
		stopSelf()
		windowManager?.removeView(iconView)
		if (isClick) windowManager?.removeView(sumiView)
	}
	
	private fun viewInput() {
		binding.btnA.visibility = View.VISIBLE
		binding.btnB.visibility = View.VISIBLE
		binding.btnC.visibility = View.VISIBLE
		binding.btnD.visibility = View.VISIBLE
		binding.btnE.visibility = View.VISIBLE
		binding.btnF.visibility = View.VISIBLE
		binding.btnG.visibility = View.VISIBLE
		binding.btnH.visibility = View.VISIBLE
		binding.btnI.visibility = View.VISIBLE
		binding.btnJ.visibility = View.VISIBLE
		binding.btnK.visibility = View.VISIBLE
		binding.btnL.visibility = View.VISIBLE
		binding.btnM.visibility = View.VISIBLE
		binding.btnN.visibility = View.VISIBLE
		binding.btnO.visibility = View.VISIBLE
		binding.btnBox.visibility = View.VISIBLE
		binding.btnDel.visibility = View.VISIBLE
		binding.btnOk.visibility = View.VISIBLE
		binding.textViewInfo.visibility = View.INVISIBLE
		binding.btnPre.visibility = View.INVISIBLE
		binding.btnNext.visibility = View.INVISIBLE
		binding.btnClose.visibility = View.INVISIBLE
	}
	
	private fun viewOutput() {
		binding.btnA.visibility = View.INVISIBLE
		binding.btnB.visibility = View.INVISIBLE
		binding.btnC.visibility = View.INVISIBLE
		binding.btnD.visibility = View.INVISIBLE
		binding.btnE.visibility = View.INVISIBLE
		binding.btnF.visibility = View.INVISIBLE
		binding.btnG.visibility = View.INVISIBLE
		binding.btnH.visibility = View.INVISIBLE
		binding.btnI.visibility = View.INVISIBLE
		binding.btnJ.visibility = View.INVISIBLE
		binding.btnK.visibility = View.INVISIBLE
		binding.btnL.visibility = View.INVISIBLE
		binding.btnM.visibility = View.INVISIBLE
		binding.btnN.visibility = View.INVISIBLE
		binding.btnO.visibility = View.INVISIBLE
		binding.btnBox.visibility = View.INVISIBLE
		binding.btnDel.visibility = View.INVISIBLE
		binding.btnOk.visibility = View.INVISIBLE
		binding.textViewInfo.visibility = View.VISIBLE
		binding.btnPre.visibility = View.VISIBLE
		binding.btnNext.visibility = View.VISIBLE
		binding.btnClose.visibility = View.VISIBLE
	}
	
	private fun showAnswer(ind : Int) {
		if (ind in 0 until ans.size) {
			val btnID1 = ans[ind*2].first*100+ans[ind*2].second
			val btnID2 = ans[ind*2+1].first*100+ans[ind*2+1].second
			if (idMap.contains(btnID1)) binding.root.findViewById<Button>(
				idMap[btnID1]!!).backgroundTintList = ColorStateList.valueOf(
				getColor(R.color.accent))
			if (idMap.contains(btnID2)) binding.root.findViewById<Button>(
				idMap[btnID2]!!).backgroundTintList = ColorStateList.valueOf(
				getColor(R.color.accent))
		}
	}
	
	private fun hideAnswer(ind : Int) {
		if (ind in 0 until ans.size) {
			val btnID1 = ans[ind*2].first*100+ans[ind*2].second
			val btnID2 = ans[ind*2+1].first*100+ans[ind*2+1].second
			if (idMap.contains(btnID1)) binding.root.findViewById<Button>(
				idMap[btnID1]!!).backgroundTintList = ColorStateList.valueOf(
				getColor(R.color.btn_input))
			if (idMap.contains(btnID2)) binding.root.findViewById<Button>(
				idMap[btnID2]!!).backgroundTintList = ColorStateList.valueOf(
				getColor(R.color.btn_input))
		}
	}
	
	private fun output() : String {
		var str = ""
		str += binding.btn101.hint
		str += binding.btn102.hint
		str += binding.btn103.hint
		str += binding.btn104.hint
		str += binding.btn105.hint
		str += binding.btn106.hint
		str += binding.btn107.hint
		str += binding.btn108.hint
		str += binding.btn109.hint
		str += binding.btn110.hint
		str += binding.btn111.hint
		str += binding.btn201.hint
		str += binding.btn202.hint
		str += binding.btn203.hint
		str += binding.btn204.hint
		str += binding.btn205.hint
		str += binding.btn206.hint
		str += binding.btn207.hint
		str += binding.btn208.hint
		str += binding.btn209.hint
		str += binding.btn210.hint
		str += binding.btn211.hint
		str += binding.btn301.hint
		str += binding.btn302.hint
		str += binding.btn303.hint
		str += binding.btn304.hint
		str += binding.btn305.hint
		str += binding.btn306.hint
		str += binding.btn307.hint
		str += binding.btn308.hint
		str += binding.btn309.hint
		str += binding.btn310.hint
		str += binding.btn311.hint
		str += binding.btn401.hint
		str += binding.btn402.hint
		str += binding.btn403.hint
		str += binding.btn404.hint
		str += binding.btn405.hint
		str += binding.btn406.hint
		str += binding.btn407.hint
		str += binding.btn408.hint
		str += binding.btn409.hint
		str += binding.btn410.hint
		str += binding.btn411.hint
		str += binding.btn501.hint
		str += binding.btn502.hint
		str += binding.btn503.hint
		str += binding.btn504.hint
		str += binding.btn505.hint
		str += binding.btn506.hint
		str += binding.btn507.hint
		str += binding.btn508.hint
		str += binding.btn509.hint
		str += binding.btn510.hint
		str += binding.btn511.hint
		str += binding.btn601.hint
		str += binding.btn602.hint
		str += binding.btn603.hint
		str += binding.btn604.hint
		str += binding.btn605.hint
		str += binding.btn606.hint
		str += binding.btn607.hint
		str += binding.btn608.hint
		str += binding.btn609.hint
		str += binding.btn610.hint
		str += binding.btn611.hint
		Log.d("output", str)
		return str
	}
	
	private fun cal(inputStr : String) : Boolean {
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
						if (m[x][y] == 'x' || m[x][y] == 'z') continue
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
	
	private fun check(x : Int, y : Int, value : Char, dir : Int, corner : Int) : Boolean {
		var posx = x
		var posy = y
		n[posx][posy] = true
		while (true) {
			posx += dx[dir]
			posy += dy[dir]
			if (posx < 0 || posx >= 8) break
			if (posy < 0 || posy >= 13) break
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
	
	private fun connect(x : Int, y : Int) {
		m[x][y] = 'x'
		m[rx][ry] = 'x'
		ans.addLast(Pair(x, y))
		ans.addLast(Pair(rx, ry))
		//printTest()
		for (i in 0 until 4) {
			var posx = x
			var posy = y
			while (true) {
				posx += dx[i]
				posy += dy[i]
				if (posx <= 0 || posx >= 7) break
				if (posy <= 0 || posy >= 12) break
				if (m[posx][posy] != 'x' && m[posx][posy] != 'z') {
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
				if (posx <= 0 || posx >= 7) break
				if (posy <= 0 || posy >= 12) break
				if (m[posx][posy] != 'x' && m[posx][posy] != 'z') {
					q.addLast(Pair(posx, posy))
				}
			}
		}
	}
	
	private fun isEmpty() : Boolean {
		for (i in 1..6) {
			for (j in 1..11) {
				if (m[i][j] != 'x' && m[i][j] != 'z') return false
			}
		}
		return true
	}
}