package net.bestidear.bestidearlauncher;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.IWindowManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;


import net.bestidear.bestidearlauncher.Launcher.PhoneStateMonitor;
import net.bestidear.bestidearlauncher.model.ApplicationInfo;
import net.bestidear.bestidearlauncher.model.WorkplaceLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class allAppActivity extends Activity {
    private static final String Tag = "allAppActivity"; 
    
    
    private HorizontalScrollView Hsv;
    private static WorkplaceLayout workplace ;
    private static boolean isRunning = false;
    private LinearLayout base2;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allapp);
        
        IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
	    try{
	            mWindowManager.setAnimationScale(1, 0.0f);
	        } catch (RemoteException e) {
	        }
		
        
        isRunning = true;
        
        Hsv = (HorizontalScrollView) findViewById(R.id.Hsv2);
        workplace = (WorkplaceLayout) findViewById(R.id.allappworkplaceLayout);
        base2 = (LinearLayout)findViewById(R.id.base2);
        setWallpaper();
        bindApplications();
    }
    
    private void setWallpaper() {
    	File file = new File("/etc/biconf/", "defaultwallpaper.jpg");
    	if(file.exists()){
    		BitmapDrawable bd = new BitmapDrawable(getResources(), file.getAbsolutePath());
    		base2.setBackground(bd);
    	}else{
    		base2.setBackgroundResource(R.drawable.main_bg);
    	}
    }
    
    private static void bindApplications() {
        if(LauncherApplication.mApplications.size()>0&&isRunning){
            workplace.removeAllViews();
            workplace.makeAllAppCelllayout(LauncherApplication.mApplications);
            workplace.requestFocus();
        }
    }

    
    public final static int RESET = 8;
    
    private static Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case RESET:bindApplications();break;
            }
        }
    };
    
    public static Handler getHandler(){
        return myHandler;
    }
    
    
    @Override
    public void onPause(){
        super.onPause();
        isRunning = false;
    }
    
    @Override
    public void onResume(){
        super.onResume();
        isRunning = true;
        bindApplications();
    }
    
    @Override
    public void onRestart(){
        super.onRestart();
        bindApplications();
    }

    
    
}
