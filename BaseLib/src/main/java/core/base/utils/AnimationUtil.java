package core.base.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import org.greenrobot.eventbus.EventBus;

import core.base.photopicker.beans.PopupWindowStateBean;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AnimationUtil {

    //判断动画显示后，是否关闭的标志位
    private static boolean isShowView;

    //动画持续时间
    public final static int ANIMATION_IN_TIME=500;
    public final static int ANIMATION_OUT_TIME=500;

    public static Animation createInAnimation(Context context, int fromYDelta){
        AnimationSet set=new AnimationSet(context,null);
        set.setFillAfter(true);

        TranslateAnimation animation=new TranslateAnimation(0,0,fromYDelta,0);
        animation.setDuration(ANIMATION_IN_TIME);
        set.addAnimation(animation);

        AlphaAnimation alphaAnimation=new AlphaAnimation(0,1);
        alphaAnimation.setDuration(ANIMATION_IN_TIME);
        set.addAnimation(alphaAnimation);


        return set;
    }

    public static Animation createOutAnimation(Context context,int toYDelta){
        AnimationSet set=new AnimationSet(context,null);
        set.setFillAfter(true);

        TranslateAnimation animation=new TranslateAnimation(0,0,0,toYDelta);
        animation.setDuration(ANIMATION_OUT_TIME);
        set.addAnimation(animation);

        AlphaAnimation alphaAnimation=new AlphaAnimation(1,0);
        alphaAnimation.setDuration(ANIMATION_OUT_TIME);
        set.addAnimation(alphaAnimation);


        return set;
    }

    public static Animation createInAnimation_Alpha(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(ANIMATION_OUT_TIME);
        return alphaAnimation;
    }


    public static Animation createOutAnimation_Alpha(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(ANIMATION_OUT_TIME);
        return alphaAnimation;
    }

    public static void createTopOutAnimation(Context context, final View view, int toYDelta) {
        ObjectAnimator hideListAniamtor = ObjectAnimator.ofFloat(view, "translationY", 0, toYDelta);
        hideListAniamtor.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(GONE);
                isShowView = false;
            }
        });
        hideListAniamtor.setDuration(context.getResources().getInteger(android.R.integer.config_shortAnimTime));
        hideListAniamtor.start();
        EventBus.getDefault().post(new PopupWindowStateBean(false));
    }
    public static void createTopInAnimation(Context context, View view, int fromYDelta) {
        ObjectAnimator popupListAnimator = ObjectAnimator.ofFloat(view, "translationY", fromYDelta, 0);
        popupListAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isShowView = true;
            }
        });
        view.setVisibility(VISIBLE);
        popupListAnimator.setDuration(context.getResources().getInteger(android.R.integer.config_shortAnimTime));
        popupListAnimator.start();
        EventBus.getDefault().post(new PopupWindowStateBean(true));
    }
}