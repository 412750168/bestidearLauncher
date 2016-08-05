package net.bestidear.weather;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import net.bestidear.bestidearlauncher.Launcher;
import net.bestidear.bestidearlauncher.MyActivity;
import net.bestidear.bestidearlauncher.R;
import net.bestidear.bestidearlauncher.utils.StringUtil;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

public class SetCityActivity extends MyActivity {

    public static final String Tag = "SetCityActivity";
    
	private TextView filterText;
	
	public static final String CITY_CODE_FILE="city_code";
    public static final String STORE_WEATHER="store_weather";
	private static final String SERVICE_ACTION = "net.bestidear.weatherwidget.UpdateService";
	private String[] cityCodes;
	private String[] groups;
    private String[] childs;
    private String CityName = null;

    private Spinner spinner_province;
    private Spinner spinner_city;
    
//    private WeatherHandler weatherHandler;
    
    DBHelper dbHelper;
    
    boolean isFirstRun;
    boolean Launch = true;
    
    private TextView today_date;
    private ImageView today_icon;
    private TextView today_weather;
    private TextView today_temp;
    
    private TextView tomorrow_date;
    private ImageView tomorrow_icon;
    private TextView tomorrow_weather;
    private TextView tomorrow_temp;

    private String classDir;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setloc);
        classDir = this.getFilesDir().getAbsolutePath().replace("/files", "/");
        
        spinner_province = (Spinner) findViewById(R.id.spinner_province);
        spinner_city = (Spinner) findViewById(R.id.spinner_city);
        
        today_date = (TextView) findViewById(R.id.today_date);
        today_icon = (ImageView) findViewById(R.id.today_icon);
        today_weather = (TextView) findViewById(R.id.today_weather);
        today_temp = (TextView) findViewById(R.id.today_temp);
        
        tomorrow_date = (TextView) findViewById(R.id.tomorrow_date);
        tomorrow_icon = (ImageView) findViewById(R.id.tomorrow_icon);
        tomorrow_weather = (TextView) findViewById(R.id.tomorrow_weather);
        tomorrow_temp = (TextView) findViewById(R.id.tomorrow_temp);
        
//        weatherHandler = new WeatherHandler(this);
        
        
        dbHelper = new DBHelper(this, "db_weather.db");
       
        String dirPath= classDir+"shared_prefs/city_code.xml";
        File file= new File(dirPath);
        
        isFirstRun = false;    
        if(!file.exists()) {
        	isFirstRun = true;
        }
        
        if(isFirstRun) {
        	importInitDatabase();
        	
        }else{
            updateWeather();
        }
        
        
        groups = dbHelper.getAllProvinces();
        Log.d(Tag, groups.toString());
                
        init();
        
        
    }

    private void init() {
        ArrayAdapter provinceAdapter = new ArrayAdapter(this , android.R.layout.simple_spinner_item,groups);
        spinner_province.setAdapter(provinceAdapter);
        if(Launch){
            if(!isFirstRun) {
                SharedPreferences sp = this.getSharedPreferences(Launcher.CITY_CODE_FILE, Launcher.MODE_PRIVATE);
                String cityCode= sp.getString("code", "");
                
                String ProvinceCode = dbHelper.getProvinceCodeByCityNum(cityCode);
                Log.d(Tag, "ProvinceCode:"+ProvinceCode);
                
                spinner_province.setSelection(Integer.parseInt(ProvinceCode));
            }
        }
        
        spinner_province.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                updateCitybyProvince(spinner_province.getSelectedItemPosition()+"");
//                Log.d(Tag, arg2+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                
            }
            
        });
        
        
        
    }
    
    private void updateCitybyProvince(String postion){
//        DBHelper dbHelper = new DBHelper(this, "db_weather.db");
        List<String[]> result = dbHelper.getCityAndCodeByProvince(postion);
        childs = result.get(0);
        cityCodes = result.get(1);
        
        ArrayAdapter cityAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, childs);
        spinner_city.setAdapter(cityAdapter);
        if(Launch){
            if(!isFirstRun) {
                SharedPreferences sp = this.getSharedPreferences(Launcher.CITY_CODE_FILE, Launcher.MODE_PRIVATE);
                String cityCode= sp.getString("code", "");
                
                int count = 0;
                for(String temp:cityCodes){
                    if(temp.equals(cityCode)){
                        break;
                    }
                    count++;
                }
                Log.d(Tag, "cityCodes:"+count);
                spinner_city.setSelection(count);
            }
            Launch = false;
        }
        
        spinner_city.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                Log.d(Tag, "change+++"+arg2);
                Log.d(Tag, "cityCodes"+cityCodes[arg2]);
                SharedPreferences.Editor edit = getSharedPreferences(Launcher.CITY_CODE_FILE, Launcher.MODE_PRIVATE).edit();
                edit.putString("code", cityCodes[arg2]);
                edit.putString("cityname",childs[arg2]);
                edit.commit();
                
                
//                weatherHandler.removeMessages(WeatherHandler.CHECK_UPDATE);
//                weatherHandler.sendEmptyMessage(WeatherHandler.CHECK_UPDATE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                
            }
        });
    }

    public void importInitDatabase() {
    	String dirPath=classDir+"databases";
    	File dir = new File(dirPath);
    	if(!dir.exists()) {
    		dir.mkdir();
    	}
    	File dbfile = new File(dir, "db_weather.db");
    	try {
    		if(!dbfile.exists()) {
    			dbfile.createNewFile();
    		}
    		InputStream is = this.getApplicationContext().getResources().openRawResource(R.raw.db_weather);
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

    @Override
    public void updateWeather() {
        SharedPreferences sp = this.getSharedPreferences(STORE_WEATHER , MODE_PRIVATE);
        
        WeatherInfo weatherInfo = WeatherUtil.getWeatherFromSP(sp);
        Log.d("",weatherInfo.getDate_y());
        if(StringUtil.isToday(weatherInfo.getDate_y())){
            Log.d("",weatherInfo.getDate_y());
            today_date.setText(weatherInfo.getDate_y());
            
            today_weather.setText(weatherInfo.getWeather1());
            
            today_temp.setText(weatherInfo.getTemp1());
            
            
            int icon = WeatherUtil.getWeatherBitMapResource(weatherInfo.getImg_single());
            today_icon.setImageResource(icon);
            
            Calendar ca = Calendar.getInstance(Locale.CHINA);
            ca.add(Calendar.DATE, 1);
            tomorrow_date.setText(ca.get(Calendar.YEAR)+"年"+(ca.get(Calendar.MONTH)+1)+"月"+ca.get(Calendar.DAY_OF_MONTH)+"日");
            
            tomorrow_weather.setText(weatherInfo.getWeather2());
            
            icon = WeatherUtil.getWeatherBitMapResource(weatherInfo.getImg3());
            tomorrow_icon.setImageResource(icon);
            
            tomorrow_temp.setText(weatherInfo.getTemp2());
            
        }else{
            
        }
        
//        weatherHandler.removeMessages(WeatherHandler.CHECK_UPDATE);
//        weatherHandler.sendEmptyMessageDelayed(WeatherHandler.CHECK_UPDATE, 30*60*1000);
    }
    
    
    
}
