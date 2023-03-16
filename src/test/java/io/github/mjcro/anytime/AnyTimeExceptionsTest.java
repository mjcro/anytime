package io.github.mjcro.anytime;

import org.testng.annotations.Test;

import java.time.Instant;

public class AnyTimeExceptionsTest {
    @Test(expectedExceptions = UnsupportedTypeException.class)
    public void testFromUnsupported() {
        AnyTime.UTCSeconds.from(new Object());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testFromNull() {
        AnyTime.UTCSeconds.from(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testParseNullString() {
        AnyTime.UTCSeconds.parse(null);
    }

    @Test(expectedExceptions = EmptyInstantException.class)
    public void testParseEmptyString() {
        AnyTime.UTCSeconds.parse("");
    }

    @Test(expectedExceptions = EmptyInstantException.class)
    public void testParseBlankString() {
        AnyTime.UTCSeconds.parse(" \n");
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void testParseLargeLong() {
        AnyTime.UTCSeconds.parse("123456789012345678901234567890");
    }

    @Test(expectedExceptions = EmptyInstantException.class)
    public void testCustomProcessorReturningNull() {
        AnyTime.Builder()
                .withZone(AnyTime.UTC)
                .withLocale(AnyTime.ROOT)
                .withIntegersAsUnixSeconds(true)
                .withProcessor(new StringProcessor() {
                    @Override
                    public Instant apply(final String s) {
                        return null;
                    }

                    @Override
                    public boolean test(final String s) {
                        return true;
                    }
                })
                .build()
                .parse("12345");
    }
}
