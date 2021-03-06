package com.umeng.umlinkdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class UMLinkActivity extends Activity {

    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umlink);
        String result;
        Intent intent = getIntent();

        //显示query中的path信息
        String path = intent.getStringExtra("path");
        result = "path:\n" + path + "\n";

        //显示query中的kv信息
        result += "\nparams:\n";
        Bundle params_bundle = intent.getBundleExtra("params");
        if (params_bundle !=null && !params_bundle.isEmpty()) {
            for (String key : params_bundle.keySet()) {
                if (!key.equals("path")) {
                    result += key + "=" + params_bundle.getString(key) + "\n";
                }
            }
        }
        else {
            result += "\n";
        }

        //显示安装参数中的kv信息
        result += "\ninstall_params:\n";
        Bundle install_params_bundle = intent.getBundleExtra("install_params");
        if (install_params_bundle != null && !install_params_bundle.isEmpty()) {
            for (String key : install_params_bundle.keySet()) {
                result += key + "=" + install_params_bundle.getString(key) + "\n";
            }
        }
        else {
            result += "\n";
        }

        mTextView = (TextView) findViewById(R.id.valueText);
        mTextView.setText(result);
    }
}