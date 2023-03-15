package io.github.mjcro.anytime;

import org.testng.annotations.Test;

public class AnyTimeExceptionsTest {
    @Test(expectedExceptions = UnsupportedTypeException.class)
    public void testFromUnsupported() {
        AnyTime.UTCSeconds().from(new Object());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testParseNullString() {
        AnyTime.UTCSeconds().parse(null);
    }

    @Test(expectedExceptions = EmptyInstantException.class)
    public void testParseEmptyString() {
        AnyTime.UTCSeconds().parse("");
    }

    @Test(expectedExceptions = EmptyInstantException.class)
    public void testParseBlankString() {
        AnyTime.UTCSeconds().parse(" \n");
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void testParseLargeLong() {
        AnyTime.UTCSeconds().parse("123456789012345678901234567890");
    }
}
