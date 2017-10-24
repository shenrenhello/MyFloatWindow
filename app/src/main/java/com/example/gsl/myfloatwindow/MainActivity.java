package com.example.gsl.myfloatwindow;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    private Button btn_show;
    private Button btn_hide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_show = (Button) findViewById(R.id.showBtn);
        btn_hide = (Button) findViewById(R.id.hideBtn);
        btn_show.setOnClickListener(this);
        btn_hide.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //>=23 版本需要打开权限
        if(Build.VERSION.SDK_INT >= 23){
            if(!Settings.canDrawOverlays(this)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            }
        }
        switch (v.getId()){
            case R.id.showBtn:
                Intent showIntent = new Intent(this,TopWindowService.class);
                showIntent.putExtra(TopWindowService.OPERATION,TopWindowService.OPERATION_SHOW);
                startService(showIntent);
                break;
            case R.id.hideBtn:
                Intent hideIntent = new Intent(this,TopWindowService.class);
                hideIntent.putExtra(TopWindowService.OPERATION,TopWindowService.OPERATION_HIDE);
                startService(hideIntent);
                break;
        }
    }

}
