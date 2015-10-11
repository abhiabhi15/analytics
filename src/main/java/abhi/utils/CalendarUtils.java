package abhi.utils;

import java.util.Calendar;

/**
 * Author : abhishek
 * Created on 9/21/15.
 */
public class CalendarUtils {

    static Calendar calendar = Calendar.getInstance();

    enum Month {
        JANUARY("1"),
        FEBRUARY("2"),
        MARCH("3"),
        APRIL("4"),
        MAY("5"),
        JUNE("6"),
        JULY("7"),
        AUGUST("8"),
        SEPTEMBER("9"),
        OCTOBER("10"),
        NOVEMBER("11"),
        DECEMBER("12");

        String val;

        Month(String val) {
            this.val = val;
        }

        static String getMonth(String value) {
            for (Month month : Month.values()) {
                if (month.val.equalsIgnoreCase(value)) {
                    return month.name();
                }
            }
            return null;
        }
    }


    public static String getMonth(String month) {
        return Month.getMonth(month).toLowerCase();
    }

    public static String getUserTimeSpecs(long timeInMilliseconds) {

        calendar.setTimeInMillis(timeInMilliseconds);
        return getQuarter(calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.YEAR);
    }


    public static String getQuarter(String month) {
        int monthNum = Integer.parseInt(month);
        return getQuarter(monthNum);
    }

    public static String getQuarter(int monthNum) {
        if (monthNum <= 3) {
            return "q1";
        } else if (monthNum <= 6) {
            return "q2";
        } else if (monthNum <= 9) {
            return "q3";
        } else {
            return "q4";
        }
    }

    public static void main(String[] args) {
        System.out.println(Month.getMonth("7"));
        System.out.println(getQuarter("6"));
    }
}
