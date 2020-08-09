package com.wasor;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class PersistenceApplication extends Application {
    public static FirebaseDatabase database;
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
