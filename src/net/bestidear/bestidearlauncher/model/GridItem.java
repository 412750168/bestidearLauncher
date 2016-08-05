package net.bestidear.bestidearlauncher.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.bestidear.bestidearlauncher.R;

public class GridItem extends RelativeLayout implements Checkable {
    private Context mContext;
    private boolean mChecked;
    private ImageView mImgView = null;
    private ImageView mSecletView = null;
    private TextView appname = null;

    public GridItem(Context context) {
        this(context, null, 0);
    }

    public GridItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item, this);
        mImgView = (ImageView) view.findViewById(R.id.img_view);
        mSecletView = (ImageView) view.findViewById(R.id.select);
        appname = (TextView) view.findViewById(R.id.appname);
    }

    @Override
    public void setChecked(boolean checked) {
        // TODO Auto-generated method stub
        mChecked = checked;
        setBackgroundDrawable(checked ? getResources().getDrawable(
                R.drawable.background) : null);
        mSecletView.setVisibility(checked ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean isChecked() {
        // TODO Auto-generated method stub
        return mChecked;
    }

    @Override
    public void toggle() {
        // TODO Auto-generated method stub
        setChecked(!mChecked);
    }

    public void setImgResId(Drawable db) {
        if (mImgView != null) {
            mImgView.setImageDrawable(db);
        }
    }
    public void setAppname(String name){
        if (appname != null) {
            appname.setText(name);
        }
    }

}
