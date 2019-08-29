package com.itfitness.facealive.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.itfitness.facealive.bean.DrawInfoBean
import java.util.concurrent.CopyOnWriteArrayList
class FaceRectView:View {
    private var faceRectList = CopyOnWriteArrayList<DrawInfoBean>()
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    init {
        mPaint.color = Color.GREEN
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 5f
    }
    constructor(context: Context?):super(context)
    constructor(context: Context?, attrs: AttributeSet?):super(context,attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int):super(context,attrs,defStyleAttr)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (faceRectList.size>0){
            for (drawinfo in faceRectList){

            }
        }
    }
    /**
     * 清空人脸矩形
     */
    fun clearRect(){
        faceRectList.clear()
        postInvalidate()
    }

    /**
     * 设置人脸绘制数据
     */
    fun setFaceRectDatas(data:List<DrawInfoBean>){
        faceRectList = data as CopyOnWriteArrayList<DrawInfoBean>
        postInvalidate()
    }
}