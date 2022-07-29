import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:kbeacon_configure/hex_converter.dart';

class KbeaconConfigure {
  static const MethodChannel _channel = MethodChannel('kbeacon_configure');
  static const EventChannel _beacons = EventChannel('beacons');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> initialize() async {
    final bool result = await _channel.invokeMethod('initialize') ?? false;
    debugPrint("Initialized: $result");
  }

  static Future<void> requestPermission() async {
    final bool result =
        await _channel.invokeMethod('requestPermission') ?? false;
    debugPrint("Permission Requested: $result");
  }

  static Future<Stream<dynamic>?> startScan() async {
    final bool result = await _channel.invokeMethod('startScan') ?? false;
    if (result) {
      return _beacons.receiveBroadcastStream().map((event) => event);
    }
    debugPrint("Scan Started: $result");
    return null;
  }

  static Future<void> stopScan() async {
    final bool result = await _channel.invokeMethod('stopScan') ?? false;
    debugPrint("Scan Stoped: $result");
  }

  // static Future<void> connect(String? mac) async {
  //   final bool result =
  //       await _channel.invokeMethod('connect', {"mac": mac}) ?? false;
  //   debugPrint("Beacon Connected: $result");
  // }

  static Future<bool> connectAndConfigureForUUID(
      String? mac, String uuid) async {
    try {
      final hexByteNamespace = HexConverter.toHexString(uuid);
      final bool result = await _channel.invokeMethod(
              'connectAndConfigureForUUID',
              {"mac": mac, "uuid": hexByteNamespace}) ??
          false;
      debugPrint("Beacon Connected: $result");
      return result;
    } catch (e) {
      debugPrint("Configuration Error: $e");
      _channel.invokeMethod(
          'toast', {"msg": "Unable to configure device, try again."});
      return false;
    }
  }

  static Future<void> disconnect() async {
    final bool result = await _channel.invokeMethod('disconnect') ?? false;
    debugPrint("Beacon Disconnected: $result");
  }

  // static Future<void> configure() async {
  //   final bool result = await _channel.invokeMethod('configure') ?? false;
  //   debugPrint("Beacon Configure: $result");
  // }
}
