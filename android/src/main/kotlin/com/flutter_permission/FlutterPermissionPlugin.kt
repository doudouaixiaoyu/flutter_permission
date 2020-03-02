package com.flutter_permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log

import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.lang.ref.WeakReference


/** FlutterPermissionPlugin */
class FlutterPermissionPlugin : FlutterPlugin, ActivityAware, MethodCallHandler {

    private var activity: Activity? = null
    private var resultBack: Result? = null
    private var isRequestResult: Boolean = false
    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        val channel = MethodChannel(flutterPluginBinding.flutterEngine.dartExecutor, "flutter_permission")
        channel.setMethodCallHandler(this)
    }

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            // 注册通道,没有变
            val channel = MethodChannel(registrar.messenger(), "flutter_permission")
            // 注册通道的类,没有变
            channel.setMethodCallHandler(FlutterPermissionPlugin())
        }

        const val REQUEST_PERMISSION_CODE = 1
    }

    // 传输方法名
    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "getAppVersion" -> result.success(getAppVersion(activity!!.applicationContext))
            "checkPermission" -> result.success(checkPermission(call.arguments()))
            "requestPermission" -> {
                resultBack = result
                requestPermission(call.arguments())
            }
            else -> result.notImplemented()
        }

    }

    private fun getAppVersion(context: Context): Long {
        var appVersionCode: Long = 0

        try {
            val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            appVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("", e.message)
        }
        return appVersionCode
    }

    private fun checkPermission(permission: String): Boolean {

        // 如果 被checkSelfPermission函数检测到应用程序 有WRITE_EXTERNAL_STORAGE：写入外部储存器权限,则返回PackageManager.PERMISSION_GRANTED，否则返回PERMISSION_DENIED
        if (ContextCompat.checkSelfPermission(activity!!.applicationContext, permission) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun requestPermission(permission: String) {
        ActivityCompat.requestPermissions(activity!!, arrayOf(permission), REQUEST_PERMISSION_CODE)
    }

    private fun processResult() {
        resultBack?.success(isRequestResult)
        resultBack = null
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        // TODO 给你的插件一个activity 用来执行一些任务
        if (activity != null) return
        activity = binding.activity

        // 添加请求权限回调，当用户没有同意的时候，返回false，下次申请的时候继续弹出对话框，当用户同意的时候，返回true，下次申请不执行任何操作，依旧返回true
        binding.addRequestPermissionsResultListener { requestCode, permissions, grantResults ->
            when (requestCode) {
                REQUEST_PERMISSION_CODE -> {
                    isRequestResult = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    processResult()
                }
                else -> {

                }
            }
            true
        }
    }

    override fun onDetachedFromActivityForConfigChanges() {
        // TODO 当给你的activity，你需要销毁,以便更改配置
    }


    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        // TODO 给你一个activity，用来更改配置

    }

    override fun onDetachedFromActivity() {
        // TODO 当你的任务结束后,不在需要activity的时候,销毁它
        activity = null
    }

}
