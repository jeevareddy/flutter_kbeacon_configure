import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:kbeacon_configure/kbeacon_configure.dart';

void main() {
  const MethodChannel channel = MethodChannel('kbeacon_configure');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await KbeaconConfigure.platformVersion, '42');
  });
}
