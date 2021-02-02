package com.umeng.umlinkdemo;

import android.app.Application;

//import com.umeng.commonsdk.UMConfigure;

import com.umeng.commonsdk.UMConfigure;

import org.xutils.x;

/**
 * Created by jessie.wangqq on 2019-10-21.
 */
public class UMLinkApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 请确保在AndroidManifest.xml中对scheme进行设置，如：
         * <intent-filter>
         *        <action android:name="android.intent.action.VIEW" />
         *        <category android:name="android.intent.category.DEFAULT" />
         *        <category android:name="android.intent.category.BROWSABLE" />
         *        <data android:scheme="mobclick" />
         * </intent-filter>
         **/

        /**
         * 在应用Application处进行友盟SDK初始化
         * 请将appkey参数替换为您的appkey，确保填入正确的appkey
         */
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, "5f3a3aa3b4b08b653e95e6f9", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.setProcessEvent(true);//支持多进程打点.默认不支持

        /**
         * getInstallParams请在UMConfigure.init之后调用
         * 由于UMConfigure.init有异步联网操作,建议init后延迟几秒后再调用getInstallParams
         * 或者，如示例在真正需要参数的某个时间点或者用户操作节点（如button点击）调用
         */
    }
}
