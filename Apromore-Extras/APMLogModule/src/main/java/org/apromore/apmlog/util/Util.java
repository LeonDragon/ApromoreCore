package org.apromore.apmlog.util;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.*;

import java.io.File;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Chii Chang (11/2019)
 * Modified: Frank Ma (16/11/2019)
 * Modified: Chii Chang (28/01/2020)
 */
public class Util {

    private static final double year = 1000.0D * 60 * 60 * 24 * 365;
    private static final double month = 1000.0D * 60 * 60 * 24 * 31;
    private static final double week = 1000.0D * 60 * 60 * 24 * 7;
    private static final double day = 1000.0D * 60 * 60 * 24;
    private static final double hour = 1000.0D *  60 * 60;
    private static final double minute = 1000.0D *  60;
    private static final double second = 1000.0D;

    public static long epochMilliOf(ZonedDateTime zonedDateTime){

        long s = zonedDateTime.toInstant().toEpochMilli();
        return s;
    }

    public static ZonedDateTime zonedDateTimeOf(XEvent xEvent) {
        XAttribute da =
                xEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP);
        Date d = ((XAttributeTimestamp) da).getValue();
        ZonedDateTime z =
                ZonedDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
        return z;
    }

    public static ZonedDateTime millisecondToZonedDateTime(long millisecond){
        Instant i = Instant.ofEpochMilli(millisecond);
        ZonedDateTime z = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
        return z;
    }

    public static String timestampStringOf(ZonedDateTime zdt){

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return zdt.format(formatter);
    }


    public static String durationShortStringOf(long duration) {
        return convertMilliseconds(duration);
    }

    private static final DecimalFormat df2 = new DecimalFormat("###############.##");

    public static String convertMilliseconds(long millis) {
        double secs = millis / second;
        double mins = millis / minute;
        double hrs = millis / hour;
        double days = millis / day;
        double wks = millis / week;
        double mths = millis / month;
        double yrs = millis / year;

        if (yrs > 1.0D) return df2.format(yrs) + " yrs";
        if (mths > 1.0D) return df2.format(mths) + " mths";
        if (wks > 1.0D) return df2.format(wks) + " wks";
        if (days > 1.0D) return df2.format(days) + " d";
        if (hrs > 1.0D) return df2.format(hrs) + " hrs";
        if (mins > 1.0D) return df2.format(mins) + " mins";
        if (secs > 1.0D) return df2.format(secs) + " secs";
        if (millis > 1.0D) return df2.format(millis) + " millis";
        return "instant";
    }


}
