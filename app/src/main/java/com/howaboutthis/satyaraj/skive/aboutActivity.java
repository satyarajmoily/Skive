package com.howaboutthis.satyaraj.skive;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

public class aboutActivity extends NavigationDrawer {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert inflater != null;
        @SuppressLint("InflateParams") View contentView = inflater.inflate(R.layout.about, null, false);
        drawer.addView(contentView, 0);
       final TextView developed = findViewById(R.id.developed);
       final TextView satyaraj = findViewById(R.id.satyaraj);
        final TextView by = findViewById(R.id.textView2);

        developed.startAnimation(AnimationUtils.loadAnimation(aboutActivity.this,R.anim.textview_left));
        satyaraj.startAnimation(AnimationUtils.loadAnimation(aboutActivity.this,R.anim.textview_right));

        Animation animation = new ScaleAnimation(1000,0,1000,0);
        animation.setDuration(500);
        by.startAnimation(animation);


        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {


            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation anin = new RotateAnimation(-10,10,50,0);
                anin.setRepeatMode(10);
                anin.setDuration(100);

                developed.startAnimation(anin);
                satyaraj.startAnimation(anin);


            }
        });



    }
}
