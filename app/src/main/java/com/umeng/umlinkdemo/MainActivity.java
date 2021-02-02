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
import android.view.View;

//import com.umeng.analytics.MobclickAgent;
//import com.umeng.analytics.UMLinkListener;
import com.umeng.umlink.MobclickLink;
import com.umeng.umlink.UMLinkListener;


import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Context mContext;

    private HashMap<String,String> mInstall_params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        Uri data = getIntent().getData();
        if (data != null) {
            MobclickLink.handleUMLinkURI(this, data, umlinkAdapter);
        }

        SharedPreferences sp = mContext.getSharedPreferences("MY_PREFERENCE", Context.MODE_PRIVATE);
        boolean hasGetInstallParams = sp.getBoolean("key_Has_Get_InstallParams", false);
        if (hasGetInstallParams == false) {
            //从来没调用过getInstallParam方法，适当延时调用getInstallParam方法
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MobclickLink.getInstallParams(mContext, umlinkAdapter);
                }
            }, 2000);//2秒后执行
            //MobclickLink.getInstallParams(mContext, umlinkAdapter);

            //在9.3.3版本中，由于要检查SDK是否初始化成功，所以可能需要3秒乃至更长的延迟才能调用getInstallParams
            //在9.3.6以后版本中，不再检查SDK是否初始化成功，可以不用延迟

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
        Uri uri = intent.getData();
        // 此处要调用，否则App在后台运行时，会无法截获
        Uri data = intent.getData();
        if (data != null) {
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
    }
}
