package com.raphjjodev.marketday;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MarketDAO {

    @Insert
    void insert(Market market);

    @Update
    void update(Market market);

    @Delete
    void delete(Market market);

    @Query("SELECT * FROM market_table ORDER BY id ASC")
    LiveData<List<Market>> getAllMarket();

    @Query("SELECT * FROM market_table WHERE favourite = true ORDER BY id ASC")
    LiveData<List<Market>> getFavouriteMarket();
}
