package com.wasor;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

public class PersistenceApplication extends Application {
    private static PersistenceApplication instance;
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        instance = this;
        mContext = getApplicationContext();
    }

    public static PersistenceApplication getInstance() {
        if (instance == null) {
            instance = new PersistenceApplication();
        }
        return instance;
    }

    public static Context getContext() {
        return mContext;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
