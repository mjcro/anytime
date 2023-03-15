package io.github.mjcro.anytime;

import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;

class Util {
    static final Pattern patternDigitsOnly = Pattern.compile("\\d+");

    static final Pattern patternYMDDash = Pattern.compile("\\d{4}[-./]\\d{2}[-./]\\d{2}");
    static final DateTimeFormatter fmtYMDDash = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd")
            .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
            .toFormatter();

    static final Pattern patternDMYDash = Pattern.compile("\\d{2}[-./]\\d{2}[-./]\\d{4}");
    static final DateTimeFormatter fmtDMYDash = new DateTimeFormatterBuilder()
            .appendPattern("dd-MM-yyyy")
            .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
            .toFormatter();

    static final Pattern patternMDYDash = Pattern.compile("\\d{1,2}[-./]\\d{1,2}[-./]\\d{2,4}");
    static final DateTimeFormatter fmtMDYDash = new DateTimeFormatterBuilder()
            .appendPattern("M-d-[yyyy][uu]")
            .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
            .toFormatter();


    static final Pattern patternMYSQL = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
    static final DateTimeFormatter fmtMYSQL = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd HH:mm:ss",
            Locale.ROOT
    );

    static final Pattern patternISO8601 = Pattern.compile("\\d{4}-\\d{2}-\\d{2}[T ]\\d{2}(:\\d{2}(:\\d{2})?)?(Z|[+-]\\d{2}(:?\\d{2})?)");
    static final DateTimeFormatter fmtISO8601 = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd['T'][' ']HH[:mm[:ss]]")
            .optionalStart().appendZoneId().optionalEnd()
            .optionalStart().appendOffset("+HHmm", "+0000").optionalEnd()
            .optionalStart().appendOffset("+HH:mm", "+00:00").optionalEnd()
            .optionalStart().appendOffset("+HH", "+00").optionalEnd()
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter();

    /**
     * Depending on locale determines if month and date placements are reversed.
     * Common is DD/MM format while "reversed" in terms of AnyTime is MM/DD.
     *
     * @param locale Locale to use.
     * @return True if date format is MM/DD, false if date format is DD/MM.
     */
    public static boolean isDayMonthReversed(Locale locale) {
        Objects.requireNonNull(locale, "locale");

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(2022, Calendar.JUNE, 9);

        String x = DateFormat.getDateInstance(DateFormat.SHORT, locale).format(cal.getTime());
        return x.startsWith("6") || x.startsWith("06");
    }

    private Util() {
    }
}
