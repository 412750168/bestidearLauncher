package net.bestidear.bestidearlauncher;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.IWindowManager;

import net.bestidear.bestidearlauncher.model.ApplicationInfo;
import net.bestidear.bestidearlauncher.model.CellInfo;
import net.bestidear.bestidearlauncher.workplace.WorkplaceConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LauncherApplication extends Application {    
    
    private static String Tag = "LauncherApplication";
    public static ArrayList<ApplicationInfo> mApplications;
    
    private String[] topApp = {"net.bestidear.jettyinput"};
//    private String[] delApp = {"com.togic.upnp","com.android.settings","com.android.providers.downloads.ui","com.amlogic.PPPoE","com.android.quicksearchbox","net.bestidear.ota"};
//    public static String[] delApp = {"com.android.settings","com.android.providers.downloads.ui","com.android.quicksearchbox","net.bestidear.ota"};
    public static String[] delApp = {"com.hpplay.happyplay.aw","com.android.settings"};
    
//    public static void setdelApp(List<String> test){
//        delApp  = (String[]) test.toArray(new String[test.size()]);
//        for(String temp:delApp){
//            Log.i("delApp", temp);
//        }
//    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
	    try{
	            mWindowManager.setAnimationScale(1, 0.0f);
	        } catch (RemoteException e) {
	        }
		
        loadApplications(true);
        registerIntentReceivers();
    }
    
    public boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			android.content.pm.ApplicationInfo info = context
					.getPackageManager().getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
    
    public BroadcastReceiver packageReceiver = new BroadcastReceiver() {  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            if(intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED))
            {
                //处理
                Log.v("test", "received ACTION_LOCALE_CHANGED");
                loadApplications(false);
                if(allAppActivity.getHandler()!=null){
                    Message.obtain(allAppActivity.getHandler(), allAppActivity.RESET).sendToTarget();
                    
                }
            }        
            if(intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) ||
                    intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED) ||
                    intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)){
            	
            	if(intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)){
            		
            		String packageName = intent.getDataString().substring(8);   
            		SharedPreferences sp = context.getSharedPreferences(
            				Launcher.LAUNCHER_CUSTOM_FILE, Launcher.MODE_PRIVATE);
            		
            		SharedPreferences.Editor editor = getSharedPreferences(
            				Launcher.LAUNCHER_CUSTOM_FILE, Launcher.MODE_PRIVATE).edit();
            		
            			String str = sp.getString("apps", "");
            			WorkplaceConfig workplaceConfig = new WorkplaceConfig(context);

            			List<CellInfo> list = workplaceConfig.getConfig(0);
            			for (CellInfo cellinfo : list) {

            				if (!cellinfo.getPackageName().equals("")
            						&& checkApkExist(LauncherApplication.this,cellinfo.getPackageName())
            						&& !str.contains(cellinfo.getPackageName()) && cellinfo.getPackageName().equals(packageName)) {
            					str = str +","+ cellinfo.getPackageName() + ",";
            				}

            			}
            			editor.putString("apps", str);
            			editor.putBoolean("FirstRun", false);
            			editor.commit();
            		
            	}
            	            	
                Log.d(Tag, "loadApplications()");
                loadApplications(false);
                if(allAppActivity.getHandler()!=null){
                    Message.obtain(allAppActivity.getHandler(), allAppActivity.RESET).sendToTarget();
                    
                }
            }
        }
    };
    
    public BroadcastReceiver LocaleReceiver = new BroadcastReceiver() {  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            if(intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED))
            {
                //处理
                Log.v("test", "received ACTION_LOCALE_CHANGED");
                loadApplications(false);
                if(allAppActivity.getHandler()!=null){
                    Message.obtain(allAppActivity.getHandler(), allAppActivity.RESET).sendToTarget();
                    
                }
            }        
        }
    };
    private void registerIntentReceivers() {
        
        IntentFilter filter = new IntentFilter();
        
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(packageReceiver, filter);
        
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        registerReceiver(LocaleReceiver, filter);
        
        
    }
    
    private void unregisterIntentReceivers(){
        unregisterReceiver(packageReceiver);
        unregisterReceiver(LocaleReceiver);
    }
    
    private void appInfoTolistop(ApplicationInfo appInfo,int index){
        for(;index > 0;index--){
            mApplications.set(index, mApplications.get(index-1));
        }
        mApplications.set(0, appInfo);
    }
    
    private void appTotop(){
        int i = 0;
        for(ApplicationInfo appInfo:mApplications){
//            Log.d(Tag, appInfo.intent.getComponent().getPackageName());
            String packageName = appInfo.intent.getComponent().getPackageName();
            for(String temp:topApp){
                if(packageName.equals(temp)){
                    appInfoTolistop(appInfo , i);
                    break;
                }
            }
            i++;
        }
    }
    
    private void appRemove(){
        int i = 0;
        ArrayList<String> delList = new ArrayList<String>();
        for(ApplicationInfo appInfo:mApplications){
            
            String packageName = appInfo.intent.getComponent().getPackageName();
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
            mApplications.remove(Integer.parseInt(delList.get(k)));
        }
    }
    
    /**
     * Loads the list of installed applications in mApplications.
     */
    private void loadApplications(boolean isLaunching) {
        if (isLaunching && mApplications != null) {
            return;
        }
        
        PackageManager manager = getPackageManager();
        
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        
        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
        
        if (apps != null) {
            final int count = apps.size();
            
            if (mApplications == null) {
                mApplications = new ArrayList<ApplicationInfo>(count);
            }
            mApplications.clear();
            
            for (int i = 0; i < count; i++) {
                ApplicationInfo application = new ApplicationInfo();
                ResolveInfo info = apps.get(i);
                application.packagename = info.activityInfo.applicationInfo.packageName;
                application.title = (String) info.loadLabel(manager);
                application.setActivity(new ComponentName(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name),
                        Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                application.icon = info.activityInfo.loadIcon(manager);
                
                mApplications.add(application);
            }
        }
        appRemove();
        appTotop();
    }
}
