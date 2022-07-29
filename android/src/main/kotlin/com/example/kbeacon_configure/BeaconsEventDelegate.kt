package com.example.kbeacon_configure

import android.util.Log
import com.kkmcn.kbeaconlib2.KBeacon
import com.kkmcn.kbeaconlib2.KBeaconsMgr
import io.flutter.plugin.common.EventChannel

class BeaconsEventDelegate : EventChannel.StreamHandler, KBeaconsMgr.KBeaconMgrDelegate {

    companion object {
        var mBeaconsStockList: Array<out KBeacon>? = arrayOf()
        var mBeacons: List<BeaconModel>? = listOf()
    }

    var eventSink: EventChannel.EventSink? = null


    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events
        Log.d(KbeaconConfigure.TAG, "Listened: ${events.toString()}")
    }

    override fun onCancel(arguments: Any?) {
        eventSink = null
    }

    override fun onBeaconDiscovered(bcons: Array<out KBeacon>?) {
        if (bcons != null) {
            mBeaconsStockList = bcons
            mBeacons = bcons.map { BeaconModel.parse(it) }
        eventSink?.success(mBeacons!!.map {  it.toMap() })
        }else {
            eventSink?.success(null)
        }

        Log.d(KbeaconConfigure.TAG, "Discovered beacons: $mBeacons")

    }

    override fun onCentralBleStateChang(nNewState: Int) {
        Log.d(KbeaconConfigure.TAG, "Central BLE State Changed: $nNewState")
    }

    override fun onScanFailed(errorCode: Int) {
        Log.d(KbeaconConfigure.TAG, "Scan Failed: $errorCode")
    }

}