package ekutil;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class EkDate {
    private EkDate() {

    }

    public static String getCurrentDateTimeString(String pattern) {
        ZonedDateTime now = ZonedDateTime.now();
        return convertDateTimeToString(now, pattern);
    }

    public static String convertDateTimeToString(ZonedDateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    public static ZonedDateTime getHighDate() {
        return ZonedDateTime.of(3000, 01, 01, 00, 00, 00, 0000, ZoneId.of("UTC+1"));
    }

    public static ZonedDateTime getLowDate() {
        return ZonedDateTime.of(1900, 01, 01, 00, 00, 00, 0000, ZoneId.of("UTC+1"));
    }

}
