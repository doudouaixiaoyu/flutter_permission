import 'dart:async';

import 'package:flutter/services.dart';

class FlutterPermission {
  static const MethodChannel _channel =
      const MethodChannel('flutter_permission');

  static Future<int> get getAppVersion async {
    final int version = await _channel.invokeMethod('getAppVersion');
    return version;
  }

  static Future<bool> checkPermission(String permission) async {
    final bool isCheck =
        await _channel.invokeMethod('checkPermission', permission);
    return isCheck;
  }

  static Future<bool> requestPermission(String permission) async {
    final bool isRequest =
        await _channel.invokeMethod('requestPermission', permission);
    return isRequest;
  }

  static Future<void> installApk(String apkPath) async {
    await _channel.invokeMethod('installApp', apkPath);
  }
}
