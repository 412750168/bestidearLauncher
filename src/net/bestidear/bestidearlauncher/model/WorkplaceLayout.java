package net.bestidear.bestidearlauncher.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.bestidear.bestidearlauncher.Launcher;
import net.bestidear.bestidearlauncher.R;
import net.bestidear.bestidearlauncher.utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorkplaceLayout extends RelativeLayout {
    private final String Tag = "workplaceLayout";
    
    private Context context;
    
    private CellInfo cellinfo;
    
    private int cellWidth = (int) getResources().getDimension(R.dimen.cellwidth);
    private int cellHeight = (int) getResources().getDimension(R.dimen.cellheight);
    private int cellSpace = (int) getResources().getDimension(R.dimen.cellspace);
    private int cell_OFFSIZE = (int) getResources().getDimension(R.dimen.celloffsize);
    private int worklayouttoLeft = (int) getResources().getDimension(R.dimen.worklayouttoLeft);
    private int worklayouttoTop = (int) getResources().getDimension(R.dimen.worklayouttoTop);
    
    private ArrayList<ApplicationInfo> Applications;
    private ApplicationInfo applicationInfo;
    
    private int Col1Left = 0;
    private int Col2Left = 0;
    private int Col1OrderX = 0;
    private int Col2OrderX = 0;
    private int Row1Top = 0;
    private int Row2Top = 0;
    
    private int AddTop = 0;
    private int BlockCount = 0;
    
    private HorizontalScrollView hsw;
    
    public WorkplaceLayout(Context context) {
        this(context , null);
    }

    public WorkplaceLayout(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public WorkplaceLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
//        View.inflate(context, R.layout.workplacelayoutview, this);
    }
    /*
     * 应用列表填充代码段
     */
    
   
    
    public void makeAllAppCelllayout(ArrayList<ApplicationInfo> mApplications){
        Log.d(Tag, "mApplications:"+mApplications.size());
        this.Applications = mApplications;
        int ColNum = Applications.size()/2+Applications.size()%2;
        int X,Y,_newcellWidth,_newcellHeight;
        _newcellWidth = cellWidth;
        _newcellHeight = cellHeight;
        for(int i=0;i<Applications.size();i++){
            applicationInfo = Applications.get(i);
            if(i<ColNum){
                X = i;
                Y = 0;
            }else{
                X = i - ColNum;
                Y = 1;
            }
            LayoutParams layoutParams = new LayoutParams(_newcellWidth, _newcellHeight);
            layoutParams.setMargins(worklayouttoLeft+X*cellWidth+cellSpace*X, worklayouttoTop+Y*cellHeight+cellSpace*Y, 0, 0);
            
            AllappcellLayout allappcelllayout = new AllappcellLayout(context);
            allappcelllayout.FillInfo(X,Y,false , i,this,applicationInfo,_newcellWidth,_newcellHeight);
            
            this.addView(allappcelllayout, i , layoutParams);
        }
    }
    /*
     * 应用列表焦点块增加代码段
     */
    private void AllAppFocusfill(int index){
//      this.removeViewAt(index);
      
        applicationInfo = Applications.get(index);
      //添加新添加块的大小和位置信息
      int _newcellWidth = cell_OFFSIZE+cellWidth;
      int _newcellHeight = cell_OFFSIZE+cellWidth;
      int X,Y;
      int ColNum = Applications.size()/2+Applications.size()%2;
      if(index<ColNum){
          X = index;
          Y = 0;
      }else{
          X = index - ColNum;
          Y = 1;
      }
      LayoutParams layoutParams = new LayoutParams(_newcellWidth, _newcellHeight);
      layoutParams.setMargins(worklayouttoLeft+X*cellWidth+cellSpace*X-cell_OFFSIZE/2, worklayouttoTop+Y*cellHeight+cellSpace*Y-cell_OFFSIZE/2, 0, 0);
      //新建添加块并添加相关信息
      AllappcellLayout allappcelllayout = new AllappcellLayout(context);
      allappcelllayout.FillInfo(X,Y,true,index,this,applicationInfo,_newcellWidth,_newcellHeight);
      
      allappcelllayout.setAnimation(AnimationUtils.loadAnimation(context, R.anim.pull_right_in));
      this.addView(allappcelllayout, Applications.size() , layoutParams);
  }
    /*
     * 应用列表失去焦点块删除代码段
     */
    private void AllAppNotFocusfill(){
//      this.getChildAt(listCellinfo.size()).setAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_out));
      if(this.getChildAt(Applications.size())!=null){
          this.removeViewAt(Applications.size());
      }
      
  }
    
    private boolean isMake = false;
    /*
     * 主界面表填充代码段
     */
    public void makeCelllayout() {
        isMake = true;
        BlockCount=0;
        int Left = 0;
        int Top = 0;
        int _newcellWidth = 0;
        int _newcellHeight = 0;
        Top = worklayouttoTop;
        Row1Top = Top;
        Row2Top = Top + cellHeight + cellSpace;
        for(int i = 0; i <2; i++){
            int j = 0;
            Left = worklayouttoLeft;
            while(true){
                int index = searchCellIndex(i,j);
                if(index != -1){
                    cellinfo = Launcher.listCellinfo.get(index);
                    if(!cellinfo.isNotDisplay()){
                        if(cellinfo.isInstall()){
                            
                            Launcher.listCellinfo.get(index).setLeft(Left);
                            Launcher.listCellinfo.get(index).setTop(Top);
                            
                            _newcellWidth = cellinfo.getSpanX()*cellWidth+(cellinfo.getSpanX()-1)*cellSpace;
                            _newcellHeight = cellinfo.getSpanY()*cellHeight+(cellinfo.getSpanY()-1)*cellSpace;
    //                        if(i==1){
    //                            _newcellHeight = _newcellHeight+cellHeight/2;
    //                        }
                            LayoutParams layoutParams = new LayoutParams(_newcellWidth, _newcellHeight);
                            layoutParams.setMargins(Left, Top, 0, 0);
                            
                            CellLayout _celllayout1 = new CellLayout(context);
                            _celllayout1.FillInfo(false ,this,cellinfo,_newcellWidth,_newcellHeight);
                            
                            BlockCount++;
                            this.addView(_celllayout1 , layoutParams);
                            
//                            Log.d(Tag, "ljlljlXXXXXXXXX"+Launcher.FocusX+"YYYYYY"+Launcher.FocusY);
//                            Log.d(Tag, "ljlljlFocusX"+i+"FocusY"+j);
                          //读取上一次焦点坐标
                            if(i==Launcher.FocusX&&j==Launcher.FocusY){
//                                Log.d(Tag, "ljlljlin~~~~~~~~~~~~");
                                _celllayout1.requestFocus();
                            }
                            
                            Left = Left+_newcellWidth+cellSpace;
                            
                            
                        }
                    }
                }else{
                    break;
                }
                j++;
                
            }
            Top = Top + cellHeight + cellSpace;
            AddTop = Top + 30;
            if(i == 0){
                Col1Left = Left;
                Col1OrderX = j;
            }else{
                Col2Left = Left;
                Col2OrderX = j;
            }
        }
        makeCustomLayout();
                
    }
    

    private void makeCustomLayout() {
        SharedPreferences sp = context.getSharedPreferences(Launcher.LAUNCHER_CUSTOM_FILE, Launcher.MODE_PRIVATE);
        String temp = sp.getString("apps", "");

        if(temp=="")
            return;
        
        String apks[] = temp.trim().split(",");
        
        
        for(String packagename:apks){
            boolean isbreak = false;
            for(CellInfo info:Launcher.listCellinfo){
                if(info.getPackageName().equals(packagename)){
                    isbreak = true;
                    break;
                }
            }
            
            if(isbreak) continue;
            
            int OrderY = (Col1Left<=Col2Left)?0:1;
            int OrderX = (OrderY==0)?Col1OrderX:Col2OrderX;
            
            try {
                PackageManager packageManager = context.getApplicationContext().getPackageManager(); 
                android.content.pm.ApplicationInfo appName = context.getPackageManager().getApplicationInfo(packagename, 0);
                String applicationName = (String) packageManager.getApplicationLabel(appName);
                
                int isUserPic = AllappcellLayout.isUsePic(packagename);
                Drawable appicon;
                if(isUserPic == -1){
                    appicon = packageManager.getApplicationIcon(appName);
                }else{
                    appicon = getResources().getDrawable(AllappcellLayout.appPic[isUserPic]);
                }

//                Log.d("fuck++++++++++++++++++++++++", applicationName);
            
            CellInfo cell = new CellInfo(applicationName, packagename, "", OrderX, OrderY, 1, 1, 0, "", "", appicon);
            
            int _newcellWidth = 0;
            int _newcellHeight = 0;
            
            int Left = (OrderY==0)?Col1Left:Col2Left;
            int Top = (OrderY==0)?Row1Top:Row2Top;
//            Log.d("FFFFFFFFFFUUUUUUUUUUUCCCCCCCCCKKKKKKKKKK","packagename"+packagename);
//                Log.d("FFFFFFFFFFUUUUUUUUUUUCCCCCCCCCKKKKKKKKKK","Left"+Left);
//                Log.d("FFFFFFFFFFUUUUUUUUUUUCCCCCCCCCKKKKKKKKKK","Top"+Top);
            cell.setLeft(Left);
            cell.setTop(Top);
            cell.setInstall(true);
                
                _newcellWidth = cell.getSpanX()*cellWidth+(cell.getSpanX()-1)*cellSpace;
                _newcellHeight = cell.getSpanY()*cellHeight+(cell.getSpanY()-1)*cellSpace;
//                if(i==1){
//                    _newcellHeight = _newcellHeight+cellHeight/2;
//                }
                LayoutParams layoutParams = new LayoutParams(_newcellWidth, _newcellHeight);
                layoutParams.setMargins(Left, Top, 0, 0);
                
                CellLayout _celllayout1 = new CellLayout(context);
                _celllayout1.FillInfo(false ,this,cell,_newcellWidth,_newcellHeight);
                
                BlockCount++;
                this.addView(_celllayout1 , layoutParams);
                
              //读取上一次焦点坐标
                if(OrderY==Launcher.FocusX&&OrderX==Launcher.FocusY){
                    Log.d(Tag, "ljlljlin~~~~~~~~~~~~");
                    _celllayout1.requestFocus();
                }
                
                if(OrderY == 0){
                    Col1Left = Left+_newcellWidth+cellSpace;
                    Col1OrderX++;
                }else{
                    Col2Left = Left+_newcellWidth+cellSpace;
                    Col2OrderX++;
                }
                
                Launcher.listCellinfo.add(cell);
                
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
               
    }

    /*
     * 查找对应下标是否有预置块
     * 
     */
    private int searchCellIndex(int i, int j) {
//        Log.d(Tag, "ffffffffffff"+i+",,,,"+j);
        int k = 0;
        for(CellInfo cinfo:Launcher.listCellinfo){
            if(cinfo.getOrderY()==i&&cinfo.getOrderX()==j){
                return k; 
            }
            k++;
        }
        return -1;
    }
    /*
     * 主界面焦点块增加代码段
     */
    private void Focusfill(int OrderX,int Y){
//        this.removeViewAt(index);
        int index = searchCellIndex(Y,OrderX);
        cellinfo = Launcher.listCellinfo.get(index);
        //添加新添加块的大小和位置信息
        int _newcellWidth = cell_OFFSIZE+cellinfo.getSpanX()*cellWidth+(cellinfo.getSpanX()-1)*cellSpace;
        int _newcellHeight = cell_OFFSIZE+cellinfo.getSpanY()*cellHeight+(cellinfo.getSpanY()-1)*cellSpace;
        LayoutParams layoutParams = new LayoutParams(_newcellWidth, _newcellHeight);
        layoutParams.setMargins(cellinfo.getLeft()-cell_OFFSIZE/2, cellinfo.getTop()-cell_OFFSIZE/2, 0, 0);
        //新建添加块并添加相关信息
        CellLayout _celllayout1 = new CellLayout(context);
        _celllayout1.FillInfo(true,this,cellinfo,_newcellWidth,_newcellHeight);
        
        _celllayout1.setAnimation(AnimationUtils.loadAnimation(context, R.anim.pull_right_in));
        this.addView(_celllayout1,BlockCount, layoutParams);
        
        
        if(hsw!= null){
            //保持焦点块在屏幕内
//                  Log.d(Tag, "SSSSSSSSSSSSSSSSSSSSSSS....scorll to _++++"+cellinfo.getLeft());
                  if(cellinfo.getLeft()>=100){
                      hsw.scrollTo(cellinfo.getLeft()-290, 0);
                  }else{
                      hsw.scrollTo(cellinfo.getLeft()-48, 0);
                  }
              }
    }
    /*
     * 主界面失去焦点块删除代码段
     */
    private void NotFocusfill(){
//        this.getChildAt(listCellinfo.size()).setAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_out));
        if(this.getChildAt(BlockCount)!=null){
            this.removeViewAt(BlockCount);
        }
    }
    
    
    public final int FOCUS = 10;
    public final int NotFOCUS = 11;
    public final int AllappFOCUS = 12;
    public final int AllappNotFOCUS = 13;
    
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case FOCUS:
//                    Log.d(Tag, "Get FOCUS,arg:"+msg.arg1);
                    Focusfill(msg.arg1,msg.arg2);break;
                case NotFOCUS:
//                    Log.d(Tag, "NotFOCUS,arg:"+msg.arg1);
                    NotFocusfill();break;
                case AllappFOCUS:
//                  Log.d(Tag, "Get FOCUS,arg:"+msg.arg1);
                    AllAppFocusfill(msg.arg1);break;
                case AllappNotFOCUS:
//                  Log.d(Tag, "Get FOCUS,arg:"+msg.arg1);
                    AllAppNotFocusfill();break;
            }
        }
    };
    
    public Handler getHandler(){
        return myHandler;
    }
   
    public void setHSW(HorizontalScrollView hsw){
        this.hsw = hsw;
    }
}
