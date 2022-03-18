package com.raphjjodev.marketday;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = Market.class, version = 1)
public abstract class MarketDatabase extends RoomDatabase {

    private static MarketDatabase instance;

    public abstract MarketDAO marketDAO();

    public static synchronized MarketDatabase getInstance(Context context) {

        if (instance == null) {

            instance = Room.databaseBuilder(context.getApplicationContext(), MarketDatabase.class,
                    "market_table")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            new PopulateDBAsyncTask(instance).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void> {

        private MarketDAO marketDAO;

        private PopulateDBAsyncTask(MarketDatabase marketDatabase) {

            this.marketDAO = marketDatabase.marketDAO();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            //This populates the database when it is first created with the following markets
            marketDAO.insert(new Market("Ijero Market", "Ijero, Ekiti",
                    "8/2/2022", true, false, 9));
            marketDAO.insert(new Market("Igbamitoro Market", "Ijero, Ekiti",
                    "12/2/2022", true, false, 9));
            marketDAO.insert(new Market("Ayetoro Market", "Ido, Ekiti",
                    "21/2/2022", true, true, 5));
            marketDAO.insert(new Market("Otun Market", "Moba, Ekiti",
                    "22/2/2022", true, true, 5));

            return null;
        }
    }
}
