package net.bestidear.weather;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;
import android.util.Log;
import net.bestidear.weather.WeatherInfo;
import net.bestidear.bestidearlauncher.R;

public class WeatherUtil {
	
	public static WeatherInfo getWeatherFromSP(SharedPreferences sp){
		WeatherInfo weatherInfo = new WeatherInfo(); 
		
		//得到城市
        weatherInfo.setCity(sp.getString("city", "请设置城市"));
        
        //得到阳历日期
        weatherInfo.setDate_y(sp.getString("date_y", "N/A"));
        
        //得到农历
        weatherInfo.setDate(sp.getString("date", "N/A"));
        
        //得到当前温度
        weatherInfo.setTemp(sp.getString("temp", "N/A"));
        
        //得到当前湿度
        weatherInfo.setSD(sp.getString("SD", "N/A"));
        
        //得到温度
        weatherInfo.setTemp1(sp.getString("temp1", "N/A"));
        
        //得到天气
        weatherInfo.setWeather1(sp.getString("weather1", "N/A"));
        
        //天气图标
        weatherInfo.setImg_single(sp.getString("img_single", "N/A"));
        
        //得到风向
        weatherInfo.setWind1(sp.getString("wind1", "N/A"));
        
        //得到建议
        weatherInfo.setIndex_d(sp.getString("index_d", "N/A"));
        
        
        //未来天气 一天
        weatherInfo.setTemp2(sp.getString("temp2", "N/A"));
        
        weatherInfo.setWeather2(sp.getString("weather2", "N/A"));
        
        weatherInfo.setImg3(sp.getString("img3", "N/A"));
        
        weatherInfo.setImg4(sp.getString("img4", "N/A"));
        
        //未来天气 二天
        weatherInfo.setTemp3(sp.getString("temp3", "N/A"));
        
        weatherInfo.setWeather3(sp.getString("weather3", "N/A"));
        
        weatherInfo.setImg5(sp.getString("img5", "N/A"));
        
        weatherInfo.setImg6(sp.getString("img6", "N/A"));
        
        //未来天气 三天
        weatherInfo.setTemp4(sp.getString("temp4", "N/A"));
        
        weatherInfo.setWeather4(sp.getString("weather4", "N/A"));
        
        weatherInfo.setImg7(sp.getString("img7", "N/A"));
        
        weatherInfo.setImg8(sp.getString("img8", "N/A"));
        
        //未来天气 四天
        weatherInfo.setTemp5(sp.getString("temp5", "N/A"));
        
        weatherInfo.setWeather5(sp.getString("weather5", "N/A"));
        
        weatherInfo.setImg9(sp.getString("img9", "N/A"));
        
        weatherInfo.setImg10(sp.getString("img10", "N/A"));
        
        weatherInfo.setValidTime(sp.getLong("validTime", 0));
        
        return weatherInfo;
	}
	
	public static WeatherInfo getWeatherFromWeb(Context context , String cityCode){
		WeatherInfo weatherInfo = new WeatherInfo();
		
		//由城市码更新天气
        StringBuffer str = new StringBuffer("http://m.weather.com.cn/data/");
        str.append(cityCode);
        str.append(".html");
        
        StringBuffer str2 = new StringBuffer("http://www.weather.com.cn/data/sk/");
        str2.append(cityCode);
        str2.append(".html");
        
        try {
          Log.d("GetWeather", str.toString()+"");
          Log.d("GetWeather", str2.toString()+"");
            String info = new WebAccessTools(context).getWebContent(str.toString());
            String info2 = new WebAccessTools(context).getWebContent(str2.toString());
            if(info == null || info2 == null){
                return null;
            }else{
                JSONObject json= new JSONObject(info).getJSONObject("weatherinfo");
                JSONObject json2= new JSONObject(info2).getJSONObject("weatherinfo");
                
                //得到城市
                weatherInfo.setCity(json.getString("city"));
                
                //得到阳历日期
                weatherInfo.setDate_y(json.getString("date_y"));
                
                //得到农历
                weatherInfo.setDate(json.getString("date"));
                
                //得到当前温度
                weatherInfo.setTemp(json2.getString("temp"));
                
                //得到当前湿度
                weatherInfo.setSD(json2.getString("SD"));
                
                //得到温度
                weatherInfo.setTemp1(json.getString("temp1"));
                
                //得到天气
                weatherInfo.setWeather1(json.getString("weather1"));
                
                //天气图标
                weatherInfo.setImg_single(json.getString("img_single"));
                
                //得到风向
                weatherInfo.setWind1(json.getString("wind1"));
                
                //得到建议
                weatherInfo.setIndex_d(json.getString("index_d"));
                
                
                //未来天气 一天
                weatherInfo.setTemp2(json.getString("temp2"));
                
                weatherInfo.setWeather2(json.getString("weather2"));
                
                weatherInfo.setImg3(json.getString("img3"));
                
                weatherInfo.setImg4(json.getString("img4"));
                
                //未来天气 二天
                weatherInfo.setTemp3(json.getString("temp3"));
                
                weatherInfo.setWeather3(json.getString("weather3"));
                
                weatherInfo.setImg5(json.getString("img5"));
                
                weatherInfo.setImg6(json.getString("img6"));
                
                //未来天气 三天
                weatherInfo.setTemp4(json.getString("temp4"));
                
                weatherInfo.setWeather4(json.getString("weather4"));
                
                weatherInfo.setImg7(json.getString("img7"));
                
                weatherInfo.setImg8(json.getString("img8"));
                
                //未来天气 四天
                weatherInfo.setTemp5(json.getString("temp5"));
                
                weatherInfo.setWeather5(json.getString("weather5"));
                
                weatherInfo.setImg9(json.getString("img9"));
                
                weatherInfo.setImg10(json.getString("img10"));
                
    
                
                //设置有效日期5小时
                long validTime = System.currentTimeMillis();
                validTime = validTime + 5*60*60*1000;
                weatherInfo.setValidTime(validTime);
                
            }
        }catch(JSONException e) {
            return null;
        }
		
        return weatherInfo;
	}
	
