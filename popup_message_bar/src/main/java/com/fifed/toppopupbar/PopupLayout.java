package com.fifed.toppopupbar;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Fedir on 25.10.2017.
 */

class PopupLayout extends FrameLayout {
    private  int maxWidth;

    public void setMaxWidth(float maxWidthDp) {
        this.maxWidth = Utils.DPtoPX(getContext(), maxWidthDp);
    }

    public void setMaxWidth(int maxWidthPx) {
        this.maxWidth = maxWidthPx;
    }

    public PopupLayout(@NonNull Context context) {
        super(context);
        initMaxWidth(context);
    }

    public PopupLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initMaxWidth(context);
    }

    public PopupLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMaxWidth(context);
    }

    private void initMaxWidth(Context context){
        maxWidth = Utils.DPtoPX(context ,500f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        if(maxWidth < measuredWidth) {
            int measureMode = MeasureSpec.getMode(widthMeasureSpec);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth, measureMode);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
