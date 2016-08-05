package net.bestidear.bestidearlauncher.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import net.bestidear.bestidearlauncher.Launcher;
import net.bestidear.bestidearlauncher.R;
import net.bestidear.bestidearlauncher.allAppActivity;
import net.bestidear.bestidearlauncher.utils.ImageUtil;
import net.bestidear.bestidearlauncher.utils.StringUtil;
import net.bestidear.bestidearlauncher.utils.Utils;
import net.bestidear.weather.SetCityActivity;

import java.util.Random;

public class CellLayout extends FrameLayout{
    private final String Tag = "cellLayout";
    private Context context;
    private FrameLayout framlayout;
    
    private ImageView icon;
    private TextView title;
    private ImageView cellimage;
    
    
    private boolean isFocus;
    
    public CellLayout(Context context) {
        this(context , null);
    }

    public CellLayout(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public CellLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        framlayout = (FrameLayout) View.inflate(context, R.layout.celllayoutview, this);
    
        icon = (ImageView) findViewById(R.id.icon);
        title = (TextView) findViewById(R.id.cellname);
        cellimage = (ImageView) findViewById(R.id.cellimage);
        
//        this.setFocusable(true);
    }
    
    private static int app_normal[][] = {{R.drawable.app_orange_normal,R.drawable.app_green_normal,R.drawable.app_blue_normal}, 
        {R.drawable.app_blue_normal,R.drawable.app_red_normal,R.drawable.app_yellow_normal}};
    
    public void FillInfo(boolean Focusblock ,final WorkplaceLayout wplo , final CellInfo cellinfo,int _newcellWidth,int _newcellHeight){
        this.setFocusable(true);
        isFocus = Focusblock;
//        Log.d(Tag,"index = " + index);
//        Log.d(Tag,"width = " + _newcellWidth);
//        Log.d(Tag,"height = " + _newcellHeight);
        cellimage.setVisibility(View.VISIBLE);
        if(!cellinfo.getBackground().equals("")){
            title.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);

        }else{
            if(cellinfo.getType()==2){
                title.setText(getResources().getString(R.string.allapp));
                icon.setImageResource(R.drawable.appicon);
            }else if(cellinfo.getType()==4){
                title.setText(getResources().getString(R.string.setting));
                icon.setImageResource(R.drawable.seticon);
            }else{
                title.setText(cellinfo.getCellName());
                icon.setImageDrawable(cellinfo.getAppicon());
            }
        }
        //如果getBackground()头为"#"，即为纯色
        if(cellinfo.getBackground().startsWith("#")){
            this.setBackgroundColor(Color.parseColor(cellinfo.getBackground()));
        }else if(cellinfo.getBackground().equals("")){
            int indexX = cellinfo.getOrderX()%app_normal[0].length;
            int indexY = cellinfo.getOrderY();
            int background_id = app_normal[indexY][indexX];
            if(!isFocus){
                cellimage.setBackgroundResource(background_id);
            }else{
              Drawable drawable = this.getResources().getDrawable(background_id);
              Drawable drawableWithShadow = ImageUtil.drawLayoutDropShadow(context.getResources(),drawable);
              this.setBackgroundDrawable(drawableWithShadow);
//                cellimage.setBackgroundResource(background_id);
//                this.setBackgroundResource(R.drawable.shape);
//                this.setPadding(10, 10, 10, 10);
            }
        }else{
            String background_name = cellinfo.getBackground();
//            if(isFocus){
//                background_name = cellinfo.getFocusbackground();
//            }else{
//                background_name = cellinfo.getBackground();
//            }
            
            if(Launcher.isUseDefault){
                Log.d(Tag, "useDefault");

                int background_id = getResources().getIdentifier(background_name,"drawable","net.bestidear.bestidearlauncher");
    //            Log.d(Tag, "background_id:"+background_id);
                if(background_id != 0){
                    if(!isFocus){
//                        if(cellinfo.getY() == 1){
//                            Drawable drawable = this.getResources().getDrawable(background_id);
//                            Bitmap bm = ImageUtil.drawableToBitmap(drawable);
//                            Bitmap drawableWithShadow = ImageUtil.createReflectionImageWithOrigin(bm);
//                            cellimage.setImageBitmap(drawableWithShadow);
//                        }else{
                            cellimage.setBackgroundResource(background_id);
//                        }
                    }else{
                        Drawable drawable = this.getResources().getDrawable(background_id);
                        Drawable drawableWithShadow = ImageUtil.drawLayoutDropShadow(context.getResources(),drawable);
                        cellimage.setBackgroundDrawable(drawableWithShadow);
//                        cellimage.setBackgroundResource(background_id);
//                        this.setBackgroundResource(R.drawable.shape);
//                        this.setPadding(30, 30, 30, 30);
                            
                    }
                }
            }else{
                Log.d(Tag, "NotuseDefault");
//                BitmapFactory bf = new BitmapFactory();
//                Bitmap bmp = bf.decodeFile(context.getCacheDir()+"/"+background_name);
                BitmapDrawable bd = new BitmapDrawable(context.getCacheDir()+"/"+background_name);
                if(!isFocus){
//                    if(cellinfo.getY() == 1){
//                        Bitmap bm = ImageUtil.drawableToBitmap(bd);
//                        Bitmap drawableWithShadow = ImageUtil.createReflectionImageWithOrigin(bm);
//                        cellimage.setImageBitmap(drawableWithShadow);
//                    }else{
                        cellimage.setBackgroundDrawable(bd);
//                    }
                }else{
                    Drawable drawableWithShadow = ImageUtil.drawLayoutDropShadow(context.getResources(),bd);
                    cellimage.setBackgroundDrawable(drawableWithShadow);
//                    cellimage.setBackgroundDrawable(bd);
//                    this.setBackgroundResource(R.drawable.shape);
//                    this.setPadding(30, 30, 30, 30);
                }
            }
        }
        
