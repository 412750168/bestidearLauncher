package net.bestidear.bestidearlauncher.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import net.bestidear.bestidearlauncher.Launcher;
import net.bestidear.bestidearlauncher.LauncherApplication;
import net.bestidear.bestidearlauncher.R;
import net.bestidear.bestidearlauncher.utils.ImageUtil;
import net.bestidear.bestidearlauncher.utils.StringUtil;
import net.bestidear.bestidearlauncher.workplace.WorkplaceConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AddLayout extends RelativeLayout {
    private final String Tag = "addLayout";
    
    private Context context;
    
    private Handler mHandler;
    
    private int worklayouttoLeft = (int) getResources().getDimension(R.dimen.worklayouttoLeft);
    
    public AddLayout(Context context) {
        this(context , null);
    }

    public AddLayout(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public AddLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
//        View.inflate(context, R.layout.workplacelayoutview, this);
    }

    public void makeAddLayout(){
        
        int _newcellWidth=60;
        int _newcellHeight = 60;
        
        LayoutParams layoutParams = new LayoutParams(_newcellWidth, _newcellHeight);
        layoutParams.setMargins(worklayouttoLeft, 20 , 0, 0);
        
        LinearLayout mLayout = new LinearLayout(context);
        mLayout.setBackgroundResource(R.drawable.main_addicon_normal);
        mLayout.setFocusable(true);
        
        mLayout.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if(arg1){
                    Log.d(Tag, "fuck1");
                    focus();
                }else{
                    Log.d(Tag, "1fuck");
                    remove();
                }
            }
        });
        
        mLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                CLick();
            }
        });
        
        
        this.addView(mLayout,layoutParams);
        
    }
    
    public void setHandler(Handler handler){
        mHandler = handler;
    }
    
    public void focus(){
        Log.d(Tag, "fuck2");
        
        int _newcellWidth=100;
        int _newcellHeight = 100;
        
        LayoutParams layoutParams = new LayoutParams(_newcellWidth, _newcellHeight);
        layoutParams.setMargins(worklayouttoLeft-20, 0 , 0, 0);
        
        LinearLayout mLayout = new LinearLayout(context);
        
        Drawable drawable = this.getResources().getDrawable(R.drawable.main_addicon_normal);
        Drawable drawableWithShadow = ImageUtil.drawLayoutDropShadow(context.getResources(),drawable);
        
        mLayout.setBackgroundDrawable(drawableWithShadow);
        mLayout.setAnimation(AnimationUtils.loadAnimation(context, R.anim.pull_right_in));
        this.addView(mLayout,1,layoutParams);
        
    }
    
    public void remove(){
        Log.d(Tag, "2fuck");
        this.removeViewAt(1);
    }
    
    
    private void CLick(){
        new Thread(){
            @Override
            public void run(){
                loadApplications();
                loadStates();
                
                for(int i= 0;i<appnames.size();i++){
                    Log.d(Tag, "fuck ~~~~~~~~~~~~~"+i+appnames.get(i).packagename);
                    Log.d(Tag, "fuck ~~~~~~~~~~~~~"+i+appstate.get(i).toString());
                }
                
                mHandler.sendEmptyMessage(Launcher.ShowDailog);
            }
        }.start();
    }
    
    
    public static List<ApplicationInfo> appnames = new ArrayList<ApplicationInfo>();
    public static List<Boolean> appstate;
    private List<String> delApp;
    
    private void loadStates(){
        appstate = new ArrayList<Boolean>();
        
      SharedPreferences sp = context.getSharedPreferences(Launcher.LAUNCHER_CUSTOM_FILE, Launcher.MODE_PRIVATE);
      String temp = sp.getString("apps", "");
//      String temp = "com.togic.upnp,com.cloud.tv";
      String apks[] = temp.trim().split(",");
      
      for(int i = 0; i< appnames.size();i++){
          int index = 0;
          for(String temp1:apks){
              if(temp1.equals(appnames.get(i).packagename)){
                  break;
              }
              index++;
          }
          if(index>=apks.length){
              appstate.add(false);
          }else{
              appstate.add(true);
          }
      }
    }
    
    private void loadApplications() {
        
        PackageManager manager = context.getPackageManager();
        
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        
        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
        
        if (appnames != null) {
            final int count = apps.size();
            
            if (appnames == null) {
                appnames = new ArrayList<ApplicationInfo>(count);
            }
            appnames.clear();
            
            for (int i = 0; i < count; i++) {
                ApplicationInfo application = new ApplicationInfo();
                ResolveInfo info = apps.get(i);
                
                application.title = (String) info.loadLabel(manager);
                application.packagename = info.activityInfo.applicationInfo.packageName;
                application.icon = info.activityInfo.loadIcon(manager);
                
                if(LauncherApplication.delApp.length > 0){
                	int j = 0;
                	boolean flag = false;
                	for(;j<LauncherApplication.delApp.length;j++){
                		String str = LauncherApplication.delApp[j];
                		if(str.equals(application.packagename))
                			flag = true;
                	}
                	if(!flag)
                		appnames.add(application);
                	
                }else {
                    appnames.add(application);
                }
                Log.d(Tag, "application"+application.packagename);
            }
        }
//        getDelapk();
        appRemove();
    }
    
    private void getDelapk(){
        WorkplaceConfig workplaceConfig = new WorkplaceConfig(context);
        List<CellInfo> listcell = workplaceConfig.getConfig(0);
        
        delApp = new ArrayList<String>();
        if(listcell != null){
            for(CellInfo temp:listcell){
                if(!temp.getPackageName().equals("")){
                    delApp.add(temp.getPackageName());
                    Log.d(Tag, "delApp"+temp.getPackageName());
                }
            }
            
            
            for(String temp:LauncherApplication.delApp){
                delApp.add(temp);
                Log.d(Tag, "delApp"+temp);
            }
        }
    }
    
    private void appRemove(){
        if(delApp==null||appnames==null||delApp.size()<=0||appnames.size()<=0){
            return;
        }
        for(int i= 0;i<appnames.size();i++){
            Log.d(Tag, "appnamesappnamesappnames"+i+appnames.get(i).packagename);
        } 
        for(int i= 0;i<delApp.size();i++){
            Log.d(Tag, "delAppdelAppdelAppdelApp"+i+delApp.get(i));
        }
        
        int i = 0;
        ArrayList<String> delList = new ArrayList<String>();
        for(ApplicationInfo appInfo:appnames){
            
            String packageName = appInfo.packagename;
            for(String temp:delApp){
//                Log.d(Tag, appInfo.intent.getComponent().getPackageName());
//                Log.d(Tag, temp);
                if(packageName.equals(temp)){
                    Log.d(Tag, "delList.add(i+"+i);
                    delList.add(i+"");
//                    break;
                }
            }
            i++;
        }
        Log.d(Tag, "delList.size():"+delList.size());
        for(int k=delList.size()-1;k>=0;k--){
            Log.d(Tag, "remove:"+delList.get(k));
            appnames.remove(Integer.parseInt(delList.get(k)));
        }
    }
}
