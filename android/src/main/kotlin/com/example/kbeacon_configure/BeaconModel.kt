package com.example.kbeacon_configure

import com.kkmcn.kbeaconlib2.KBConnState
import com.kkmcn.kbeaconlib2.KBeacon

class BeaconModel(
) {

    var mac: String? = null
    var rssi: Int = 0
    var name: String? = null
    var state: KBConnState? = null


    companion object {
        fun parse(it: KBeacon): BeaconModel {
            val beacon = BeaconModel()
            beacon.mac = it.mac
            beacon.rssi = it.rssi
            beacon.name = it.name
            beacon.state = it.state
            return beacon
        }
    }


    fun toMap(): Map<String, Any> {
        return mapOf(
            Pair("mac", mac as Any),
            Pair("rssi", rssi as Any),
            Pair("name", name as Any),
            Pair("state", state?.name as Any),
        )
    }
}