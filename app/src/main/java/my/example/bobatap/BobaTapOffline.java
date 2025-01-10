package my.example.bobatap;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class BobaTapOffline extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
