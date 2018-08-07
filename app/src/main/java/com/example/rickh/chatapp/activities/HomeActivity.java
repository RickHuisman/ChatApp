package com.example.rickh.chatapp.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import com.example.rickh.chatapp.R;
import com.example.rickh.chatapp.adapters.TabSectionAdapter;

public class HomeActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(
                new TabSectionAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(pageChangerListener);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mFab = (FloatingActionButton) findViewById(R.id.quick_action_fab);
        mFab.setOnClickListener(quickActionClick);
    }

    View.OnClickListener quickActionClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (mViewPager.getCurrentItem()) {
                case 0:
                    startActivity(new Intent(getApplicationContext(), CreateChatActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(getApplicationContext(), AddFriendActivity.class));
                    break;
            }
        }
    };

    protected void animateFab(final int position) {
        mFab.clearAnimation();

        final int[] iconIntArray = {R.drawable.ic_message_white_24dp,R.drawable.ic_person_add_white_24dp};

        ScaleAnimation shrink = new ScaleAnimation(1f, 0.1f, 1f, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFab.setImageDrawable(getResources().getDrawable(iconIntArray[position], null));

                ScaleAnimation expand = new ScaleAnimation(0.1f, 1f, 0.1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(125);
                expand.setInterpolator(new AccelerateInterpolator());
                mFab.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mFab.startAnimation(shrink);
    }

    ViewPager.OnPageChangeListener pageChangerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            animateFab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
