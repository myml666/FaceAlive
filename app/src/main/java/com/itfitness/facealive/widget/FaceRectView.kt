package com.itfitness.facealive.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.arcsoft.face.GenderInfo
import com.arcsoft.face.LivenessInfo
import com.itfitness.facealive.bean.DrawInfoBean
import java.util.concurrent.CopyOnWriteArrayList

class FaceRectView : View {
    private var faceRectList = CopyOnWriteArrayList<DrawInfoBean>()
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        mPaint.color = Color.GREEN
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 5f
        mPaint.textSize = 80f
        mPaint.textAlign = Paint.Align.CENTER
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (faceRectList.size > 0) {
            for (drawinfo in faceRectList) {
                drawIsAlive(drawinfo,canvas)
                drawFaceRect(drawinfo,canvas)
                drawGenderAndAge(drawinfo, canvas)
            }
        }
    }

    /**
     * 绘制人脸矩形
     */
    private fun drawFaceRect(drawinfo: DrawInfoBean, canvas: Canvas) {
        mPaint.style = Paint.Style.STROKE
        canvas.drawRect(drawinfo.rect, mPaint)
    }
    /**
     * 绘制是否是活体
     */
    private fun drawIsAlive(drawinfo: DrawInfoBean, canvas: Canvas) {
        mPaint.style = Paint.Style.FILL
        val isAlive = when(drawinfo.isAlive){
            LivenessInfo.ALIVE->{
                mPaint.color = Color.GREEN
                "活体"
            }
            LivenessInfo.NOT_ALIVE->{
                mPaint.color = Color.RED
                "非活体"
            }
            else->{
                mPaint.color = Color.YELLOW
                "未知"
            }
        }
        canvas.drawText(isAlive,(drawinfo.rect.left + drawinfo.rect.width() / 2).toFloat(), drawinfo.rect.top.toFloat() + mPaint.textSize, mPaint)
    }
    /**
     * 绘制性别和年龄
     */
    private fun drawGenderAndAge(drawinfo: DrawInfoBean, canvas: Canvas) {
        mPaint.style = Paint.Style.FILL
        val gender = when (drawinfo.gender) {
            GenderInfo.MALE -> "男"
            GenderInfo.FEMALE -> "女"
            else -> "未知"
        }
        canvas.drawText(
            "性别：$gender 年龄：${drawinfo.age}",
            (drawinfo.rect.left + drawinfo.rect.width() / 2).toFloat(), drawinfo.rect.bottom.toFloat() - 10, mPaint
        )
    }

    /**
     * 清空人脸矩形
     */
    fun clearRect() {
        faceRectList.clear()
        postInvalidate()
    }

    /**
     * 设置人脸绘制数据
     */
    fun setFaceRectDatas(data: CopyOnWriteArrayList<DrawInfoBean>) {
        faceRectList = data
        postInvalidate()
    }
}