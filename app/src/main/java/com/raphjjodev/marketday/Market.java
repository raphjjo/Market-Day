package com.raphjjodev.marketday;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "market_table")
public class Market {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    private String location;

    private String lastDate;

    private boolean dayInclusive;

    private boolean favourite;

    private int interval;

    public Market(String name, String location, String lastDate, boolean dayInclusive, boolean favourite, int interval) {
        this.name = name;
        this.location = location;
        this.lastDate = lastDate;
        this.dayInclusive = dayInclusive;
        this.favourite = favourite;
        this.interval = interval;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getLastDate() {
        return lastDate;
    }

    public boolean isDayInclusive() {
        return dayInclusive;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public int getInterval() {
        return interval;
    }
}
