package com.itfitness.facealive.widget

import android.hardware.Camera

interface PreviewCallBack {
    /**
     * 相机预览数据回调
     */
    fun onPreview(data:ByteArray,camera:Camera)
}