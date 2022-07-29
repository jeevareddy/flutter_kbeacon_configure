import 'package:flutter/material.dart';
import 'package:kbeacon_configure/beacon_model.dart';
import 'package:kbeacon_configure/kbeacon_configure.dart';

class ScanScreen extends StatefulWidget {
  final String? uuid;
  final Function(BuildContext)? onConfigurationSuccess;
  const ScanScreen({Key? key, this.onConfigurationSuccess, this.uuid})
      : super(key: key);

  @override
  State<ScanScreen> createState() => _ScanScreenState();
}

class _ScanScreenState extends State<ScanScreen> {
  List<BeaconModel> _discoveredBeacons = [];

  bool _isScanning = false;

  @override
  void initState() {
    // startScan();
    super.initState();
  }

  startScan() async {
    await KbeaconConfigure.initialize();
    final stream = await KbeaconConfigure.startScan();
    stream?.listen((data) {
      setState(() {
        _discoveredBeacons = (data as List?)
                ?.map((e) => BeaconModel.fromMap(Map<String, dynamic>.from(e)))
                .toList() ??
            [];
      });
    });
    setState(() {
      _isScanning = true;
    });
  }

  stopScan() async {
    await KbeaconConfigure.stopScan();
    if (mounted) {
      setState(() {
        _isScanning = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Scan for Device"),
        actions: [
          if (_isScanning)
            Container(
              margin: const EdgeInsets.all(8.0),
              child: const CircularProgressIndicator(
                color: Colors.white,
              ),
            ),
          ElevatedButton(
              onPressed: () {
                if (_isScanning) {
                  stopScan();
                } else {
                  startScan();
                }
              },
              child: Text(_isScanning ? "Stop" : "Scan"))
        ],
      ),
      body: Builder(builder: (context) {
        if (!_isScanning) {
          return Center(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: const [
                Icon(
                  Icons.bluetooth_disabled,
                  size: 56.0,
                  color: Colors.lightBlueAccent,
                ),
                Text("Start Scan to Discover Devices"),
              ],
            ),
          );
        }
        return ListView.builder(
          itemCount: _discoveredBeacons.length,
          itemBuilder: (context, index) {
            debugPrint(_discoveredBeacons[index].toString());
            return ListTile(
              title: Text(_discoveredBeacons[index].name ?? ""),
              subtitle: Text(_discoveredBeacons[index].mac.toString()),
              trailing: Builder(builder: (context) {
                if (_discoveredBeacons[index].state == "Connecting") {
                  return const CircularProgressIndicator();
                } else if (_discoveredBeacons[index].state == "Connected") {
                  return ElevatedButton(
                    child: const Text("Disconnect"),
                    onPressed: () {
                      KbeaconConfigure.disconnect();
                    },
                  );
                }
                return ElevatedButton(
                  child: const Text("Configure"),
                  onPressed: () {
                    KbeaconConfigure.connectAndConfigureForUUID(
                            _discoveredBeacons[index].mac, widget.uuid ?? "")
                        .then((value) {
                      if (value) {
                        stopScan();
                        widget.onConfigurationSuccess?.call(context);
                      }
                    });
                  },
                );
              }),
            );
          },
        );
      }),
    );
  }
}
