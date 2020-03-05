import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_permission/flutter_permission.dart';
import 'package:flutter_permission/permission.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  int _platformVersion = 0;
  bool _isPermission = false;
  bool _isRequest = false;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    int platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await FlutterPermission.getAppVersion;
    } on PlatformException {
      platformVersion = 0;
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  Future<void> boolPermission() async {
    bool isPermission = await FlutterPermission.checkPermission(
        AndroidPermission.WRITE_EXTERNAL_STORAGE);
    setState(() {
      _isPermission = isPermission;
    });
  }

  Future<void> requestPermission() async {
    bool isRequest = await FlutterPermission.requestPermission(
        AndroidPermission.WRITE_EXTERNAL_STORAGE);
    setState(() {
      _isRequest = isRequest;
    });
  }

  Future<void> installApp() async {
    await FlutterPermission.installApk('/sdcard/Download/app-debug.apk');
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: <Widget>[
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                FlatButton(
                  onPressed: () {},
                  child: Text('当前App版本: $_platformVersion'),
                ),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: <Widget>[
                FlatButton(
                  color: Theme.of(context).primaryColor,
                  onPressed: boolPermission,
                  child: Text('获取是否有写入的权限'),
                ),
                Text(_isPermission ? '有' : '无'),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: <Widget>[
                FlatButton(
                  color: Theme.of(context).primaryColor,
                  onPressed: requestPermission,
                  child: Text('申请读写的权限'),
                ),
                Text(_isRequest ? '请求成功' : '请求失败'),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: <Widget>[
                FlatButton(
                  color: Theme.of(context).primaryColor,
                  onPressed: installApp,
                  child: Text('安装App'),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
