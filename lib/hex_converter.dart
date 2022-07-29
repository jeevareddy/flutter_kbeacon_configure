import 'package:flutter/material.dart';

class HexConverter {
  static String toHexString(String str) {
    final hex = str.codeUnits
        .map((int e) => e.toRadixString(16).padLeft(2, '0'))
        .join();
    debugPrint("Hex Value: $hex");
    debugPrint("String Value: ${fromHexString(hex)}");
    return "0x$hex";
  }

  static String fromHexString(String hex) {
    String result = "";
    for (var i = 0; i < hex.length; i += 2) {
      result = result +
          String.fromCharCode(int.parse(hex.substring(i, i + 2), radix: 16));
    }
    return result;
  }

  static String parseUUID(String uuid) {
    String result = "";
    final hex = uuid.split("-").fold<String>("", (p, e) => p + e);
    for (var i = 0; i < hex.length; i += 2) {
      result = result +
          String.fromCharCode(int.parse(hex.substring(i, i + 2), radix: 16));
    }
    return result;
  }
}
