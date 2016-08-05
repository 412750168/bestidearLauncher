package net.bestidear.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import net.bestidear.bestidearlauncher.Launcher;
import net.bestidear.bestidearlauncher.MyActivity;
import net.bestidear.bestidearlauncher.R;
import net.bestidear.bestidearlauncher.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;



public class WeatherHandler extends Handler {
    private static final String TAG = WeatherHandler.class.getSimpleName();
    
    private String classDir;
    private MyActivity activity;
    private Context context;
//    private LocationClient locationClient = null;
    public static boolean isDateChange = false;
    public static boolean isCityChange = false;
    
    public WeatherHandler(MyActivity activity){
        this.activity = activity;
        context = activity;
        classDir = context.getFilesDir().getAbsolutePath().replace("/files", "/");
        init();
    }
    
    private void init() {
        String dirPath= classDir+"shared_prefs/city_code.xml";
        File file= new File(dirPath);
        boolean isFirstRun = false;    
        if(!file.exists()) {
            isFirstRun = true;
        }
        if(isFirstRun) {
            importInitDatabase();
        }
        
//        locationClient = new LocationClient(context); 
//        //设置定位条件 
//        LocationClientOption option = new LocationClientOption(); 
//        option.setOpenGps(false);                                //是否打开GPS 
//        option.setCoorType("bd09ll");                           //设置返回值的坐标类型。 
//        option.setPriority(LocationClientOption.NetWorkFirst);  //设置定位优先级 
//        option.setProdName("Location");                     //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。 
//        option.setScanSpan(600000);
//        locationClient.setLocOption(option); 
//        
//        locationClient.registerLocationListener(new BDLocationListener() { 
//            
//            @Override 
//            public void onReceiveLocation(BDLocation location) { 
//                // TODO Auto-generated method stub 
//                if (location == null) { 
//                    return;
//                } 
//                Log.e("++++++++++++++++++++", location.getLatitude()+":::"+location.getLongitude());
//                getLocationCityInfo(location.getLatitude(),location.getLongitude());
//                locationClient.stop();
//            } 
//            @Override 
//            public void onReceivePoi(BDLocation location) { 
//            } 
//        }); 
    }

    
    public final static int CHECK_UPDATE=0x800002;
    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case CHECK_UPDATE:
                Log.d("Handler", "CHECK_UPDATE");
//                CheckAndUpdate();
                break;
        }
    }
    
    public void importInitDatabase() {
        String dirPath= classDir+"databases";
        File dir = new File(dirPath);
        if(!dir.exists()) {
            dir.mkdir();
        }
        File dbfile = new File(dir, "db_weather.db");
        try {
            if(!dbfile.exists()) {
                dbfile.createNewFile();
            }
            InputStream is = context.getApplicationContext().getResources().openRawResource(R.raw.db_weather);
            FileOutputStream fos = new FileOutputStream(dbfile);
            byte[] buffere=new byte[is.available()];
            is.read(buffere);
            fos.write(buffere);
            is.close();
            fos.close();

        }catch(FileNotFoundException  e){
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    private void CheckAndUpdate(){
        
        SharedPreferences shared = context.getSharedPreferences(Launcher.STORE_WEATHER, Launcher.MODE_PRIVATE);
        SharedPreferences sharedCityCode = context.getSharedPreferences(Launcher.CITY_CODE_FILE,Launcher.MODE_PRIVATE);
        String OldCityCode = shared.getString("city", "");
        String NewCityCode = sharedCityCode.getString("cityname", "").substring(sharedCityCode.getString("cityname", "").indexOf(".")+1);

        long currentTime = System.currentTimeMillis();
        //得到天气缓冲文件中的有效期
        long vaildTime = shared.getLong("validTime", currentTime);
            
        SharedPreferences sp=context.getSharedPreferences(Launcher.CITY_CODE_FILE, Launcher.MODE_PRIVATE);
        String cityCode= sp.getString("code", "");
            
        if(!isCityChange && !cityCode.equals("") && cityCode!=null && cityCode.trim().length() > 0) {
            //比较天气缓存文件中的有效期或城市是否改变，如果超时或城市改变了或日期变更，则访问网络更新天气
            if(vaildTime <= currentTime || !OldCityCode.equals(NewCityCode) || !isDateChange){
                if(Utils.isNetworkAvailable(context)){
                  Log.d("访问网络更新天气", "execuating here!");
                    GetWeather(cityCode);
                    isDateChange = false;
                }else{
                    Message message = Message.obtain(activity.getHandler(), Launcher.GoneWeather);
                    message.sendToTarget();
                }
            }else{
                Message message = Message.obtain(activity.getHandler(), Launcher.GoneWeather);
                message.sendToTarget();
            }
        }else{
//            if(Utils.isNetworkConnected(context)){
//              Log.d("else", "execuating here!");
//                if(locationClient.isStarted()){
//                    locationClient.stop();
//                }else{
//                    isCityChange = false;
//                    locationClient.start(); 
//                    locationClient.requestLocation(); 
//                }
//            }else{
////                myHandler.sendEmptyMessage(UPDATE_WIDGET);
//            }
        }
    }
    
    public void getLocationCityInfo(final double latitude,final double longitude) {
        new Thread(){
            public void run(){
                Log.e("WIFI定位", "execuating here!!");
                //根据经纬度得到详细的地址信息
                String url = "http://ugc.map.soso.com/rgeoc/?lnglat="+longitude+","+latitude+"&reqsrc=wb";
              
                String info = new WebAccessTools(context).getWebContent(url);
                if(info != null){
                    Log.e("++++++++++++++++++++", info);
                    JSONArray jsonArr;
                    JSONObject json;
                    try {
                        json = new JSONObject(info).getJSONObject("detail");
                        jsonArr = json.getJSONArray("results");
                        json = jsonArr.getJSONObject(0);
                                
                        info=json.getString("c");
                            
                        info=info.split("市")[0];
                      Log.e("++++++++++++++++++++", info);
                        
                        if(!info.equals("")){
                            DBHelper dbHelper = new DBHelper(context, "db_weather.db");
                            String cityCode = dbHelper.getCityCodeByName(info);
                            SharedPreferences.Editor edit = context.getSharedPreferences(Launcher.CITY_CODE_FILE, Launcher.MODE_PRIVATE).edit();
                            edit.putString("code", cityCode);
                            edit.putString("cityname",info);
                            edit.commit();
                            
                            CheckAndUpdate();
                            
                        }
                            
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
    //                  e.printStackTrace();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }.start();
    }
    
    public void GetWeather(final String cityCode) {
        new Thread(){
            @Override
            public void run(){
            	WeatherInfo weatherInfo = WeatherUtil.getWeatherFromWeb(context, cityCode);
                	
            	if(weatherInfo == null){
            		Message message = Message.obtain(activity.getHandler(), Launcher.GoneWeather);
            		message.sendToTarget();
            		try {
            			sleep(3*1000);
            		} catch (InterruptedException e) {
            			e.printStackTrace();
            		}
            		CheckAndUpdate();
            	}else{
            		WeatherUtil.WeatherinfoSave(context, Launcher.STORE_WEATHER, weatherInfo);
            		
            		Message message = Message.obtain(activity.getHandler(), Launcher.Update_weather);
            		message.sendToTarget();
            	}
            }
        }.start();
        
    }
}
