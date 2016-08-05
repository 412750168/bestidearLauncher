package net.bestidear.bestidearlauncher.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import net.bestidear.bestidearlauncher.model.CellInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    
    
    public static File getFileFromNetwork(String url, File file,
            int paramInt) {
        Log.d("net.bestidear.bestidearlauncher.utils.Utils", "load path:" + url);
        File localFile = new File(file, "workplace.txt");
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(
                        url).openConnection();
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                if (httpURLConnection.getResponseCode() == 200) {
                    inputStream = httpURLConnection.getInputStream();
                    fileOutputStream = new FileOutputStream(localFile);
                    byte[] arrayOfByte = new byte[1024];
                    while (true) {
                        int i = inputStream.read(arrayOfByte);
                        if (i == -1)
                            break;
                        fileOutputStream.write(arrayOfByte, 0, i);
                    }
                }
                inputStream.close();
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return localFile;
    }
    
    public static String getStringFromFile(String paramFile)
            throws FileNotFoundException, IOException {
        BufferedReader localBufferedReader = new BufferedReader(new FileReader(
                paramFile));
        StringBuilder localStringBuilder = new StringBuilder();
        while (true) {
            String str = localBufferedReader.readLine();
            if (str == null)
                break;
            localStringBuilder.append(str);
        }
        return localStringBuilder.toString();
    }
    
    public static String getStringFromInputStream(InputStream paramInputStream)
            throws FileNotFoundException, IOException {
        
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(paramInputStream)); 
        do{
            String s = bufReader.readLine();
            if(s == null)
                break;
            stringBuilder.append(s);
        }while(true);
        if(paramInputStream !=null)
            paramInputStream.close();
        if(bufReader!=null)
            bufReader.close();
                                    
        return stringBuilder.toString();
        
    }
    
    public static boolean isNetworkAvailable(Context context) { 
        ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
        if (connectivity == null) { 
            Log.i("NetWorkState", "Unavailabel"); 
            return false; 
        } else { 
            NetworkInfo[] info = connectivity.getAllNetworkInfo(); 
            if (info != null) { 
                for (int i = 0; i < info.length; i++) { 
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) { 
                        Log.i("NetWorkState", "Availabel"); 
                        return true; 
                    } 
                } 
            } 
        } 
            return false; 
    }
    
    public static boolean isWifiConnected(Context context) { 
        if (context != null) { 
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
        .getSystemService(Context.CONNECTIVITY_SERVICE); 
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager 
        .getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
        if (mWiFiNetworkInfo != null) { 
        return mWiFiNetworkInfo.isAvailable(); 
        } 
        } 
        return false; 
        }
    public static List<CellInfo> doJsonParser(String jsonData){
        List<CellInfo> listCellInfo = new ArrayList<CellInfo>();
        try {
            JSONObject obj = new JSONObject(jsonData);
            JSONArray cellArray = obj.getJSONArray("workplace");
            JSONObject temp = null;
            CellInfo cellinfo;
            for(int i = 0; i < cellArray.length() ; i++ ){
                cellinfo = new CellInfo();
                temp = cellArray.getJSONObject(i);
//                Log.d(Tag,"temp = " + temp);
//                Log.d(Tag,"temp = " + temp.getString("x")+","+temp.getString("y"));
                if(temp != null){
                    cellinfo.setCellName(temp.getString("cellName"));
                    cellinfo.setPackageName(temp.getString("packageName"));
                    cellinfo.setClassName(temp.getString("className"));
                    cellinfo.setType(StringUtil.toInteger(temp.getString("type")));
                    cellinfo.setBackground(temp.getString("background"));
                    cellinfo.setOrderX(StringUtil.toInteger(temp.getString("orderX")));
                    cellinfo.setOrderY(StringUtil.toInteger(temp.getString("orderY")));
                    cellinfo.setSpanX(StringUtil.toInteger(temp.getString("spanX")));
                    cellinfo.setSpanY(StringUtil.toInteger(temp.getString("spanY")));
                    listCellInfo.add(cellinfo);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return listCellInfo;
        }
    
    public static List<String> doDelappJsonParser(String jsonData){
        List<String> list = new ArrayList<String>();
        try {
            JSONObject obj = new JSONObject(jsonData);
            JSONArray AppArray = obj.getJSONArray("delApp");
            for(int i = 0; i < AppArray.length() ; i++ ){
                String packageName = AppArray.getJSONObject(i).getString("packageName");
                list.add(packageName);
            }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return list;
        }
}
