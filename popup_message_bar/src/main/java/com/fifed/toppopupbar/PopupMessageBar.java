package com.fifed.toppopupbar;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.ColorInt;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Fedir on 22.10.2017.
 */

public class PopupMessageBar {
    private Context context;
    private static int contextHash;
    private WindowManager wm;
    private WindowManager.LayoutParams params;
    private FrameLayout container;
    private View bar;
    private TextView tvMessage;
    private long duration = 2000;
    private static List<View> queue = new LinkedList<>();
    private static boolean isShowing;
    private View targetView;

    public static boolean isShowing(){
        return isShowing;
    }


    public static PopupMessageBar init(Context context){
        return new PopupMessageBar(context);
    }

    public static PopupMessageBar init(View targetView){
        return new PopupMessageBar(targetView);
    }

    private PopupMessageBar(){}

    private PopupMessageBar(Context context) {
        if(!(context instanceof ContextThemeWrapper)){
            throw new RuntimeException("Must be Activity context only!");
        }
        this.context = context;
        if(contextHash != 0 && contextHash != context.hashCode()){
            queue.clear();
        }
        contextHash = context.hashCode();
        initBar();
    }

    private PopupMessageBar(View targetView){
        this.targetView = targetView;
        Context context = targetView.getContext();
        if(!(context instanceof ContextThemeWrapper)){
            throw new RuntimeException("Must be Activity context only!");
        }
        this.context = context;
        if(contextHash != 0 && contextHash != context.hashCode()){
            queue.clear();
        }
        contextHash = context.hashCode();
        initBar();
    }

    private void initBar(){
        params = new WindowManager.LayoutParams(
                MATCH_PARENT,
                WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP;
        setPosition();
        wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        container = new FrameLayout(context);
        container.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                isShowing = true;
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                isShowing = false;
            }
        });
        bar =  LayoutInflater.from(context).inflate(R.layout.bar_view, null);
        container.addView(bar, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        tvMessage = bar.findViewById(R.id.tvMessage);
    }

    private void setPosition(){
        if(targetView == null) {
            params.verticalMargin = (float) Utils.getStatusBarHeight(context) / context.getResources().getDisplayMetrics().heightPixels;
        } else {
            int[] location = new int[2];
            targetView.getLocationOnScreen(location);
            int bottom = location[1] + targetView.getHeight();
            params.verticalMargin = (float) bottom / context.getResources().getDisplayMetrics().heightPixels;
        }
    }

    public void show(){
           if(isShowing()){
               queue.add(container);
           } else {
               showBar(container);
           }
    }

    private void showBar(final View view){
        Animation appearAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in_anim);
        appearAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            try {
                wm.addView(view, params);
                bar.startAnimation(appearAnimation);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideBar(view);
                    }
                }, duration + appearAnimation.getDuration());
            } catch (BadTokenException e){
                //NOP
            }
    }

    private void hideBar(final View view){
        Animation disappearAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        disappearAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        disappearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    wm.removeViewImmediate(view);
                } catch (BadTokenException e){
                    //NOP
                }
                if(!queue.isEmpty()){
                    showBar(queue.get(0));
                    if(!queue.isEmpty()){
                        queue.remove(0);
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        bar.startAnimation(disappearAnimation);
    }

    public PopupMessageBar setDuration(long duration){
        this.duration = duration;
        return this;
    }

    public PopupMessageBar setTextSize(float size) {
        tvMessage.setTextSize(size);
        return this;
    }

    public PopupMessageBar setMessage(String message){
        tvMessage.setText(message);
        return this;
    }

    public PopupMessageBar setTextColor(@ColorInt int color){
        tvMessage.setTextColor(color);
        return this;
    }

    public PopupMessageBar setBackgroundColor(@ColorInt int color){
        bar.setBackgroundColor(color);
        return this;
    }
}
