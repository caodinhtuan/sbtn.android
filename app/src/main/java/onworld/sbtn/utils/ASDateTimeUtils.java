package onworld.sbtn.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Created by sb1 on 5/7/15.
 */
public class ASDateTimeUtils {

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
    public static String formatMillis(int millisec) {
        int seconds = (int) (millisec / 1000);
        int hours = seconds / (60 * 60);
        seconds %= (60 * 60);
        int minutes = seconds / 60;
        seconds %= 60;

        String time;
        if (hours > 0) {
            time = String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            time = String.format("%d:%02d", minutes, seconds);
        }
        return time;
    }


}
