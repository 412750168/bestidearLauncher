package net.bestidear.bestidearlauncher.workplace;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import net.bestidear.bestidearlauncher.model.CellInfo;
import net.bestidear.bestidearlauncher.utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ContactService {
    private static String Tag = "ContactService";
    
    private static String Workplace_Config_URL = "http://bestidear-upfile.stor.sinaapp.com/launcher/workplace.txt";
    private static String WebPath = "http://bestidear-upfile.stor.sinaapp.com/launcher";

    private Context context;
    /**
     * 从服务器上获取数据
     */
    public ContactService(Context context){
        this.context = context;
    }
    
    
    
    public boolean getConfig() throws Exception {
        String Parth = Workplace_Config_URL;
        URL url = new URL(Parth);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(3000);
        conn.setRequestMethod("GET");
        File file = new File(context.getCacheDir(), "workplace.txt");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream();
            String s = Utils.getStringFromInputStream(is);
            String olds = null;
            if(file.exists()){
                olds = Utils.getStringFromFile(file.getAbsolutePath());
            }
            Log.d(Tag, "newsnewsnewsnewsnewsnewsnewsnewsn::::::::::::::::::"+s);
            Log.d(Tag, "oldsoldsoldsoldsoldsoldsoldsolds:::::::::::::::::::"+olds);
//            contacts = xmlParser(is);
            if(!file.exists()||!s
                    .equals(olds)){
//                Log.d(Tag, "cleanCache()");
//                cleanCache();
                
                
                Log.d(Tag, "downLoadRes(is)");
                boolean downSuccess = downLoadRes(s);
                
                if(downSuccess){
                    Log.d(Tag, "saveFile");
                    FileWriter fw=new FileWriter(file);
                    BufferedWriter buffw=new BufferedWriter(fw);
                    PrintWriter pw=new PrintWriter(buffw);
                    pw.write(s);
                    
                    pw.close();
                    buffw.close();
                    fw.close();
                    
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
            
        } else {
            return false;
        }
    }
    
    private boolean downLoadRes(String s) {
        int count = 0 ;
        try {
            List<String> downLoadList = getDownLoadList(s);
            File cache = context.getCacheDir();
            for(String name:downLoadList){
                Log.d(Tag, "downLoadRes:name:"+name);
                Uri test = getImageURI(name, cache);
                Log.d(Tag, "downLoadRes:result:"+test);
                if(test == null){
                    break;
                }
                count++;
            }
            if(count<downLoadList.size()){
                return false;
            }else{
                return true;
            }
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        
    }

    // 图片的名字保存下来了
    private List<String> getDownLoadList(String s) throws Exception {
        List<String> contacts = new ArrayList<String>();
        List<CellInfo> list = Utils.doJsonParser(s);
        for (CellInfo cinfo:list) {
            contacts.add(cinfo.getBackground());
        }
        return contacts;
    }

    private void cleanCache(){
        File cache = context.getCacheDir();
        File[] files = cache.listFiles();
        for(File file :files){
            file.delete();
        }
//        cache.delete();
    }
    
    
    /*
     * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片
     * 这里的path是图片的地址
     */
    public Uri getImageURI(String name, File cache) throws Exception {
        String path = WebPath + "/" + name + ".png";
        Log.d(Tag, "getImageURI:"+path);
        File file = new File(cache, name);
        // 如果图片存在本地缓存目录，则不去服务器下载 
        if (file.exists()) {
            return Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
        } else {
            // 从网络上获取图片
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            if (conn.getResponseCode() == 200) {

                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
//                is.close();
//                fos.close();
                // 返回一个URI对象
                return Uri.fromFile(file);
            }
        }
        return null;
    }
}
