package com.raphjjodev.marketday;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MarketDayAlgorithm {

    private final int numberOfDaysToNextMarketDay;
    private final String nextDateString;
    private final String dateFormatOrPattern= "dd/MM/yyyy";
    private final int interval;
    private final String currentDateString;

    public MarketDayAlgorithm(String lastDateString, int intervalSaved, boolean marketDayInclusive) {

        if (marketDayInclusive)
            this.interval = intervalSaved - 1;
        else
            this.interval = intervalSaved;

        this.currentDateString = setCurrentDate();
        //Simply parsing the strings to date format
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatOrPattern, Locale.getDefault());

        Date lastDate = null;
        Date currentDate = null;
        try {
            lastDate = sdf.parse(lastDateString);
            currentDate = sdf.parse(currentDateString);
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }

        if (lastDate !=null && currentDate != null) {
            //Getting days between last market day and current date
            long diff = currentDate.getTime() - lastDate.getTime();
            int daysDiff = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            //Getting days left till next market day
            int numberOfDaysAfterLastMarket = daysDiff % interval; //Subtracting this value with the interval gives us the remaining days to the next market day
            numberOfDaysToNextMarketDay = interval - numberOfDaysAfterLastMarket;

            //Ensuring that the next market date still shows current market date if it is the current date
            if (numberOfDaysToNextMarketDay == interval)
                nextDateString = currentDateString;
            else
                nextDateString = addDaysToDate(numberOfDaysToNextMarketDay, sdf);


        } else {

            Log.i("DateParsedError", "There has been an error while parsing the dates for either" +
                    " lastMarketDate or currentDate");
            numberOfDaysToNextMarketDay = -1;
            nextDateString = currentDateString;
        }
    }

    private String addDaysToDate(int daysToAdd, SimpleDateFormat sdf) {

        Calendar calendarForNext = Calendar.getInstance();
        calendarForNext.add(Calendar.DATE, daysToAdd);
        return sdf.format(calendarForNext.getTime());
    }

    public String getCurrentDate() {
        return parseDateToNeededFormat(currentDateString);
    }

    public String getDaysRemaining() {
        return setDaysRemaining(numberOfDaysToNextMarketDay, interval);
    }

    public String getNextDateString() {
        return parseDateToNeededFormat(nextDateString);
    }

    private String parseDateToNeededFormat(String date){

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatOrPattern, Locale.getDefault());
        try {
            Date myDate = sdf.parse(date);
            sdf.applyPattern("EEE, d MMM yyyy");
            assert myDate != null;
            return sdf.format(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private String setCurrentDate() {

        Date calendar = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatOrPattern, Locale.getDefault());
        return sdf.format(calendar);

    }

    private String setDaysRemaining(int daysRemainingInt, int marketDayInterval) {

        if (daysRemainingInt > 1 && daysRemainingInt != marketDayInterval) {

            return "In " + daysRemainingInt + " days";
        } else if (daysRemainingInt == 1) {

            return "Tomorrow";
        } else if (daysRemainingInt == marketDayInterval){

            return "Today";
        } else {

            return "Error";
        }
    }
}
