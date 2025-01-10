package com.example.bobatap;
import static com.example.bobatap.Constants.PREF_DIRECTORY;
import static com.example.bobatap.Constants.PREF_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Search.OnDataPass {
    private Context context;
    public static String USER_ID;
    public static boolean IS_SEARCHED_USER = false;
    ViewPagerAdapter pagerAdapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);


        init();
        context = this;
        addTabs();

    }

    private void init() {

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

    }

    private void addTabs() {

//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home));
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_search));
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_add));
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_heart));

        List<Integer> drawableResList = new ArrayList<>();
        drawableResList.add(R.drawable.ic_home);
        drawableResList.add(R.drawable.baseline_search_);
        drawableResList.add(R.drawable.baseline_add_);
        drawableResList.add(R.drawable.baseline_notifications);
        drawableResList.add(R.drawable.baseline_person_outline_24);

        for (int i = 0; i < 4; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(drawableResList.get(i)));
        }


        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String directory = preferences.getString(PREF_DIRECTORY, "");

        Bitmap bitmap = loadProfileImage(directory);
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);

        tabLayout.addTab(tabLayout.newTab().setIcon(drawable));


        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_fill);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Drawable selectedIcon;

                int selectedColor = ContextCompat.getColor(context, R.color.black);
                tab.getIcon().setColorFilter(selectedColor, PorterDuff.Mode.SRC_IN);// Change to your desired color
                switch (tab.getPosition()) {

                    case 0:
//                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_fill);
                        tab.setIcon(R.drawable.ic_home_fill);
                        IS_SEARCHED_USER = false;
                        break;

                    case 1:
                        tab.setIcon(R.drawable.baseline_search_);
                        IS_SEARCHED_USER = false;
                        break;

                    case 2:
                        tab.setIcon(R.drawable.baseline_add_circle);
                        IS_SEARCHED_USER = false;
                        break;

                    case 3:
                        tab.setIcon(R.drawable.baseline_notificationss);
                        IS_SEARCHED_USER = false;
                        break;
                    case 4:
                        tab.setIcon(R.drawable.baseline_person);
                        IS_SEARCHED_USER = false;
                        break;


                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case 0:
                        tab.setIcon(R.drawable.ic_home);
                        break;

                    case 1:
                        tab.setIcon(R.drawable.baseline_search_);
                        break;

                    case 2:
                        tab.setIcon(R.drawable.baseline_add_);
                        break;

                    case 3:
                        tab.setIcon(R.drawable.baseline_notifications);
                        break;
                    case 4:
                        tab.setIcon(R.drawable.baseline_person_outline_24);
                        break;


                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case 0:
                        tab.setIcon(R.drawable.ic_home_fill);
                        IS_SEARCHED_USER = false;
                        break;

                    case 1:
                        tab.setIcon(R.drawable.baseline_search_);
                        IS_SEARCHED_USER = false;
                        break;

                    case 2:
                        tab.setIcon(R.drawable.baseline_add_circle);
                        IS_SEARCHED_USER = false;
                        break;

                    case 3:

                        tab.setIcon(R.drawable.baseline_notificationss);
                        IS_SEARCHED_USER = false;
                        break;
                    case 4:

                        tab.setIcon(R.drawable.baseline_person);
                        IS_SEARCHED_USER = false;
                        break;

                }

            }
        });

    }

    private Bitmap loadProfileImage(String directory) {

        try {
            File file = new File(directory, "profile.png");

            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void onChange(String uid) {
        USER_ID = uid;
        IS_SEARCHED_USER = true;
        viewPager.setCurrentItem(4);

    }

    @Override
    public void onBackPressed() {

        if (viewPager.getCurrentItem() == 4) {
            viewPager.setCurrentItem(0);
            IS_SEARCHED_USER = false;
        } else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus(true);
    }

    @Override
    protected void onPause() {
        updateStatus(false);
        super.onPause();
    }

    void updateStatus(boolean status) {

        Map<String, Object> map = new HashMap<>();
        map.put("online", status);

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(user.getUid())
                .update(map);
    }


}