package com.raphjjodev.marketday;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MarketViewModel extends AndroidViewModel {

    private MarketRepository marketRepository;
    private LiveData<List<Market>> allMarket;
    private LiveData<List<Market>> favouriteMarket;

    public MarketViewModel (@NonNull Application application) {
        super(application);

        this.marketRepository = new MarketRepository(application);
        this.allMarket = marketRepository.getAllMarket();
        this.favouriteMarket = marketRepository.getFavouriteMarket();
    }

    public void insert(Market market) {

        marketRepository.insert(market);
    }

    public void update(Market market) {

        marketRepository.update(market);
    }

    public void delete(Market market) {

        marketRepository.delete(market);
    }

    public LiveData<List<Market>> getAllMarket() {

        return allMarket;
    }

    public LiveData<List<Market>> getFavouriteMarket() {

        return favouriteMarket;
    }
}
