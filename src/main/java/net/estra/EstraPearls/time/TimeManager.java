package net.estra.EstraPearls.time;

import net.estra.EstraPearls.PearlPlugin;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeManager {

    public static String getTimeEST(){


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        dateFormat.setTimeZone(TimeZone.getTimeZone("EDT"));
        String currentTimeAndDayInEST = dateFormat.format(new Date());
        return currentTimeAndDayInEST.toString();

    }

    public static String FormatUTCtoET(Date utcDate) {

        if(utcDate == null){
            return null;
        }

        // Create a SimpleDateFormat object with the desired format
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d yyyy HH:mm a zzzz");

        // Set the timezone of the formatter to EST
        formatter.setTimeZone(PearlPlugin.timezone);

        // Format the date in EST timezone
        String estDateStr = formatter.format(utcDate);

        // Return the formatted date string
        return estDateStr;
    }

}
