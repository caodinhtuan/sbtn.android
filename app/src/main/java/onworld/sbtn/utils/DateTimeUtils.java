package onworld.sbtn.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Created by sb1 on 5/7/15.
 */
public class DateTimeUtils {

    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    public static String getCurrentUTCDateString() {
        String result = "";
        try {
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

            result = dateFormatGmt.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getCurrentUTCDateTime() {
        String result = "";
        try {
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

            result = dateFormatGmt.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
