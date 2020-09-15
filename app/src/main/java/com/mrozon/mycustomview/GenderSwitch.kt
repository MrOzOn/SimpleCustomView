package com.mrozon.mycustomview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import timber.log.Timber


class GenderSwitch(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paintChecked : Paint = Paint()
    private val paintUnchecked : Paint = Paint()
    private var mIsMale :  Boolean = true

    fun isMale(): Boolean {
        return mIsMale
    }

    fun setMale(male: Boolean) {
        mIsMale = male
        invalidate()
        requestLayout()
    }

    init {
        val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.GenderSwitch)
        try {
            paintChecked.color = typedArray!!.getColor(R.styleable.GenderSwitch_colorChecked,
                Color.parseColor("#03A9F4"))
            paintUnchecked.color = typedArray.getColor(R.styleable.GenderSwitch_colorUnchecked,
                Color.parseColor("#E0E0E0"))
            mIsMale = typedArray.getBoolean(R.styleable.GenderSwitch_isMale, true)
        } finally {
            typedArray?.recycle()
        }
    }

    private val paintActive : Paint = Paint().apply {
    }

    private val paintInactive : Paint = Paint().apply {
        alpha = 75
    }

    private var bitmapMale:Bitmap? = null
    private var bitmapFemale:Bitmap? = null
    private var sizeBitmap = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Timber.d("onMeasure")

        val heightSize = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
        val widthSize = (MeasureSpec.getSize(widthMeasureSpec) - 2 * paddingStart - 2 * paddingEnd) / 2.5
        sizeBitmap = minOf(heightSize, widthSize.toInt())
        bitmapMale = getBitmapFromVectorDrawable(context, R.drawable.ic_male_avatar,
            sizeBitmap)
//        bitmapFemale?.reconfigure(100,100,Bitmap.Config.ARGB_8888)
        bitmapFemale = getBitmapFromVectorDrawable(context, R.drawable.ic_female_avatar,
            sizeBitmap)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        Timber.d("onLayout")
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        Timber.d("onDraw")
        super.onDraw(canvas)

        val top = (height - sizeBitmap)/2F
        val widthCheckedArea = width-(sizeBitmap+paddingStart+paddingEnd*1F)
        val widthUncheckedArea = width - widthCheckedArea


        canvas.apply {
            drawRect(0F, 0F, width.toFloat(), height.toFloat(), paintUnchecked)

            if(mIsMale){
                drawRect(0F,
                    0F,
                    widthCheckedArea,
                    height.toFloat(),
                    paintChecked)
                drawBitmap(bitmapMale!!,(widthCheckedArea-bitmapMale?.width!!)/2,top,paintActive)
                drawBitmap(bitmapFemale!!,widthCheckedArea + (widthUncheckedArea - bitmapFemale?.width!!)/2F,top,paintInactive)
            }
            else
            {
                drawRect(widthUncheckedArea,
                    0F,
                    width.toFloat(),
                    height.toFloat(),
                    paintChecked)
                drawBitmap(bitmapMale!!,(widthUncheckedArea-bitmapMale?.width!!)/2,top,paintInactive)
                drawBitmap(bitmapFemale!!,widthUncheckedArea + (widthCheckedArea - bitmapFemale?.width!!)/2F,top,paintActive)

            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int, width:Int): Bitmap? {
        var drawable = ContextCompat.getDrawable(context!!, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }
        val bitmap = Bitmap.createBitmap(
            width,
            width,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable?.setBounds(0, 0, canvas.width, canvas.height)
        drawable?.draw(canvas)
        return bitmap
    }

    private fun dip2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    private val myListener =  object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            Timber.d("onDown: $e")
            return true
        }
    }
    private val detector: GestureDetector = GestureDetector(context, myListener)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return detector.onTouchEvent(event).let { result ->
            if (!result) {
                if (event?.action == MotionEvent.ACTION_UP) {
                    Timber.d("ACTION_UP: $event")
                    mIsMale = !mIsMale
                    invalidate()
                    true
                } else false
            } else true
        }
    }
}