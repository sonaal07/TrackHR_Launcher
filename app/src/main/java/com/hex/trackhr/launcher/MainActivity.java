package com.hex.trackhr.launcher;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    int logotimer;
    Animation rotate, zoomin,rotate_half;
    ImageView hex, hex_inner, hex_outer;
    Timer timer;
    TimerTask timerTask;
    ImageView logo;
    TextView tvhexbis,tvheading;
    static String Settings_Password="727595";
    Boolean pause = false;
    Dialog d;
    int d_time = 20500;
    Context c;
    boolean initStart = true;
    boolean cacheCleared = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_v2);
        Log.e("SerialNo",getSerialNumber(getApplicationContext()));

        uninstallApps();
        disableApps();
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/deja_vu_sans_condensed.ttf");
        ((TextView)findViewById(R.id.track)).setTypeface(typeface);

        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "fonts/deja_vu_sans_condensed_bold.ttf");
        ((TextView)findViewById(R.id.hr)).setTypeface(typeface2);

        c = this;


        if (ShellInterface.isSuAvailable()) {
            try {
                ShellInterface.runCommand("service call activity 42 s16 com.android.systemui");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    Thread logotimer1;
    int skip = 0;

    public void enableDubugging()
    {
            try {
                if (ShellInterface.isSuAvailable()) {
                    //ShellInterface.runCommand("service call activity 42 s16 com.android.systemui");
                    ShellInterface.runCommand("setprop service.adb.tcp.port 5555");
                    ShellInterface.runCommand("stop adbd");
                    ShellInterface.runCommand("start adbd");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "ADB ON", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    public void disableDubugging()
    {
        if (ShellInterface.isSuAvailable()) {
            try {
                //ShellInterface.runCommand("service call activity 42 s16 com.android.systemui");
//                ShellInterface.runCommand("setprop service.adb.tcp.port 5555");
//                ShellInterface.runCommand("stop adbd");
//                ShellInterface.runCommand("start adbd");
                ShellInterface.runCommand("stop adbd");
                Toast.makeText(getApplicationContext(),"ADB Stopped",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void disableApp(String packageName)
    {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "pm disable "+packageName});
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void disableApps()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] packages = new String[]{
                        "com.oranth.tvlauncher",
                        "com.android.vending"};
                for (int i=0;i<packages.length;i++) {
                    disableApp(packages[i]);
                }
            }
        }).start();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_1:
                enableDubugging();
                return true;
        }
        return false;
    }


    public void uninstallApps()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] packages = new String[]{"ru.andr7e.deviceinfohw.pro",
                        "org.xbmc.kodi",
                        "com.valor.mfc.droid.tvapp.generic",
                        "com.netflix.mediaclient",

                        "com.csdroid.pkg",
                        "com.mm.droid.livetv.tve",
                        "com.google.android.youtube.tv",
                        //"com.oranth.tvlauncher",
                        "com.ionitech.airscreen",
                        "cm.aptoidetv.pt"};
                for (int i=0;i<packages.length;i++) {
                    uninstallApp(packages[i]);
                }
            }
        }).start();
    }

    public static String getSerialNumber(Context context) {
        return Build.SERIAL;
    }

    public void uninstallApp(String packageName)
    {
        try {
            //adb uninstall --user 0 <package_name>
            Process process = Runtime.getRuntime().exec(new String[]{"su", "adb", " uninstall --user 0 "+packageName});
            //Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "pm uninstall "+packageName});
            process.waitFor();
        }catch (Exception e)
        {

        }
    }

    public void clearCache()
    {
        if(cacheCleared)
        {
            return;
        }
        String packageName = "com.hexbis.trackhr.attendance";
        try {
            cacheCleared = true;
            Process process = Runtime.getRuntime().exec(new String[]{"adb shell su -c \"rm -rf /data/data/"+packageName,"/cache/*\""});
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    int lastKeyCode = -1;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d("keyEvent", String.valueOf(event.getKeyCode()));

        if(event.getKeyCode()==8&&lastKeyCode==8) {
            Intent intent;
            PackageManager pm = getPackageManager();
            intent = pm.getLaunchIntentForPackage("eu.chainfire.supersu");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            lastKeyCode=-1;
        }
        else
        {
            lastKeyCode=event.getKeyCode();
        }
        return super.dispatchKeyEvent(event);
    }

    void startapp()
    {
        if(skip==1) {
            Log.e("HexLauncher ", "StartApp Skipped");
            return;
        }
        Log.e("HexLauncher ","StartApp Called");
        logotimer1 = new Thread() {
            public void run() {
                try {
                    //clearCache();
                    logotimer = 0;
                    while (logotimer < d_time) {
                        sleep(100);
                        logotimer = logotimer + 100;
                    }
                    if(!pause)
                    {
                        Log.e("HexLauncher ","StartAppNot Paused");

                        if(d!=null && d.isShowing())
                        {
                            d.dismiss();
                        }
                        Intent i;

                        PackageManager manager = getPackageManager();
                        try {
                            Log.e("HexLauncher ","PM Called");
                            i = manager.getLaunchIntentForPackage("com.example.deep.camstroke");
                            if (i == null) {
                                i = manager.getLaunchIntentForPackage("com.hexbis.trackhr.attendance");
                                if (i == null) {
                                    i = manager.getLaunchIntentForPackage("com.example.sonaal.hawkeye_monitor");
                                    if (i == null)
                                        throw new PackageManager.NameNotFoundException();
                                }

                            }
                            i.putExtra("Restart",initStart);
                            initStart=false;
                            i.addCategory(Intent.CATEGORY_LAUNCHER);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        } catch (Exception e) {
                            Log.e("HexLauncher ","PM Error");
                            e.printStackTrace();

                        }
                    }

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }

        };
        logotimer1.start();
    }
    @Override
    protected void onResume() {
        //Toast.makeText(this,"Restarting..",Toast.LENGTH_LONG).show();
        //hex.startAnimation(rotate);

        if(d!=null && d.isShowing())
        {
            d.dismiss();
        }
        d_time = 10500;
        pause = false;

        startapp();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause = true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
