package net.bestidear.bestidearlauncher.workplace;

import android.content.Context;
import android.util.Log;

import net.bestidear.bestidearlauncher.Launcher;
import net.bestidear.bestidearlauncher.LauncherApplication;
import net.bestidear.bestidearlauncher.model.CellInfo;
import net.bestidear.bestidearlauncher.utils.StringUtil;
import net.bestidear.bestidearlauncher.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WorkplaceConfig {

    
    public List<CellInfo> listCellinfo;
    private Context mContext;
    
    public WorkplaceConfig(Context context){
        this.mContext = context;
    }
    
    public List<CellInfo> getConfig(int paramInt){
        File file = new File("/etc/biconf/", "defaultworkplace.json");
        Log.d("WorkplaceConfig:::::", file.getAbsolutePath());
        InputStream inputStream =null;
        String s = null;
        if(file.exists()){
            
            try {
                Log.d("WorkplaceConfig", "file");
                inputStream = new FileInputStream(file);
                s = Utils.getStringFromInputStream(inputStream);
//                    Launcher.isUseDefault = false;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
            
        }else{
            try {
                Log.d("WorkplaceConfig", "defaultworkplace.txt2");
                inputStream = mContext.getAssets().open("defaultworkplace.json");
                s = Utils.getStringFromInputStream(inputStream);
                Launcher.isUseDefault = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        if(s!=null){
            List list = null;
            Log.d("WorkCOnfig", s);
            list = Utils.doJsonParser(s);
//            LauncherApplication.setdelApp(Utils.doDelappJsonParser(s));
            if(list.size()<=0){
                return null;
            }else{
                return list;
            }
        }
        return null;
    }
            
            

    
}
