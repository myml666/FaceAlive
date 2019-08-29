package com.itfitness.facealive

import android.hardware.Camera
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.itfitness.facealive.widget.PreviewCallBack
import kotlinx.android.synthetic.main.activity_findface.*

class FindFaceActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findface)
        initListener()
    }

    private fun initListener() {
        facecamera.preViewCallBack = object : PreviewCallBack {
            override fun onPreview(data: ByteArray, camera: Camera) {

            }
        }
    }
}