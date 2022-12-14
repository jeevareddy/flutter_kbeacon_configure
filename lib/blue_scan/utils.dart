import 'dart:typed_data';

int byteToInt8(int b) =>
    Uint8List.fromList([b]).buffer.asByteData().getInt8(0);

int twoByteToInt16(int v1, int v2) =>
    Uint8List.fromList([v1, v2]).buffer.asByteData().getUint16(0);

String byteListToHexString(List<int> bytes) => bytes
    .map((i) => i.toRadixString(16).padLeft(2, '0'))
    .reduce((a, b) => (a + b));
