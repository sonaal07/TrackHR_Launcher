package com.hex.trackhr.launcher;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    int logotimer;
    Animation rotate, zoomin,rotate_half;
    ImageView hex, hex_inner, hex_outer;
    Timer timer;
    TimerTask timerTask;
    ImageView logo;
    TextView tvhexbis;
    static String Settings_Password="727595";
    Boolean pause = false;
    Dialog d;
    int d_time = 20500;
    Context c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logo = (ImageView)findViewById(R.id.ivset);
        tvhexbis = (TextView) findViewById(R.id.tvhexbis);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/spaceage.ttf");
        tvhexbis.setTypeface(typeface);


        c = this;
        //startapp();
        logo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                d = new Dialog(c, android.R.style.Theme_DeviceDefault_Dialog);
                d.setTitle("Enter Password");
                d.setContentView(R.layout.settings_password);
                final EditText pass;
                Button ok;
                pass = (EditText) d.findViewById(R.id.etpass);
                ok = (Button) d.findViewById(R.id.btnok);
                pass.setHint("");
                pass.setText("");
                pass.requestFocus();

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (pass.getText().toString().equals(Settings_Password)) {
                            d_time = 20500;
                            if (ShellInterface.isSuAvailable()) {
                                try {
                                    //ShellInterface.runCommand("service call activity 42 s16 com.android.systemui");
                                    ShellInterface.runCommand("am startservice -n com.android.systemui/.SystemUIService");
                                    Toast.makeText(getApplicationContext(),"Bar On",Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            d.dismiss();


                        } else {
                            pass.setHint("Wrong Password");
                            pass.setText("");
                        }

                    }
                });
                d.show();
                return true;
            }
        });

        if (ShellInterface.isSuAvailable()) {
            try {
                //ShellInterface.runCommand("service call activity 42 s16 com.android.systemui");
                ShellInterface.runCommand("setprop service.adb.tcp.port 5555");
                ShellInterface.runCommand("stop adbd");
                ShellInterface.runCommand("start adbd");
                Toast.makeText(getApplicationContext(),"ADB ON",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ShellInterface.isSuAvailable()) {
            try {
                ShellInterface.runCommand("service call activity 42 s16 com.android.systemui");
                //ShellInterface.runCommand("am startservice -n com.android.systemui/.SystemUIService");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        hex = (ImageView)findViewById(R.id.hex);
        hex_inner = (ImageView)findViewById(R.id.ivlogo_inner);
        hex_outer = (ImageView)findViewById(R.id.ivlogo_outer);

        rotate_half = AnimationUtils.loadAnimation(this, R.anim.rotate_half);
        zoomin = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        //hex_inner.startAnimation(zoomin);
        zoomin.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        hex_outer.startAnimation(rotate);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        hex_inner.clearAnimation();
                        //hex_inner.startAnimation(rotate_half);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                rotate_half.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        hex_inner.setRotation(90f);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
                //hex.startAnimation(rotate);



            //disable = true;


    }

    Thread logotimer1;
    int skip = 0;
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
                            i.addCategory(Intent.CATEGORY_LAUNCHER);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
