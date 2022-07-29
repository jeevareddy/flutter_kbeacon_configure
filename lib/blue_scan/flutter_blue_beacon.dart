library flutter_blue_beacon;

import 'beacon.dart';
// import 'package:flutter_blue/flutter_blue.dart';
import 'package:flutter_blue_plus/flutter_blue_plus.dart';

class FlutterBlueBeacon {
  // Singleton
  FlutterBlueBeacon._();

  static final FlutterBlueBeacon _instance = FlutterBlueBeacon._();

  static FlutterBlueBeacon get instance => _instance;

  Stream<Beacon?> scan({Duration? timeout}) => FlutterBluePlus.instance
      .scan(timeout: timeout, allowDuplicates: true)
      .map((scanResult) {
        return Beacon.fromScanResult(scanResult);
      })
      .expand((b) => b)
      .where((b) => b != null);
}
