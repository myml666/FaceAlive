package com.itfitness.facealive

import android.graphics.Rect
import android.hardware.Camera
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.arcsoft.face.*
import com.itfitness.facealive.bean.DrawInfoBean
import com.itfitness.facealive.widget.PreviewCallBack
import kotlinx.android.synthetic.main.activity_findface.*
import java.util.ArrayList
import java.util.concurrent.CopyOnWriteArrayList

class FindFaceActivity: AppCompatActivity(){

    private var faceEngine:FaceEngine?=null
    private var afCode = -1
    //初始化和调用引擎的属性，年龄检测,初始化和调用引擎的属性，人脸识别,	初始化和调用引擎的属性，性别检测,初始化和调用引擎的属性，RGB活体检测
    private val processMask =
        FaceEngine.ASF_AGE or FaceEngine.ASF_FACE3DANGLE or FaceEngine.ASF_GENDER or FaceEngine.ASF_LIVENESS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findface)
        initEngine()
        initListener()
    }

    private fun initListener() {
        facecamera.preViewCallBack = object : PreviewCallBack {
            override fun onPreview(data: ByteArray, camera: Camera) {
                if (facerectview != null) {
                    facerectview.clearRect()
                }
                val faceInfoList = ArrayList<FaceInfo>()
                //人脸识别
                var code = faceEngine!!.detectFaces(
                    data,//图像数据
                    facecamera.previewSize!!.width,//图像的宽度，为4的倍数
                    facecamera.previewSize!!.height,//图像的高度，NV21(CP_PAF_NV21)格式为2的倍数；BGR24(CP_PAF_BGR24)、GRAY(CP_PAF_GRAY)、DEPTH_U16(CP_PAF_DEPTH_U16)格式无限制
                    FaceEngine.CP_PAF_NV21,// 图像的颜色空间格式，支持NV21(CP_PAF_NV21)、BGR24(CP_PAF_BGR24)、GRAY(CP_PAF_GRAY)、DEPTH_U16(CP_PAF_DEPTH_U16)
                    faceInfoList//人脸列表，传入后赋值
                )
                if (code == ErrorInfo.MOK && faceInfoList.size > 0) {
                    //RGB活体、年龄、性别、三维角度检测，在调用该函数后，可以调用getLiveness(List) ，getAge(List)，getGender(List)，getFace3DAngle(List)分别获取 RGB活体、年龄、性别、三维角度的检测结果；
                    // RGB活体最多支持 1 个人脸信息的检测，超过部分返回未知； 年龄、性别、三维角度最多支持4个人脸信息的检测，超过部分返回未知
                    code = faceEngine!!.process(
                        data,//图像数据
                        camera.parameters.previewSize.width,//图像的宽度，为4的倍数
                        camera.parameters.previewSize.height,//图像的高度，NV21(CP_PAF_NV21)格式为2的倍数，BGR24(CP_PAF_BGR24)格式无限制
                        FaceEngine.CP_PAF_NV21,//图像的颜色空间格式，支持NV21(CP_PAF_NV21)、BGR24(CP_PAF_BGR24)
                        faceInfoList,//人脸列表
                        processMask//检测的属性（ASF_AGE、ASF_GENDER、ASF_FACE3DANGLE、ASF_LIVENESS），支持多选，注：检测的属性须在引擎初始化接口（init(Context, long, int, int, int, int)）的combinedMask参数中启用
                    )
                    if (code != ErrorInfo.MOK) {
                        return
                    }
                } else {
                    return
                }

                val ageInfoList = ArrayList<AgeInfo>()//年龄识别
                val genderInfoList = ArrayList<GenderInfo>()//性别识别
                val face3DAngleList = ArrayList<Face3DAngle>()//3D角度识别
                val faceLivenessInfoList = ArrayList<LivenessInfo>()//活体检测
                val ageCode = faceEngine!!.getAge(ageInfoList)
                val genderCode = faceEngine!!.getGender(genderInfoList)
                val face3DAngleCode = faceEngine!!.getFace3DAngle(face3DAngleList)
                val livenessCode = faceEngine!!.getLiveness(faceLivenessInfoList)

                //有其中一个的错误码不为0，return
                if (ageCode or genderCode or face3DAngleCode or livenessCode != ErrorInfo.MOK) {
                    return
                }

                val drawInfoList = CopyOnWriteArrayList<DrawInfoBean>()
                for (i in faceInfoList.indices) {
                    val adjustRect = adjustRect(faceInfoList[i].rect)
                    val drawInfoBean = DrawInfoBean(adjustRect,ageInfoList[i].age,genderInfoList[i].gender,faceLivenessInfoList[i].liveness)
                    drawInfoList.add(drawInfoBean)
                }
                //绘制人脸区域及人脸信息
                facerectview.setFaceRectDatas(drawInfoList)
            }
        }
    }

    /**
     * 初始化人脸识别引擎
     */
    private fun initEngine() {
        faceEngine = FaceEngine()
        //初始化人脸识别引擎
        afCode = faceEngine!!.init(
            this,
            FaceEngine.ASF_DETECT_MODE_VIDEO,//检测模式，支持VIDEO模式(ASF_DETECT_MODE_VIDEO)和IMAGE模式(ASF_DETECT_MODE_IMAGE)
            FaceEngine.ASF_OP_270_ONLY,//人脸检测角度，支持0度(ASF_OP_0_ONLY)，90度(ASF_OP_90_ONLY)，180度(ASF_OP_180_ONLY)，270度(ASF_OP_270_ONLY)，全角度检测(ASF_OP_0_HIGHER_EXT)，建议使用单一指定角度检测，性能比全角度检测更佳，IMAGE模式（ASF_DETECT_MODE_IMAGE）为了提高检测识别率不支持全角度（ASF_OP_0_HIGHER_EXT）检测
            16,//识别的最小人脸比例（图片长边与人脸框长边的比值），在VIDEO模式(ASF_DETECT_MODE_VIDEO)下有效值范围[2，32]，推荐值16；在IMAGE模式(ASF_DETECT_MODE_IMAGE)下有效值范围[2，32]，推荐值30
            20,// 引擎最多能检测出的人脸数，有效值范围[1,50]
            FaceEngine.ASF_FACE_DETECT or FaceEngine.ASF_AGE or FaceEngine.ASF_FACE3DANGLE or FaceEngine.ASF_GENDER or FaceEngine.ASF_LIVENESS//需要启用的功能组合，可多选
        )
    }

    /**
     * 校正绘制的矩形
     * 由于识别的图像的方向与预览的图像的方向不一致，
     * 所以需要对检测出来的区域进行校正，当然这里我是针对前置摄像头进行校正，如果是后置摄像头则校正方法不一样
     */
    private fun adjustRect(rect: Rect):Rect{
        val justRect = Rect()
        //根据屏幕与分辨率宽高的比值缩放人脸的矩形区域
        val scalWidthVal = facerectview.width.toFloat()/facecamera.previewSize!!.height.toFloat()
        val scalHeightVal = facerectview.height.toFloat()/facecamera.previewSize!!.width.toFloat()
        rect.left = (scalHeightVal*rect.left).toInt()
        rect.right = (scalHeightVal*rect.right).toInt()
        rect.top = (scalWidthVal*rect.top).toInt()
        rect.bottom = (scalWidthVal*rect.bottom).toInt()
        justRect.left  = facerectview.width - rect.top
        justRect.right =facerectview.width -  rect.bottom
        justRect.top = facerectview.height - rect.left
        justRect.bottom = facerectview.height - rect.right
        return justRect
    }

    /**
     * 释放引擎
     */
    private fun unInitEngine() {
        if (afCode == 0) {
            afCode = faceEngine!!.unInit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unInitEngine()
    }
}