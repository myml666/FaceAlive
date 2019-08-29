package com.itfitness.facealive

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.arcsoft.face.ActiveFileInfo
import com.arcsoft.face.ErrorInfo
import com.arcsoft.face.FaceEngine
import com.itfitness.facealive.common.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val faceEngine = FaceEngine()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListener()
    }

    private fun initListener() {
        bt_activeengine.setOnClickListener {
            //激活引擎
            Thread(Runnable {
                val activeCode = faceEngine.activeOnline(this@MainActivity, Constants.APP_ID, Constants.SDK_KEY)
                val msg = if(activeCode == ErrorInfo.MOK){
                    "引擎激活成功"
                }else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED){
                    "引擎已经激活无需再次激活"
                }else{
                    "引擎激活失败"
                }
                val activeFileInfo = ActiveFileInfo()
                val res = faceEngine.getActiveFileInfo(this@MainActivity, activeFileInfo)
                if (res == ErrorInfo.MOK) {
                    Log.i("激活信息", activeFileInfo.toString())
                }
                runOnUiThread {
                    Toast.makeText(this@MainActivity,msg,Toast.LENGTH_SHORT).show()
                }
            }).start()
        }
        bt_findface.setOnClickListener {
            gotoActivity(FindFaceActivity::class.java)
        }
    }

    /**
     * 跳转Activity
     */
    private fun gotoActivity(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
    }
}
