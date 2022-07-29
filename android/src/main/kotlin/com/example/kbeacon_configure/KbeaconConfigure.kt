package com.example.kbeacon_configure

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kkmcn.kbeaconlib2.KBConnState
import com.kkmcn.kbeaconlib2.KBeacon
import com.kkmcn.kbeaconlib2.KBeaconsMgr
import com.kkmcn.kbeaconlib2.KBConnectionEvent
import com.kkmcn.kbeaconlib2.KBeacon.ConnStateDelegate
import com.kkmcn.kbeaconlib2.KBException

import com.kkmcn.kbeaconlib2.KBCfgPackage.KBAdvTxPower

import com.kkmcn.kbeaconlib2.KBCfgPackage.KBAdvMode
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgAdvEddyUID

import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgAdvIBeacon
import com.kkmcn.kbeaconlib2.KBeacon.ActionCallback
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBTriggerAction

import com.kkmcn.kbeaconlib2.KBCfgPackage.KBTriggerType

import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgTrigger
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgBase
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgCommon
import io.flutter.plugin.common.MethodChannel


class KbeaconConfigure {

    companion object {
        const val TAG = "KbeaconConfigure"
        const val DEFAULT_PASSWORD = "0000000000000000"
        const val CONNECTION_TIMEOUT = 15000
    }

    var mBeaconMgr: KBeaconsMgr? = null
    var mUuid: String = ""
    var mResultMethodChannel: MethodChannel.Result? = null
    var mBeaconsDelegate = BeaconsEventDelegate()
    var mConnectedBeacon: KBeacon? = null


    fun initialize(ctx: Context) {
        mBeaconMgr = KBeaconsMgr.sharedBeaconManager(ctx)
        if (mBeaconMgr == null) {
            toastShow(ctx, "Make sure the phone supports BLE function");
        }
    }


    fun startScanning(ctx: Context) {

        mBeaconMgr!!.delegate = mBeaconsDelegate

        when (mBeaconMgr!!.startScanning()) {
            0 -> {
                Log.v(TAG, "start scan success")
            }
            KBeaconsMgr.SCAN_ERROR_BLE_NOT_ENABLE -> {
                toastShow(ctx, "BLE function is not enable")
            }
            KBeaconsMgr.SCAN_ERROR_NO_PERMISSION -> {
                toastShow(ctx, "BLE scanning has no location permission")
            }
            else -> {
                toastShow(ctx, "BLE scanning unknown error")
            }
        }
    }

    fun stopScanning() {
        mBeaconMgr!!.stopScanning()
        mBeaconMgr!!.clearBeacons()
    }

    fun connectAndConfigureForUUID(id: String, uuid:String, result: MethodChannel.Result) {
        if (BeaconsEventDelegate.mBeacons != null) {
            val beacon = BeaconsEventDelegate.mBeaconsStockList!!.findLast { it.mac == id }
            mUuid = uuid
            mResultMethodChannel = result
            beacon?.connect(DEFAULT_PASSWORD, CONNECTION_TIMEOUT, connectionDelegate)
        }
    }

    private val connectionDelegate: ConnStateDelegate =
        ConnStateDelegate { beacon, state, nReason ->
            when (state) {
                KBConnState.Connected -> {
                    mConnectedBeacon = beacon
                    configureBeacon()
                    Log.v(TAG, "device has connected")
                }
                KBConnState.Connecting -> {
                    Log.v(TAG, "device start connecting")

                }
                KBConnState.Disconnecting -> {
                    Log.v(TAG, "device start disconnecting")

                }
                KBConnState.Disconnected -> {
                    mConnectedBeacon = null
                    when (nReason) {
                        KBConnectionEvent.ConnAuthFail -> {
                            Log.d(TAG, "password error")
                        }
                        KBConnectionEvent.ConnTimeout -> {
                            Log.d(TAG, "connection timeout")
                        }
                        else -> {
                            Log.d(TAG, "connection other error, reason:$nReason")
                        }
                    }

                    Log.e(TAG, "device has disconnected:$nReason")
                }
            }
        }

    fun disconnectBeacon() {
        mResultMethodChannel = null
        mUuid = ""
        mConnectedBeacon?.disconnect()
    }


    fun requestPermissions(ctx: Activity) {
        //for android6, the app need corse location permission for BLE scanning
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                ctx,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0
            )
        }
        //for android10, the app need fine location permission for BLE scanning
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                ctx,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
            );
        }
    }

    fun configureBeacon() {
        if (mConnectedBeacon?.isConnected != true) {
            return
        }

        //Change Common Configs
        val commonCfg = KBCfgCommon()
        commonCfg.name = "Aiders"


        //set trigger type
        val btnTriggerPara = KBCfgTrigger(0, KBTriggerType.BtnSingleClick)
        btnTriggerPara.setTriggerAdvChangeMode(0)
        btnTriggerPara.triggerAction = KBTriggerAction.Advertisement
        btnTriggerPara.triggerAdvSlot = 0
        btnTriggerPara.triggerAdvTime = 2


        //UID para
        val uidCfg = KBCfgAdvEddyUID()
        uidCfg.slotIndex = 0
        uidCfg.advMode = KBAdvMode.Legacy
        uidCfg.isAdvConnectable = true
        uidCfg.isAdvTriggerOnly = true
        uidCfg.advPeriod = 1280.0f
        uidCfg.txPower = KBAdvTxPower.RADIO_Neg4dBm
        uidCfg.nid = "0x636f6d2e616964657273"//"com.aiders"   //"0x00010203040506070809"
        uidCfg.sid = mUuid //"0x010203040506"

        val cfgList: ArrayList<KBCfgBase> = ArrayList()
        cfgList.add(commonCfg)
        cfgList.add(btnTriggerPara)
        cfgList.add(uidCfg)

        //send parameters to device
        mConnectedBeacon!!.modifyConfig(cfgList) { bConfigSuccess, error ->
            if (bConfigSuccess) {
                mResultMethodChannel?.success(true)
                disconnectBeacon()
                Log.e(TAG, "config data to beacon success")
            } else {
                mResultMethodChannel?.error("2", "Configuration failed.", {})
                Log.e(TAG, "config failed for error:" + error.message)
            }
            mResultMethodChannel = null
        }
    }

     fun toastShow(ctx: Context, msg: String) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
    }
}