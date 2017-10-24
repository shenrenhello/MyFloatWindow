package com.example.gsl.myfloatwindow;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TopWindowService extends Service {

    public static final String OPERATION = "operation";
    public static final int OPERATION_SHOW = 100;
    public static final int OPERATION_HIDE = 101;

    private static final int HANDLE_CHECK_ACTIVTY = 200;

    private  boolean isAdded = false;//是否已添加悬浮窗
    private static WindowManager wm;
    private static WindowManager.LayoutParams params;
    private Button btn_floatView;

    private List<String> homeList;//桌面应用程序包名列表
    private ActivityManager mActivityManager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        homeList = getHomes();
        createFloatView();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        int operation = intent.getIntExtra(OPERATION,OPERATION_SHOW);
        switch (operation){
            case OPERATION_SHOW:
                mHandler.removeMessages(HANDLE_CHECK_ACTIVTY);
                mHandler.sendEmptyMessage(HANDLE_CHECK_ACTIVTY);
                break;
            case OPERATION_HIDE:
                mHandler.removeMessages(HANDLE_CHECK_ACTIVTY);
                break;
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLE_CHECK_ACTIVTY:
                    if (isHome()){
                        if (!isAdded){
                            wm.addView(btn_floatView,params);
                            isAdded = true;
                        }
                    }else {
                        if (isAdded){
                            wm.removeView(btn_floatView);
                            isAdded = false;
                        }
                    }
                    mHandler.sendEmptyMessageDelayed(HANDLE_CHECK_ACTIVTY,1000);
                    break;
            }
        }
    };

    /**
     * 获得属于桌面的应用的应用包名
     * @return 返回包含所有包名的字符串列表
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager pm = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo info : resolveInfos){
            names.add(info.activityInfo.packageName);
        }
        return  names;
    }

    public  boolean isHome(){
        if (mActivityManager == null){
            mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        }
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);//表示只返回一个即当前运行的那个task。
        //return true;
        return homeList.contains(rti.get(0).topActivity.getPackageName());//其最顶层的Activity即显示给用户的Activity。
    }

    private void createFloatView(){
        btn_floatView = new Button(getApplicationContext());
        btn_floatView.setBackgroundColor(Color.RED);
        btn_floatView.getBackground().setAlpha(100);
        btn_floatView.setText("我是悬浮窗~，拖我啊^_^");

        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();

        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        /**
         * if set TYPE_PHONE  下拉通知栏后不可见
         */
        params.format = PixelFormat.RGBA_8888;//北京透明

        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        /**
         * 下面的flags属性形同锁定。悬浮窗不可触摸，不接受任何事件，不影响后面的事件响应
         * params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
         * WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
         */
        params.width = wm.getDefaultDisplay().getWidth()/2;
        params.height = wm.getDefaultDisplay().getHeight()/4;

        btn_floatView.setOnTouchListener(new View.OnTouchListener() {

            int lastX,lastY;
            int paramX,paramY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = params.x;
                        paramY = params.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) (event.getRawX()) - lastX;
                        int dy = (int) (event.getRawY()) - lastY;
                        params.x = paramX + dx;
                        params.y = paramY + dy;
                        wm.updateViewLayout(btn_floatView,params);
                        break;
                }
                return true;
            }
        });
//        wm.addView(btn_floatView,params);
//        isAdded = true;
    }
}
