package net.bestidear.bestidearlauncher;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public abstract class MyActivity extends Activity {
    
    public final static int Update_weather = 19; 
    private Handler WeatherHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case Update_weather:
                    updateWeather();
                    break;
            }
        }
    };
    
    public abstract void updateWeather();
    
    public Handler getHandler(){
        return WeatherHandler;
    }
}
