package my.example.bobatap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.Constants;

public class MainActivity extends AppCompatActivity {

    ImageView bobatap_logo;
    TextView ritmos;
    TextView frm;
    FirebaseAuth Fauth;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        if (isOnline()) {

            load();
        } else {
            try {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error")
                        .setMessage("Internet not available, Cross check your internet connectivity")
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        load();
                    }
                }).show();
            } catch (Exception e) {
                Log.d(Constants.TAG, "Show Dialog: " + e.getMessage());
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            return false;
        }
        return true;
    }

    public void load(){
        bobatap_logo = (ImageView)findViewById(R.id.bobatap_logo);
        ritmos = (TextView)findViewById(R.id.ritmos);
        frm = (TextView)findViewById(R.id.from);

        bobatap_logo.animate().alpha(0f).setDuration(0);
        ritmos.animate().alpha(0f).setDuration(0);

        bobatap_logo.animate().alpha(1f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ritmos.animate().alpha(1f).setDuration(800);

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Fauth = FirebaseAuth.getInstance();
                if (Fauth.getCurrentUser() != null) {
                    if (Fauth.getCurrentUser().isEmailVerified()) {
                        Intent n = new Intent(MainActivity.this, Home.class);
                        startActivity(n);
                        finish();
                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Check whether you have verified your Email, Otherwise please verify");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                Intent intent = new Intent(MainActivity.this, Login.class);
                                startActivity(intent);
                                finish();

                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                        Fauth.signOut();
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();

                }

            }
        },3000);
    }
}