	public static void WeatherinfoSave(Context context , String filename , WeatherInfo weatherInfo){
		if(weatherInfo == null)
			return;
		
		SharedPreferences.Editor editor = context.getSharedPreferences(filename,context.MODE_PRIVATE).edit();
	
		//得到城市
        editor.putString("city", weatherInfo.getCity());
        
        //得到阳历日期
        editor.putString("date_y", weatherInfo.getDate_y());
        
        //得到农历
        editor.putString("date", weatherInfo.getDate());
        
        //得到当前温度
        editor.putString("temp", weatherInfo.getTemp());
        
        //得到当前湿度
        editor.putString("SD", weatherInfo.getSD());
        
        //得到温度
        editor.putString("temp1", weatherInfo.getTemp1());
        
        //得到天气
        editor.putString("weather1", weatherInfo.getWeather1());
        
        //天气图标
        editor.putString("img_single", weatherInfo.getImg_single());
        
        //得到风向
        editor.putString("wind1", weatherInfo.getWind1());
        
        //得到建议
        editor.putString("index_d", weatherInfo.getIndex_d());
        
        
        //未来天气 一天
        editor.putString("temp2", weatherInfo.getTemp2());
        
        editor.putString("weather2", weatherInfo.getWeather2());
        
        editor.putString("img3", weatherInfo.getImg3());
        
        editor.putString("img4", weatherInfo.getImg4());
        
        //未来天气 二天
        editor.putString("temp3", weatherInfo.getTemp3());
        
        editor.putString("weather3", weatherInfo.getWeather3());
        
        editor.putString("img5", weatherInfo.getImg5());
        
        editor.putString("img6", weatherInfo.getImg6());
        
        //未来天气 三天
        editor.putString("temp4", weatherInfo.getTemp4());
        
        editor.putString("weather4", weatherInfo.getWeather4());
        
        editor.putString("img7", weatherInfo.getImg7());
        
        editor.putString("img8", weatherInfo.getImg8());
        
        //未来天气 四天
        editor.putString("temp5", weatherInfo.getTemp5());
        
        editor.putString("weather5", weatherInfo.getWeather5());
        
        editor.putString("img9", weatherInfo.getImg9());
        
        editor.putString("img10", weatherInfo.getImg10());

        //设置有效日期5小时
        editor.putLong("validTime", weatherInfo.getValidTime());
        
        //保存
        editor.commit();
	}
	
	
	//解析天气图标
    public static int getWeatherBitMapResource(String weather) {
        //Log.i("weather_info", "============="+weather+"===============");
        Time t=new Time();
        t.setToNow();
        //Log.i("time_info", "============="+t.hour+"===============");
        if(weather.equals("0")&&t.hour < 18) {
            return R.drawable.weathericon_condition_01;
        } else if(weather.equals("0")&&t.hour >= 18) {
            return R.drawable.weathericon_condition_15;
        } else if(weather.equals("1")&&t.hour < 18) {
            return R.drawable.weathericon_condition_02;
        }else if(weather.equals("1")&&t.hour >= 18) {
            return R.drawable.weathericon_condition_16;
        } else if(weather.equals("2")) {
            return R.drawable.weathericon_condition_04;
        } else if(weather.equals("18")) {
            return R.drawable.weathericon_condition_05;
        } else if(weather.equals("20") || weather.equals("29") || weather.equals("30") || weather.equals("31")) {
            return R.drawable.weathericon_condition_06;
        } else if(weather.equals("3") || weather.equals("7") || weather.equals("21")) {
            return R.drawable.weathericon_condition_07;
        } else if(weather.equals("8") || weather.equals("9") || weather.equals("22") || weather.equals("23")) {
            return R.drawable.weathericon_condition_08;
        } else if(weather.equals("10") || weather.equals("11") || weather.equals("12") || weather.equals("24") || weather.equals("25")) {
            return R.drawable.weathericon_condition_09;
        } else if(weather.equals("4") || weather.equals("5")) {
            return R.drawable.weathericon_condition_10;
        } else if(weather.equals("13") || weather.equals("14") || weather.equals("15") || weather.equals("26")) {
            return R.drawable.weathericon_condition_11;
        } else if(weather.equals("16") || weather.equals("17") || weather.equals("27") || weather.equals("28")) {
            return R.drawable.weathericon_condition_12;
        } else if(weather.equals("6")) {
            return R.drawable.weathericon_condition_13;
        }else if(weather.equals("19")) {
            return R.drawable.weathericon_condition_14;
        }else {
            return R.drawable.weathericon_condition_17;
        }
    }
}
