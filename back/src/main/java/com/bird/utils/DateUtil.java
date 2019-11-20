package com.bird.utils;

import java.lang.ref.SoftReference;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 对ApacheUtil包里的类再封装，将时区设置为东八区
 * @see org.apache.commons.lang3.time.DateUtils
 * @author zhyyy
 */
public final class DateUtil {
    private static final String[] DEFAULT_PATTERNS = new String[]{"EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy"};
    private static final String[] SIMPLE_PATTERNS = new String[]{"yyyyMMdd"};
    private static final Date DEFAULT_TWO_DIGIT_YEAR_START;
    public static final TimeZone GMT = TimeZone.getTimeZone("GMT+08:00");
    public static Date startOfThisDay(Long time) {
        if (time == null) {
            return null;
        }
        return startOfThisDay(new Date(time));
    }

    public static Date startOfThisDay(Date date) {
        return parseDate(DateUtil.formatDate(date, SIMPLE_PATTERNS[0]), SIMPLE_PATTERNS);
    }

    public static Date startOfNextDay(Long time) {
        if (time == null) {
            return null;
        }
        return startOfNextDay(new Date(time));
    }

    public static Date startOfNextDay(Date date) {
        Date result = parseDate(DateUtil.formatDate(date, SIMPLE_PATTERNS[0]), SIMPLE_PATTERNS);
        result.setTime(result.getTime() + 24 * 3600 * 1000);
        return result;
    }

    public static Date parseDate(String dateValue) {
        return parseDate(dateValue, null, null);
    }

    public static Date parseDate(String dateValue, String[] dateFormats) {
        return parseDate(dateValue, dateFormats, null);
    }

    public static Date parseDate(String dateValue, String[] dateFormats, Date startDate) {
        String[] localDateFormats = dateFormats != null ? dateFormats : DEFAULT_PATTERNS;
        Date localStartDate = startDate != null ? startDate : DEFAULT_TWO_DIGIT_YEAR_START;
        String v = dateValue;
        if (dateValue.length() > 1 && dateValue.startsWith("'") && dateValue.endsWith("'")) {
            v = dateValue.substring(1, dateValue.length() - 1);
        }

        String[] arr$ = localDateFormats;
        int len$ = localDateFormats.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            String dateFormat = arr$[i$];
            SimpleDateFormat dateParser = DateUtil.DateFormatHolder.formatFor(dateFormat);
            dateParser.set2DigitYearStart(localStartDate);
            ParsePosition pos = new ParsePosition(0);
            Date result = dateParser.parse(v, pos);
            if (pos.getIndex() != 0) {
                return result;
            }
        }

        return null;
    }

    public static String formatDate(Date date) {
        return formatDate(date, "EEE, dd MMM yyyy HH:mm:ss zzz");
    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat formatter = DateUtil.DateFormatHolder.formatFor(pattern);
        return formatter.format(date);
    }

    public static void clearThreadLocal() {
        DateUtil.DateFormatHolder.clearThreadLocal();
    }

    private DateUtil() {
    }

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(GMT);
        calendar.set(2000, 0, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
    }

    static final class DateFormatHolder {
        private static final ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>> THREAD_LOCAL_FORMATS = new ThreadLocal<>();

        DateFormatHolder() {
        }

        public static SimpleDateFormat formatFor(String pattern) {
            SoftReference<Map<String, SimpleDateFormat>> ref = THREAD_LOCAL_FORMATS.get();
            Map<String, SimpleDateFormat> formats = ref == null ? null : (Map) ref.get();
            if (formats == null) {
                formats = new HashMap<>(16);
                THREAD_LOCAL_FORMATS.set(new SoftReference<>(formats));
            }

            SimpleDateFormat format = (SimpleDateFormat) ((Map) formats).get(pattern);
            if (format == null) {
                format = new SimpleDateFormat(pattern, Locale.US);
                format.setTimeZone(GMT);
                formats.put(pattern, format);
            }

            return format;
        }

        public static void clearThreadLocal() {
            THREAD_LOCAL_FORMATS.remove();
        }
    }
}

