package com.example.crud_firebase;

import com.google.firebase.database.FirebaseDatabase;

public class MyFirbaseAppPersistencia extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
