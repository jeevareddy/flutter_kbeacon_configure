package com.example.kbeacon_configure

import android.app.Activity
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** KbeaconConfigurePlugin */
class KbeaconConfigurePlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var mMethodChannel: MethodChannel
    private lateinit var mEventChannel: EventChannel
    private var mKBconfigure = KbeaconConfigure()
    private var mActivity: Activity? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        mMethodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "kbeacon_configure")
        mEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "beacons")
        mEventChannel.setStreamHandler(mKBconfigure.mBeaconsDelegate)
        mMethodChannel.setMethodCallHandler(this)
        mKBconfigure.initialize(flutterPluginBinding.applicationContext)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        mActivity = binding.activity
        mKBconfigure.requestPermissions(mActivity!!)
    }

    override fun onDetachedFromActivityForConfigChanges() {

    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        mActivity = binding.activity
    }

    override fun onDetachedFromActivity() {

    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {

        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "initialize" -> {
                mKBconfigure.initialize(mActivity!!.applicationContext)
                result.success(true)
            }
            "startScan" -> {
                mKBconfigure.startScanning(mActivity!!.applicationContext)
                result.success(true)
            }
            "stopScan" -> {
                mKBconfigure.stopScanning()
                result.success(true)
            }
//            "connect" -> {
//                val id = call.argument<String>("mac")
//                if (id != null) {
//                    kb.connectToBeacon(id)
//                    result.success(true)
//                }
//            }
            "connectAndConfigureForUUID" -> {
                val id = call.argument<String>("mac")
                val uuid = call.argument<String>("uuid")
                if (id != null && uuid != null) {
                    mKBconfigure.connectAndConfigureForUUID(id, uuid, result)
                } else {
                    result.error("1", "Invalid Arguments", {})
                }
            }
            "disconnect" -> {
                mKBconfigure.disconnectBeacon()
                result.success(true)

            }
            "requestPermission" -> {
                if (mActivity == null) {
                    result.success(false)
                } else {
                    mKBconfigure.requestPermissions(mActivity!!)
                    result.success(true)
                }
            }
            "configure" -> {
                mKBconfigure.configureBeacon()
                result.success(true)
            }
            "toast" -> {
                val msg = call.argument<String>("msg")
                if (msg != null && mActivity != null) {
                    mKBconfigure.toastShow(mActivity!!.applicationContext, msg)
                    result.success(true)
                }
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        mMethodChannel.setMethodCallHandler(null)
    }
}
