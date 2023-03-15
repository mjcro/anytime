package io.github.mjcro.anytime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * General use case parser able to transform date/time in most used
 * formats into Java Instant object.
 * <p>
 * Immutable and thread safe.
 */
public class AnyTime {
    /**
     * UTC timezone ID.
     */
    public static final ZoneId UTC = ZoneId.of("UTC");
    /**
     * General locale.
     */
    public static final Locale ROOT = Locale.ROOT;
    /**
     * Instance of parser configured with UTC timezone and general locale.
     * Will count long values as unix seconds.
     */
    public static AnyTime UTCSeconds = new AnyTime(UTC, ROOT, true);
    /**
     * Instance of parser configured with UTC timezone and general locale.
     * Will count long values as unix milliseconds.
     */
    public static AnyTime UTCMillis = new AnyTime(UTC, ROOT, false);

    private final ZoneId zoneId;
    private final Locale locale;
    private final boolean seconds;

    private final ArrayList<Matcher> stringMatchers;

    /**
     * Constructs any date/time reader/parser.
     *
     * @param zoneId     Zone identifier to use when parsing string dates.
     * @param locale     Locale to use, reserved for future use.
     * @param intSeconds True if integers should be read as unix seconds, false if unix milliseconds.
     */
    public AnyTime(final ZoneId zoneId, final Locale locale, final boolean intSeconds) {
        this.zoneId = Objects.requireNonNull(zoneId, "zoneId");
        this.locale = Objects.requireNonNull(locale, "locale");
        this.seconds = intSeconds;

        stringMatchers = new ArrayList<>();
        stringMatchers.add(new Matcher(Util.patternYMDDash, s -> Util.fmtYMDDash.withZone(getZoneId()).parse(s.replaceAll("[./]", "-"))));
        stringMatchers.add(new Matcher(Util.patternDMYDash, s -> Util.fmtDMYDash.withZone(getZoneId()).parse(s.replaceAll("[./]", "-"))));
        stringMatchers.add(new Matcher(Util.patternMYSQL, s -> Util.fmtMYSQL.withZone(getZoneId()).parse(s)));
        stringMatchers.add(new Matcher(Util.patternISO8601, s -> Util.fmtISO8601.withZone(getZoneId()).parse(s)));
    }

    /**
     * @return Zone identifier this parser configured with.
     */
    public ZoneId getZoneId() {
        return zoneId;
    }

    /**
     * @return Locale this parser configured with.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @return True if integer values should be read as unix epoch seconds.
     */
    public boolean areIntegersSeconds() {
        return seconds;
    }

    /**
     * Tries to convert (cast, parse) of "any" given object to instant
     * using following rules:
     * - null causes NPE
     * - Instant given will be returned
     * - TemporalAccessor will be wrapped as instant
     * - Date will be converted to instant
     * - Numbers will be processed using fromLong or fromDouble methods
     * - Char sequences will be parsed using parse method.
     * - Optional will be unfolded
     *
     * @param any Object that contains date/time in any format.
     * @return Instant.
     * @throws NullPointerException     If null or empty optional given.
     * @throws UnsupportedTypeException If unsupported object provided.
     * @throws EmptyInstantException    If empty or blank string given.
     */
    public Instant from(Object any) {
        if (any == null) {
            throw new NullPointerException();
        }

        if (any instanceof Instant) {
            return (Instant) any;
        } else if (any instanceof TemporalAccessor) {
            if (any instanceof LocalDate) {
                return ((LocalDate) any).atStartOfDay(getZoneId()).toInstant();
            }
            return Instant.from((TemporalAccessor) any);
        } else if (any instanceof Date) {
            return ((Date) any).toInstant();
        } else if (any instanceof Number) {
            Number number = (Number) any;
            return number instanceof Double || number instanceof Float
                    ? fromDouble(number.doubleValue())
                    : fromLong(number.longValue());
        } else if (any instanceof CharSequence) {
            return parse(any.toString());
        } else if (any instanceof Optional) {
            return from(((Optional<?>) any).orElse(null));
        } else {
            throw new UnsupportedTypeException(any.getClass());
        }
    }

    /**
     * Constructs instant from given integer value.
     *
     * @param number Unix seconds or milliseconds.
     * @return Instant.
     */
    public Instant fromLong(long number) {
        if (areIntegersSeconds()) {
            return Instant.ofEpochSecond(number);
        }
        return Instant.ofEpochMilli(number);
    }

    /**
     * Constructs instant from given floating point value.
     *
     * @param number Unix time in seconds.
     * @return Instant.
     */
    public Instant fromDouble(double number) {
        long seconds = (long) (number % 1);
        long nanos = (long) (1e9 * (number - seconds));
        return Instant.ofEpochSecond(seconds, nanos);
    }

    /**
     * Parses string using all available and configured method.
     *
     * @param string String to parse.
     * @return Instant.
     * @throws NullPointerException  If null given.
     * @throws EmptyInstantException If blank string given.
     * @throws NumberFormatException If string contains only digits, but it can be parsed into Long
     */
    public Instant parse(String string) {
        if (string == null) {
            throw new NullPointerException();
        }
        string = string.trim();
        if (string.isEmpty()) {
            throw new EmptyInstantException();
        }

        // Check for numeric
        if (Util.patternDigitsOnly.matcher(string).matches()) {
            return fromLong(Long.parseLong(string));
        }

        // Applying matchers
        for (Matcher matcher : stringMatchers) {
            if (matcher.pattern.matcher(string).matches()) {
                return Instant.from(matcher.reader.apply(string));
            }
        }

        // The final attempt - using instant itself
        return Instant.parse(string);
    }

    /**
     * Utility class used to configure string parsers.
     */
    private static final class Matcher {
        private final Pattern pattern;
        private final Function<String, TemporalAccessor> reader;

        private Matcher(Pattern pattern, Function<String, TemporalAccessor> reader) {
            this.pattern = pattern;
            this.reader = reader;
        }
    }
}