        this.setOnClickListener(new OnClickListener() {
        
            @Override
            public void onClick(View v) {
                if(cellinfo.getType() == 2){
//                    Log.d(Tag,"do Type = " + cellinfo.getType());
                    Intent it = new Intent(context, allAppActivity.class);
                    context.startActivity(it);
                }else if(cellinfo.getType() == 3){
//                  Log.d(Tag,"do Type = " + cellinfo.getType());
                  Intent it = new Intent(context, SetCityActivity.class);
                  context.startActivity(it);
                }else if(cellinfo.getType() == 4){
//                  Log.d(Tag,"do Type = " + cellinfo.getType());
                  Intent it = new Intent(android.provider.Settings.ACTION_SETTINGS);
                  context.startActivity(it);
                }else if(cellinfo.getType() == 0){
//                    Log.d(Tag,"do Type = " + cellinfo.getType());
                    Intent it = new Intent();
                    if(!cellinfo.getClassName().equals("")){
                        it.setClassName(cellinfo.getPackageName(), cellinfo.getClassName());
                        it.addCategory(Intent.CATEGORY_LAUNCHER);
                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
                    }else{
                        it = context.getPackageManager().getLaunchIntentForPackage(cellinfo.getPackageName());
                    }

                    context.startActivity(it);
                }
//                Log.d(Tag,"onClick" + v.getLeft());
            }
        });
        
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Log.d(Tag, "DSFKHKSDF"+cellinfo.getOrderX()+"........."+cellinfo.getOrderY());
                    Message msg = Message.obtain(wplo.getHandler(), wplo.FOCUS);
                    msg.arg1 = cellinfo.getOrderX();
                    msg.arg2 = cellinfo.getOrderY();
                    Log.d(Tag, "ljlljlonFocusChangeX"+msg.arg1);
                    Log.d(Tag, "ljlljlonFocusChangeY"+msg.arg2);
                    
                    if(msg.arg1!=Launcher.FocusY||msg.arg2!=Launcher.FocusX){
                        Launcher.goLive = false;
                    }
                    
                    //===========================
                    Launcher.FocusY = msg.arg1;
                    Launcher.FocusX = msg.arg2;
                  //===========================
                    
                    
                    msg.sendToTarget();
                }else{
                    Message msg = Message.obtain(wplo.getHandler(), wplo.NotFOCUS);
                    msg.sendToTarget();
                }
            }
        });
    }
    
    public void requestfouces(){
        this.requestFocus();
    }
    
    
}
