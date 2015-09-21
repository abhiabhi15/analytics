package abhi.utils;

/**
 * Author : abhishek
 * Created on 9/21/15.
 */
public class CalendarUtils {

    enum Month{
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

        Month(String val){
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
        return null;
    }


    public static String getQuarter(String month) {
        return null;
    }

    public static void main(String[] args) {
        System.out.println(Month.getMonth("1"));
    }
}
