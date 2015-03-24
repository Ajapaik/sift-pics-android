package pics.sift.android.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Dates {
    private static final SimpleDateFormat ISO_8601 = new Iso8601DateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT);

    public static Date parse(String str) {
        if(str != null) {
            try {
                return ISO_8601.parse(str);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static String toString(Date date) {
        return (date != null) ? ISO_8601.format(date) : null;
    }

    private static class Iso8601DateFormat extends SimpleDateFormat {
        static final long serialVersionUID = 1L;

        public Iso8601DateFormat(String format, Locale locale) {
            super(format, locale);
            setTimeZone(TimeZone.getTimeZone("UTC"));
        }
    }
}
