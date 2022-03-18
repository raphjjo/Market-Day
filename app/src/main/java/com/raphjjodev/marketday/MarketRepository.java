package com.raphjjodev.marketday;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MarketRepository {

    private MarketDAO marketDAO;
    private LiveData<List<Market>> allMarket;
    private LiveData<List<Market>> favouriteMarket;

    public MarketRepository(Application application) {

        MarketDatabase marketDatabase = MarketDatabase.getInstance(application);
        this.marketDAO = marketDatabase.marketDAO();
        this.allMarket = marketDAO.getAllMarket();
        this.favouriteMarket = marketDAO.getFavouriteMarket();
    }

    public void insert(Market market) {

        new InsertMarketAsyncTask(marketDAO).execute(market);
    }

    public void update(Market market) {

        new UpdateMarketAsyncTask(marketDAO).execute(market);
    }

    public void delete(Market market) {

        new DeleteMarketAsyncTask(marketDAO).execute(market);
    }

    public LiveData<List<Market>> getAllMarket() {

        return allMarket;
    }

    public LiveData<List<Market>> getFavouriteMarket() {

        return favouriteMarket;
    }

    private static class InsertMarketAsyncTask extends AsyncTask<Market, Void, Void> {

        private MarketDAO marketDAO;

        private InsertMarketAsyncTask (MarketDAO marketDAO) {

            this.marketDAO = marketDAO;
        }

        @Override
        protected Void doInBackground(Market... markets) {

            marketDAO.insert(markets[0]);
            return null;
        }
    }

    private static class UpdateMarketAsyncTask extends AsyncTask<Market, Void, Void> {

        private MarketDAO marketDAO;

        private UpdateMarketAsyncTask (MarketDAO marketDAO) {

            this.marketDAO = marketDAO;
        }

        @Override
        protected Void doInBackground(Market... markets) {

            marketDAO.update(markets[0]);
            return null;
        }
    }

    private static class DeleteMarketAsyncTask extends AsyncTask<Market, Void, Void> {

        private MarketDAO marketDAO;

        private DeleteMarketAsyncTask (MarketDAO marketDAO) {

            this.marketDAO = marketDAO;
        }

        @Override
        protected Void doInBackground(Market... markets) {

            marketDAO.delete(markets[0]);
            return null;
        }
    }

}
