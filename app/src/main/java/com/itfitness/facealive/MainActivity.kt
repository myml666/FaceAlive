package com.itfitness.facealive

import android.Manifest
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
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private val faceEngine = FaceEngine()
    companion object{
        const val PERMISSION_CODE = 100//权限申请码
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListener()
    }

    private fun initListener() {
        bt_activeengine.setOnClickListener {
            requiresPermission()
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

    /**
     * 申请权限
     */
    @AfterPermissionGranted(PERMISSION_CODE)
    private fun requiresPermission() {
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            activeengine()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this, PERMISSION_CODE, *perms)
                    .setRationale("相机、手机状态、读写内存卡")
                    .setPositiveButtonText("确定")
                    .setNegativeButtonText("取消")
                    .build())
        }
    }

    /**
     * 激活引擎
     */
    private fun activeengine() {
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

    //接受系统权限的处理，这里交给EasyPermissions来处理，回调到 EasyPermissions.PermissionCallbacks接口
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,this)//注意这个this，内部对实现该方法进行了查询，所以没有this的话，回调结果的方法不生效
    }
    /**
     * 拒绝权限
     */
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    /**
     * 同意权限
     */
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

}
