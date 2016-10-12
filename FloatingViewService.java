package credencys.kiosk.Activity.Service;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.credencys.kiosk.R;

import credencys.kiosk.Activity.MainActivity;
import credencys.kiosk.Activity.Util.Constant;

public class FloatingViewService extends Service {

    private WindowManager windowManager;
    private boolean mIsFloatingViewAttached = false;
    private ImageButton imageButton;
    public boolean FAB_ANABLED;
    private int btnViewID;
    private MainActivity mainActivity;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressWarnings("ResourceAsColor")
    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        imageButton = new ImageButton(this);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.CENTER|Gravity.RIGHT;

        /*
        * Toggle the FAB between Kuber and the Navigator
        * */
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo foregroundTaskInfo = manager.getRunningTasks(1).get(0);
        final String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();

        int id = 0;
        if (foregroundTaskPackageName.contains(Constant.LAUNCH_NAVIGATOR)){
            id = R.id.btnMap;
            imageButton.setImageResource(R.mipmap.ic_back_to_kuber);
            imageButton.setBackgroundColor(R.drawable.back_to_kuber);

            windowManager.addView(imageButton, params);

            imageButton.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                        {
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(Constant.LAUNCH_KUBER);
                                startActivity(LaunchIntent);

                                ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                                ActivityManager.RunningTaskInfo foregroundTaskInfo = manager.getRunningTasks(1).get(0);
                                final String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();

                                if (!foregroundTaskPackageName.contains("com.mapfactor.navigator")){
                                    disableFAB();
                                }
                                toggleFAB();

                            } else {
                                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(Constant.LAUNCH_KUBER);
                                startActivity(LaunchIntent);
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
        else if (foregroundTaskPackageName.contains(Constant.LAUNCH_KUBER)){
            id = R.id.btnApp;
            imageButton.setImageResource(R.mipmap.ic_back_to_navigator);
            imageButton.setBackgroundColor(R.drawable.back_to_kuber);

            windowManager.addView(imageButton, params);

            imageButton.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                        {
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(Constant.LAUNCH_NAVIGATOR);
                                startActivity(LaunchIntent);

                                ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                                ActivityManager.RunningTaskInfo foregroundTaskInfo = manager.getRunningTasks(1).get(0);
                                final String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();

                                if (!foregroundTaskPackageName.contains(Constant.LAUNCH_NAVIGATOR) &&
                                        foregroundTaskInfo.equals(MainActivity.class)){
                                    disableFAB();
                                }
                                toggleFAB();

                            } else {
                                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(Constant.LAUNCH_KUBER);
                                startActivity(LaunchIntent);
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

        mIsFloatingViewAttached = true;

    }

    public void removeView(){
        if (imageButton != null){
            windowManager.removeView(imageButton);
            mIsFloatingViewAttached = false;
        }
    }


    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT);
        super.onDestroy();
        removeView();
    }

    public void disableFAB(){
        imageButton.setVisibility(View.GONE);
        FAB_ANABLED = false;
    }

    public void anableFAB(){

        if (imageButton.isEnabled()){
            imageButton.setVisibility(View.VISIBLE);
        }

        FAB_ANABLED = true;
    }

    public void toggleFAB(){

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo foregroundTaskInfo = manager.getRunningTasks(1).get(0);
        final String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
        if(foregroundTaskPackageName.contains(Constant.LAUNCH_KUBER) || foregroundTaskPackageName.contains(Constant.LAUNCH_NAVIGATOR)){

            if (foregroundTaskPackageName.contains(Constant.LAUNCH_KUBER)){
                imageButton.setImageResource(R.mipmap.ic_back_to_navigator);
                imageButton.setVisibility(View.VISIBLE);

                imageButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(Constant.LAUNCH_NAVIGATOR);
                                startActivity(LaunchIntent);

                                if (foregroundTaskPackageName.contains(Constant.LAUNCH_NAVIGATOR)){
                                    disableFAB();
                                    imageButton.setVisibility(View.GONE);
                                }
                                toggleFAB();
                        }
                        return false;
                    }
                });


            }else if (foregroundTaskPackageName.contains(Constant.LAUNCH_NAVIGATOR)){
                imageButton.setImageResource(R.mipmap.ic_back_to_kuber);
                imageButton.setVisibility(View.VISIBLE);

                imageButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(Constant.LAUNCH_KUBER);
                                startActivity(LaunchIntent);

                                if (foregroundTaskPackageName.contains(Constant.LAUNCH_KUBER)){
                                    disableFAB();
                                    imageButton.setVisibility(View.GONE);
                                }
                                toggleFAB();
                        }
                        return false;
                    }
                });
            }
        }
    }
}
