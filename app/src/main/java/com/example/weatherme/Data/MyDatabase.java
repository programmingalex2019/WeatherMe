package com.example.weatherme.Data;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.weatherme.Models.CityModel;

import java.util.concurrent.Executors;

//need to specify entities that will be in database -> and versioning
@Database(entities = {CityModel.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {

    //interface of communication backend-front-end
    public abstract myDAO myDAO();

    //one time instance
    private static volatile MyDatabase INSTANCE;

    //initialize database on a different thread to avoid slow processes
    public static MyDatabase getDatabase(final Context context){
        if(INSTANCE == null){ //make sure one time instance
            synchronized (MyDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class, "assignment_database")
                            .addCallback(initCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    //create database method
    private static final RoomDatabase.Callback initCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadExecutor().execute(() -> {
                final myDAO mydao = INSTANCE.myDAO();
            });
        }
    };

}
