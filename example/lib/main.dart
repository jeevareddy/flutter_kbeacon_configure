import 'dart:async';

import 'package:flutter/material.dart';
import 'package:kbeacon_configure/blue_scan/beacon.dart';
import 'package:kbeacon_configure/blue_scan/flutter_blue_beacon.dart';
import 'package:kbeacon_configure/hex_converter.dart';
import 'package:kbeacon_configure/screens/scan_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  FlutterBlueBeacon flutterBlueBeacon = FlutterBlueBeacon.instance;

  /// Scanning
  StreamSubscription? _scanSubscription;
  Map<int, Beacon> beacons = {};

  bool _configured = true;

  EddystoneUID? _beacon;

  String? _errorMessage;

  startMonitoring() async {
    try {
      setState(() {
        _scanSubscription = flutterBlueBeacon.scan().listen(
            (beacon) {
              debugPrint("Listening beacons ${DateTime.now()}");
              if (beacon is EddystoneUID) {
                if (HexConverter.fromHexString(beacon.namespaceId) ==
                    "com.aiders") {
                  setState(() {
                    _beacon = beacon;
                  });
                  debugPrint('Beacon Name: ${beacon.name}');
                  debugPrint(
                      'Beacon ID: ${HexConverter.fromHexString(beacon.beaconId)}');
                } else {
                  setState(() {
                    _beacon = null;
                  });
                }
              } else {
                setState(() {
                  _beacon = null;
                });
              }
              debugPrint('localName: ${beacon?.name}');
              if (beacon != null) {
                setState(() {
                  beacons[beacon.hash] = beacon;
                });
              }
            },
            onDone: stopMonitoring,
            onError: (e) {
              debugPrint(e.toString());
              setState(() {
                _errorMessage = e.toString();
              });
            });
      });
    } catch (e) {
      debugPrint(e.toString());
      setState(() {
        _errorMessage = e.toString();
      });
    }
  }

  stopMonitoring() {
    debugPrint("Stop scanning");
    _scanSubscription?.cancel();
    setState(() {
      _scanSubscription = null;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: _configured
          ? body()
          : ScanScreen(
              uuid: "jeevan",
              onConfigurationSuccess: (BuildContext ctx) {
                setState(() {
                  _configured = true;
                });
              },
            ),
    );
  }

  Widget body() {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Plugin example app'),
        actions: [
          ElevatedButton(
              onPressed: () {
                setState(() {
                  _configured = false;
                });
              },
              child: const Text("Configure a Device"))
        ],
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            if (_beacon != null)
              Column(
                children: [
                  Text(
                    "Detected Beacon in proximity",
                    style: Theme.of(context).textTheme.headlineSmall,
                  ),
                  Card(
                    child: ListTile(
                      title: Text(_beacon!.name),
                      subtitle:
                          Text(HexConverter.fromHexString(_beacon!.beaconId)),
                      trailing: Text("${_beacon!.distance} m"),
                    ),
                  ),
                ],
              ),
            if (_scanSubscription == null)
              ElevatedButton(
                child: const Text('Start Monitoring'),
                onPressed: startMonitoring,
              )
            else
              ElevatedButton(
                child: const Text('Stop Monitoring'),
                onPressed: stopMonitoring,
              ),
            if (_errorMessage != null) Text(_errorMessage!),
          ],
        ),
      ),
    );
  }
}
