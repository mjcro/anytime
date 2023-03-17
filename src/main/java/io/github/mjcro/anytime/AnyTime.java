package io.github.mjcro.anytime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
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
    public static final Locale ROOT = Locale.UK;
    /**
     * Instance of parser configured with UTC timezone and general locale.
     * Will count long values as unix seconds.
     */
    public static AnyTime UTCSeconds = Builder()
            .withIntegersAsUnixSeconds()
            .withZone(UTC)
            .withLocale(ROOT)
            .build();
    /**
     * Instance of parser configured with UTC timezone and general locale.
     * Will count long values as unix milliseconds.
     */
    public static AnyTime UTCMillis = Builder()
            .withIntegersAsUnixMilliseconds()
            .withZone(UTC)
            .withLocale(ROOT)
            .build();

    private final ZoneId zoneId;
    private final Locale locale;
    private final boolean seconds;

    private final ArrayList<StringProcessor> stringProcessors;

    /**
     * Constructs any date/time reader/parser.
     *
     * @param zoneId                 Zone identifier to use when parsing string dates.
     * @param locale                 Locale to use, reserved for future use.
     * @param intSeconds             True if integers should be read as unix seconds, false if unix milliseconds.
     * @param customStringProcessors Custom string processor to use during parse. They will be invoked first.
     */
    public AnyTime(
            final ZoneId zoneId,
            final Locale locale,
            final boolean intSeconds,
            final StringProcessor... customStringProcessors
    ) {
        this.zoneId = Objects.requireNonNull(zoneId, "zoneId");
        this.locale = Objects.requireNonNull(locale, "locale");
        this.seconds = intSeconds;

        stringProcessors = new ArrayList<>();
        if (customStringProcessors != null && customStringProcessors.length > 0) {
            stringProcessors.addAll(Arrays.asList(customStringProcessors));
        }
        stringProcessors.add(new Matcher(Util.patternDigitsOnly, s -> fromLong(Long.parseLong(s))));
        if (Util.isMonthBeforeDay(getLocale())) {
            // US-like dates, MM/DD/YYYY
            stringProcessors.add(new Matcher(Util.patternMDYDash, s -> Util.fmtMDYDash.withZone(getZoneId()).parse(s.replaceAll("[./]", "-"))));
            stringProcessors.add(new Matcher(Util.patternMYSQLReverse, s -> Util.fmtMYSQLReverseMonthFirst.withZone(zoneId).parse(s.replaceAll("[./]", "-"))));
        } else {
            // Europe-like dates, DD/MM/YYYY
            stringProcessors.add(new Matcher(Util.patternDMYDash, s -> Util.fmtDMYDash.withZone(getZoneId()).parse(s.replaceAll("[./]", "-"))));
            stringProcessors.add(new Matcher(Util.patternMYSQLReverse, s -> Util.fmtMYSQLReverse.withZone(zoneId).parse(s.replaceAll("[./]", "-"))));
        }
        stringProcessors.add(new Matcher(Util.patternYMDDash, s -> Util.fmtYMDDash.withZone(getZoneId()).parse(s.replaceAll("[./]", "-"))));
        stringProcessors.add(new Matcher(Util.patternMYSQL, s -> Util.fmtMYSQL.withZone(getZoneId()).parse(s.replaceAll("[./]", "-"))));
        stringProcessors.add(new Matcher(Util.patternISO8601_ZONE, s -> Util.fmtISO8601.parse(s))); // Java 8 fix
        stringProcessors.add(new Matcher(Util.patternISO8601, s -> Util.fmtISO8601.withZone(getZoneId()).parse(s)));
        stringProcessors.add(new Matcher(Util.patternTwitter, s -> Util.fmtTwitter.withLocale(getLocale()).parse(s)));
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
        } else if (any instanceof Supplier) {
            return from(((Supplier<?>) any).get());
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

        // Applying matchers
        for (StringProcessor processor : stringProcessors) {
            if (processor.test(string)) {
                Instant value = processor.apply(string);
                if (value == null) {
                    throw new EmptyInstantException(processor);
                }
                return value;
            }
        }

        // The final attempt - using instant itself
        return Instant.parse(string);
    }

    /**
     * Works similar to {@link #from} but instead of throwing
     * exception it will return Optional.empty.
     *
     * @param any Any value to read instant from.
     * @return Optional instant result.
     */
    public Optional<Instant> optionalFrom(Object any) {
        try {
            return Optional.of(from(any));
        } catch (Exception ignore) {
            // Suppressing
            return Optional.empty();
        }
    }

    /**
     * Works similar to {@link #parse} but instead of throwing
     * exception it will return Optional.empty.
     *
     * @param string String to parse.
     * @return Optional instant result.
     */
    public Optional<Instant> optionalParse(String string) {
        try {
            return Optional.of(parse(string));
        } catch (Exception ignore) {
            // Suppressing
            return Optional.empty();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnyTime)) return false;
        AnyTime anyTime = (AnyTime) o;
        return areIntegersSeconds() == anyTime.areIntegersSeconds() && getZoneId().equals(anyTime.getZoneId()) && getLocale().equals(anyTime.getLocale());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getZoneId(), getLocale(), areIntegersSeconds());
    }

    @Override
    public String toString() {
        return "AnyTime{" +
                "zoneId=" + getZoneId() +
                ", locale=" + (getLocale() == ROOT ? "ROOT" : getLocale()) +
                ", " + (areIntegersSeconds() ? "seconds" : "milliseconds") +
                '}';
    }

    /**
     * Utility class used to configure string parsers.
     */
    private static final class Matcher implements StringProcessor {
        private final Pattern pattern;
        private final Function<String, TemporalAccessor> reader;

        private Matcher(Pattern pattern, Function<String, TemporalAccessor> reader) {
            this.pattern = pattern;
            this.reader = reader;
        }

        @Override
        public boolean test(String s) {
            return pattern.matcher(s).matches();
        }

        @Override
        public Instant apply(String s) {
            return Instant.from(reader.apply(s));
        }
    }

    /**
     * @return Builder.
     */
    public static Builder Builder() {
        return new Builder();
    }

    /**
     * Mutable helper to build {@link AnyTime} instance.
     */
    public static final class Builder {
        private ZoneId zoneId = UTC;
        private Locale locale = AnyTime.ROOT;
        private boolean integersAsUnixSeconds = true;
        private final ArrayList<StringProcessor> processors = new ArrayList<>();

        /**
         * Set time zone.
         *
         * @param zoneId Zone identifier.
         * @return Self.
         */
        public Builder withZone(ZoneId zoneId) {
            this.zoneId = Objects.requireNonNull(zoneId, "zoneId");
            return this;
        }

        /**
         * Set time zone.
         *
         * @param timeZone Time zone.
         * @return Self.
         */
        public Builder withZone(TimeZone timeZone) {
            return withZone(timeZone.toZoneId());
        }

        /**
         * Set time zone.
         *
         * @param zoneId Name of ZoneId.
         * @return Self.
         */
        public Builder withZone(String zoneId) {
            return withZone(ZoneId.of(zoneId));
        }

        /**
         * Set locale.
         *
         * @param locale Locale.
         * @return Self.
         */
        public Builder withLocale(Locale locale) {
            this.locale = Objects.requireNonNull(locale, "locale");
            return this;
        }

        /**
         * Sets integer processing mode.
         *
         * @param value True if integers are unix seconds, false if unix milliseconds.
         * @return Self.
         */
        public Builder withIntegersAsUnixSeconds(boolean value) {
            this.integersAsUnixSeconds = value;
            return this;
        }

        /**
         * Sets integer processing mode - all integers are unix seconds.
         *
         * @return Self.
         */
        public Builder withIntegersAsUnixSeconds() {
            return withIntegersAsUnixSeconds(true);
        }

        /**
         * Sets integer processing mode - all integers are unix milliseconds.
         *
         * @return Self.
         */
        public Builder withIntegersAsUnixMilliseconds() {
            return withIntegersAsUnixSeconds(false);
        }

        /**
         * Appends new string parse processor.
         *
         * @param processor String parse processor to use.
         * @return Self.
         */
        public Builder withProcessor(StringProcessor processor) {
            this.processors.add(Objects.requireNonNull(processor, "processor"));
            return this;
        }

        /**
         * @return Date/time parser instance.
         */
        public AnyTime build() {
            return new AnyTime(zoneId, locale, integersAsUnixSeconds, processors.toArray(new StringProcessor[0]));
        }
    }
}
