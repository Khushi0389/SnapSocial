package my.example.bobatap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import my.example.bobatap.Like.LikeFragment;
import my.example.bobatap.Post.PostActivity;
import my.example.bobatap.Profile.Account_Settings;
import my.example.bobatap.Profile.EditProfile;
import my.example.bobatap.Profile.ProfileFragment;
import my.example.bobatap.Search.SearchFragment;
import my.example.bobatap.home.HomeFragment;

public class Home extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,NavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.insta_bottom_navigation);

        navView = findViewById(R.id.nav_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        navView.setNavigationItemSelectedListener(this);
        String name = getIntent().getStringExtra("PAGE");
        if (name != null){
            loadfragment(new HomeFragment());

        }else{
            loadfragment(new HomeFragment());

        }

    }
    private boolean loadfragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }

        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.Home:
                fragment = new HomeFragment();
                break;

            case R.id.search:
                fragment = new SearchFragment();
                break;

            case R.id.post:
                fragment = null;
                startActivity(new Intent(Home.this, PostActivity.class));
                break;
            case R.id.likes:
                fragment = new LikeFragment();
                break;
            case R.id.profile:
                fragment = new ProfileFragment();
                break;
            case R.id.setting:
                Log.d("Home", "Setting item clicked");
                startActivity(new Intent(Home.this, Account_Settings.class));
                break;
            case R.id.editprf:
                Log.d("Home", "Setting item clicked");
                startActivity(new Intent(Home.this, EditProfile.class));
                break;
            case R.id.aboutapps:
                Log.d("Home", "Setting item clicked");
                startActivity(new Intent(Home.this, Aboutapp.class));
                break;
        }
        return loadfragment(fragment);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


}