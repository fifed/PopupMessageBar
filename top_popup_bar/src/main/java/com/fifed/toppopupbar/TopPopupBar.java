package com.fifed.toppopupbar;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.ColorInt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

public class TopPopupBar {
    private Context context;
    private static int contextHash;
    private WindowManager wm;
    private WindowManager.LayoutParams params;
    private FrameLayout container;
    private View bar;
    private TextView tvMessage;
    private long duration = 2000;
    private static List<View> queue = new LinkedList<>();
    private static boolean isAnimation;
    private static View currentView;

    public static boolean isShowing(){
        return currentView != null;
    }

    public TopPopupBar(Context context) {
        if(!(context instanceof Activity)){
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
        params.verticalMargin  = (float) Utils.getStatusBarHeight(context) / context.getResources().getDisplayMetrics().heightPixels;
        wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        container = new FrameLayout(context);
        container.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                currentView = view;
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                currentView = null;
            }
        });
        bar =  LayoutInflater.from(context).inflate(R.layout.bar_view, null);
        container.addView(bar, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        tvMessage = bar.findViewById(R.id.tvMessage);
    }

    public void show(){
           if(isShowing()){
               queue.add(container);
               if(!isAnimation){
                   hideBar(currentView);
               }
           } else {
               showBar(container);
           }
    }

    private void showBar(final View view){
        isAnimation = true;
        Animation appearAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in_anim);
        appearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimation = false;
                if(!queue.isEmpty()){
                    hideBar(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

            wm.addView(view, params);
            view.findViewById(R.id.rlBackground).startAnimation(appearAnimation);
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideBar(view);
                }
            }, duration + appearAnimation.getDuration());
    }

    private void hideBar(final View view){
        isAnimation = true;
        Animation disappearAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        disappearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                wm.removeViewImmediate(view);
                isAnimation = false;
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
        view.findViewById(R.id.rlBackground).startAnimation(disappearAnimation);
    }

    public TopPopupBar setDuration(long duration){
        this.duration = duration;
        return this;
    }

    public TopPopupBar setTextSize(float size) {
        tvMessage.setTextSize(size);
        return this;
    }

    public TopPopupBar setMessage(String message){
        tvMessage.setText(message);
        return this;
    }

    public TopPopupBar setTextColor(@ColorInt int color){
        tvMessage.setTextColor(color);
        return this;
    }

    public TopPopupBar setBackgroundColor(@ColorInt int color){
        bar.setBackgroundColor(color);
        return this;
    }
}
