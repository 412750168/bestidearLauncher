package net.bestidear.bestidearlauncher;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.IWindowManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import net.bestidear.bestidearlauncher.model.ApplicationInfo;
import net.bestidear.bestidearlauncher.model.GridItem;
import net.bestidear.bestidearlauncher.model.AddLayout;

import java.util.HashMap;
import java.util.Map;

public class ChoiceActivity extends Activity implements OnClickListener{

    private GridView mGridView;
    private GridAdapter mGridAdapter;
    private TextView mActionText;
    
    private Button btnAllselect;
    private Button btnUNselect;
    private Button btnOk;
    
    private String[] s;
    private boolean[] b;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice);

        IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
	    try{
	            mWindowManager.setAnimationScale(1, 0.0f);
	        } catch (RemoteException e) {
	        }
		
        
        ready();
        
        btnAllselect = (Button) findViewById(R.id.btnAllSelect);
        btnUNselect = (Button) findViewById(R.id.btnUnSelect);
        btnOk = (Button) findViewById(R.id.btnEnter);
        btnAllselect.setOnClickListener(this);
        btnUNselect.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        
        mActionText = (TextView) findViewById(R.id.action_text);
        mGridView = (GridView) findViewById(R.id.gridview1);
        mGridAdapter = new GridAdapter(this);
        mGridView.setAdapter(mGridAdapter);
        mGridView.requestFocus();
        mActionText.setText(formatString(checkedCount()));
        mGridView.setOnItemClickListener(new OnItemClickListener() {    
            @Override 
            public void onItemClick(AdapterView<?> arg0, View arg1,    
                            int position, long arg3) {    
                mGridAdapter.chiceState(position);
                mActionText.setText(formatString(checkedCount()));
            }    
        });    
    }

    private int checkedCount(){
        int mCount = 0;
        for(int i=0;i<b.length;i++){
            if(b[i]){
                mCount++;
            }
        }
        return mCount;
    }
    

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnAllSelect:
                allSelect();
                break;
            case R.id.btnUnSelect:
                unSelect();
                break;
            case R.id.btnEnter:
                Save();
                break;
        }
        
    }
    
    private void allSelect(){
        for(int i=0;i<b.length;i++){
            b[i] = true;
        }
        mGridAdapter.notifyDataSetChanged();
    }
    private void unSelect(){
        for(int i=0;i<b.length;i++){
            b[i] = false;
        }
        mGridAdapter.notifyDataSetChanged();
    }
    
    private void ready(){
        
        b = new boolean[AddLayout.appnames.size()];
        for(int i = 0 ; i<AddLayout.appstate.size();i++){
            if(AddLayout.appstate.get(i)){
                b[i]=true;
            }else{
                b[i]=false;
            }
        }
        
        for(int i = 0 ; i<AddLayout.appnames.size();i++ ){
            Log.d("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^", "ddddddddd");
            Log.d("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&", AddLayout.appnames.get(i).title+",,,"+b[i]);
            Log.d("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^", "dddddddd");
        }
    }
    
    private void Save(){
        String result = "";
        for(int i= 0;i < b.length;i++){
            if(b[i]){
                if(!result.equals("")){
                    result += ",";
                 }
             result += AddLayout.appnames.get(i).packagename;
             }
         }
        SharedPreferences.Editor editor = getSharedPreferences(Launcher.LAUNCHER_CUSTOM_FILE, Launcher.MODE_PRIVATE).edit();
        editor.putString("apps", result);
        editor.commit();
        
        this.finish();
    }
    

    private String formatString(int count) {
        return String.format(getString(R.string.selection), count);

    }

    private class GridAdapter extends BaseAdapter {

        private Context mContext;

        public GridAdapter(Context ctx) {
            mContext = ctx;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return AddLayout.appnames.size();
        }

        @Override
        public ApplicationInfo getItem(int position) {
            // TODO Auto-generated method stub
            return AddLayout.appnames.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            GridItem item;
            if (convertView == null) {
                item = new GridItem(mContext);
                item.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.FILL_PARENT));
            } else {
                item = (GridItem) convertView;
            }
            item.setImgResId(getItem(position).icon);
            item.setAppname(getItem(position).title);
//            item.setChecked(mSelectMap.get(position) == null ? false
//                    : mSelectMap.get(position));
            item.setChecked(b[position]);
            mActionText.setText(formatString(checkedCount()));
            return item;
        }
        public void chiceState(int post)
        {
            b[post]=b[post]==true?false:true;
            this.notifyDataSetChanged();
        }
    }

    

    

}



