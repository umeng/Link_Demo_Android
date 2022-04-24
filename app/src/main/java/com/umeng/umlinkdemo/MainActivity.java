package com.umeng.umlinkdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.umlink.MobclickLink;
import com.umeng.umlink.UMLinkListener;


import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private String mFrom = "";
    private HashMap<String,String> mInstall_params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        SharedPreferences sp = mContext.getSharedPreferences("MY_PREFERENCE", Context.MODE_PRIVATE);
        boolean hasAuth = sp.getBoolean("key_Has_Auth", false);
        if (hasAuth == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("请同意隐私授权");
            builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    System.exit(0);//正常退出
                }
            });
            builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //记录授权标记
                    SharedPreferences.Editor sp = mContext.getSharedPreferences("MY_PREFERENCE", Context.MODE_PRIVATE).edit();
                    sp.putBoolean("key_Has_Auth", true);
                    sp.commit();

                    startUmeng();
                }
            });
            builder.show();
        }
        else {
            startUmeng();
        }
    }

    public void startUmeng() {
        //真正初始化友盟SDK
        UMConfigure.init(this, "5f3a3aa3b4b08b653e95e6f9", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);

        UMConfigure.setProcessEvent(true);//支持多进程打点.默认不支持
        
        /**
         * getInstallParams请在UMConfigure.init之后调用
         * 由于UMConfigure.init有异步联网操作,建议init后延迟几秒后再调用getInstallParams
         * 或者，如示例在真正需要参数的某个时间点或者用户操作节点（如button点击）调用
         */

        Uri data = getIntent().getData();
        if (data != null) {
            mFrom = "onCreate-handleUMLinkURI";
            Log.i("UMLINKDEMOINFO", "onCreate: "+data.toString());
            MobclickLink.handleUMLinkURI(this, data, umlinkAdapter);
        }

        SharedPreferences sp = mContext.getSharedPreferences("MY_PREFERENCE", Context.MODE_PRIVATE);
        boolean hasGetInstallParams = sp.getBoolean("key_Has_Get_InstallParams", false);
        if (hasGetInstallParams == false) {
            mFrom = "onCreate-getInstallParams";
            //从来没调用过getInstallParam方法，适当延时调用getInstallParam方法
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MobclickLink.getInstallParams(mContext, umlinkAdapter);
                }
            }, 2000);//2秒后执行

            //MobclickLink.getInstallParams(mContext, umlinkAdapter);
            //不再检查SDK是否初始化成功，可以不用延迟几秒
            //但延迟调用有助于提升模糊匹配成功率

        }
        else {
            //已经调用过getInstallParam方法，没必要在下次启动时再调用
            //但后续仍可在需要时调用，比如demo中的按钮点击
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 此处要调用，否则App在后台运行时，会无法截获
        Uri data = intent.getData();
        if (data != null) {
            Log.i("UMLINKDEMOINFO", "onNewIntent: "+data.toString());
            mFrom = "onNewIntent-handleUMLinkURI";
            MobclickLink.handleUMLinkURI(this, data, umlinkAdapter);
        }
    }

    UMLinkListener umlinkAdapter = new UMLinkListener() {
        @Override
        public void onLink(String path, HashMap<String, String> query_params) {
            android.util.Log.i("mob", "-----onLink-----");
            Intent in = new Intent(mContext, UMLinkActivity.class);
            if (!path.isEmpty()) {
                in.putExtra("path", path);
            }
            if (!query_params.isEmpty()) {
                Bundle query_params_bundle = new Bundle();
                for (String key : query_params.keySet()) {
                    query_params_bundle.putString(key, query_params.get(key));
                }
                in.putExtra("params", query_params_bundle);
            }
            if (mInstall_params != null && !mInstall_params.isEmpty()) {
                Bundle install_params_bundle = new Bundle();
                for (String key : mInstall_params.keySet()) {
                    install_params_bundle.putString(key, mInstall_params.get(key));
                }
                in.putExtra("install_params", install_params_bundle);
            }
            in.putExtra("from", mFrom);
            startActivity(in);
        }

        @Override
        public void onInstall(HashMap<String, String> install_params, Uri uri) {
            if (install_params.isEmpty() && uri.toString().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("没有匹配到安装参数");
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.show();
            }
            else {
                if (!install_params.isEmpty()) {
                    mInstall_params = install_params;
                }
                if (!uri.toString().isEmpty()) {
                    MobclickLink.handleUMLinkURI(mContext, uri, umlinkAdapter);
                }
            }
            SharedPreferences.Editor sp = mContext.getSharedPreferences("MY_PREFERENCE", Context.MODE_PRIVATE).edit();
            sp.putBoolean("key_Has_Get_InstallParams", true);
            sp.commit();
        }

        @Override
        public void onError(String error) {
            android.util.Log.i("mob", error);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(error);
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            builder.show();
        }
    };

    public void onClick(View v) {
        int id = v.getId();
        Intent in = null;
        if (id == R.id.installparams) {
            MobclickLink.getInstallParams(this, umlinkAdapter);
        }
        else if (id == R.id.other) {
            in = new Intent(mContext, UMOtherActivity.class);
            startActivity(in);
        }
    }
}
