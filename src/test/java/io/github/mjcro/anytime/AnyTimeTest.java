package io.github.mjcro.anytime;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

public class AnyTimeTest {
    @Test
    public void testUnixSecondsAndMilliseconds() {
        Assert.assertEquals(
                AnyTime.UTCSeconds.fromLong(1234567890), // Seconds
                Instant.parse("2009-02-13T23:31:30Z")
        );
        Assert.assertEquals(
                AnyTime.UTCMillis.fromLong(1234567890), // Milliseconds
                Instant.parse("1970-01-15T06:56:07.890Z")
        );
    }


    @DataProvider
    public Object[][] fromObjectDataProvider() {
        return new Object[][]{
                {Instant.parse("2023-01-01T13:01:48Z"), Instant.parse("2023-01-01T13:01:48Z")},
                {Optional.of(Instant.parse("2023-01-01T13:01:48Z")), Instant.parse("2023-01-01T13:01:48Z")},
                {(Supplier<String>) () -> "1980-03-08 12:34:56", Instant.parse("1980-03-08T12:34:56Z")},

                // Integers
                {1234567897, Instant.parse("2009-02-13T23:31:37Z")},

                // Floating points
                {1234567897.12, Instant.parse("2009-02-13T23:31:37.120Z")},
                {999999999.987654, Instant.parse("2001-09-09T01:46:39.987654016Z")},

                {Date.from(Instant.parse("2009-02-13T23:31:37.120Z")), Instant.parse("2009-02-13T23:31:37.120Z")},

                // Zoned
                {LocalDate.parse("2031-12-13"), Instant.parse("2031-12-13T00:00:00Z")},
                {ZonedDateTime.parse("2023-03-15T16:33:10+02:00[Europe/Kiev]"), Instant.parse("2023-03-15T14:33:10Z")},
        };
    }

    @Test(dataProvider = "fromObjectDataProvider")
    public void testFromObject(Object given, Instant expected) {
        Assert.assertEquals(AnyTime.UTCSeconds.from(given), expected);
    }


    @DataProvider
    public Object[][] fromDoubleDataProvider() {
        return new Object[][]{
                {1234567897.12, Instant.parse("2009-02-13T23:31:37.120Z")},
                {1678885308., Instant.parse("2023-03-15T13:01:48Z")},
                {999999999.987654, Instant.parse("2001-09-09T01:46:39.987654016Z")}
        };
    }

    @Test(dataProvider = "fromDoubleDataProvider")
    public void testFromDouble(double given, Instant expected) {
        Assert.assertEquals(AnyTime.UTCSeconds.fromDouble(given), expected);
    }

    @DataProvider
    public Object[][] parseDataProvider() {
        return new Object[][]{
                // Epoch seconds
                {"1678890790", Instant.parse("2023-03-15T14:33:10Z")},

                {"2021-06-05", Instant.parse("2021-06-05T00:00:00Z")},
                {"2021-6-5", Instant.parse("2021-06-05T00:00:00Z")},
                {"21-6-5", Instant.parse("2021-06-05T00:00:00Z")},
                {"2021.06.05", Instant.parse("2021-06-05T00:00:00Z")},
                {"2021/06/05", Instant.parse("2021-06-05T00:00:00Z")},
                {"12-11-1999", Instant.parse("1999-11-12T00:00:00Z")},
                {"12/11/1999", Instant.parse("1999-11-12T00:00:00Z")},
                {"12.11.1999", Instant.parse("1999-11-12T00:00:00Z")},
                {"2.1.1999", Instant.parse("1999-01-02T00:00:00Z")},
                {"2.1.02", Instant.parse("2002-01-02T00:00:00Z")},

                {"2012-03-04 15:22:11", Instant.parse("2012-03-04T15:22:11Z")},

                // ISO8601
                {"2017-07-10T08:00:00-0800", Instant.parse("2017-07-10T16:00:00Z")},
                {"2028-03-15T07:24:05-03:00", Instant.parse("2028-03-15T10:24:05Z")},
                {"2028-03-15T07:24:05+00:00", Instant.parse("2028-03-15T07:24:05Z")},
                {"2028-03-15T07:24:05+02:00", Instant.parse("2028-03-15T05:24:05Z")},
                {"2028-03-15T07:24+02:00", Instant.parse("2028-03-15T05:24:00Z")},
                {"2028-03-15T07+02:00", Instant.parse("2028-03-15T05:00:00Z")},
                {"2028-03-15T07:24:05-0300", Instant.parse("2028-03-15T10:24:05Z")},
                {"2028-03-15T07:24:05+0000", Instant.parse("2028-03-15T07:24:05Z")},
                {"2028-03-15T07:24:05+0200", Instant.parse("2028-03-15T05:24:05Z")},
                {"2028-03-15T07:24+0200", Instant.parse("2028-03-15T05:24:00Z")},
                {"2028-03-15T07+0200", Instant.parse("2028-03-15T05:00:00Z")},
                {"2028-03-15T07:24:05-03", Instant.parse("2028-03-15T10:24:05Z")},
                {"2028-03-15T07:24:05+00", Instant.parse("2028-03-15T07:24:05Z")},
                {"2028-03-15T07:24:05+02", Instant.parse("2028-03-15T05:24:05Z")},
                {"2028-03-15T07:24+02", Instant.parse("2028-03-15T05:24:00Z")},
                {"2028-03-15T07+02", Instant.parse("2028-03-15T05:00:00Z")},

                // ISO-Like
                {"2012.08.22 04:02:16", Instant.ofEpochSecond(1345608136)},
                {"2012/08/22 04:02:16", Instant.ofEpochSecond(1345608136)},
                {"22-08-2012 04:02:16", Instant.ofEpochSecond(1345608136)},
                {"22.08.2012 04:02:16", Instant.ofEpochSecond(1345608136)},
                {"22/08/2012 04:02:16", Instant.ofEpochSecond(1345608136)},

                // BC integration edge cases
                {"2023-08-17T08:25:09.1", Instant.parse("2023-08-17T08:25:09.1Z")},
                {"2023-08-17T08:25:09.09", Instant.parse("2023-08-17T08:25:09.09Z")},
                {"2023-08-17T08:25:09.073", Instant.parse("2023-08-17T08:25:09.073Z")},
                {"2023-08-17T08:25:09.0733", Instant.parse("2023-08-17T08:25:09.0733Z")},
                {"2023-08-17T08:25:09.07333", Instant.parse("2023-08-17T08:25:09.07333Z")},
                {"2023-08-17T08:25:09.073333", Instant.parse("2023-08-17T08:25:09.073333Z")},
                {"2023-08-17T08:25:09.0733333", Instant.parse("2023-08-17T08:25:09.0733333Z")},
                {"2023-09-25T10:50:47.0626603+00:00", Instant.parse("2023-09-25T10:50:47.0626603Z")},
                {"2023-09-25T12:50:47.0626603+02:00", Instant.parse("2023-09-25T10:50:47.0626603Z")},
        };
    }

    @Test(dataProvider = "parseDataProvider")
    public void testParse(String given, Instant expected) {
        Assert.assertEquals(AnyTime.UTCSeconds.parse(given), expected, "Error parsing '" + given + "'");
    }

    @DataProvider
    public Object[][] parseDifferentLocalesDataProvider() {
        return new Object[][]{
                {"12-11-1999", Instant.parse("1999-12-11T00:00:00Z"), Locale.US},
                {"12/11/1999", Instant.parse("1999-12-11T00:00:00Z"), Locale.US},
                {"12.11.1999", Instant.parse("1999-12-11T00:00:00Z"), Locale.US},
                {"6.11.1999", Instant.parse("1999-06-11T00:00:00Z"), Locale.US},
                {"3/1/2023", Instant.parse("2023-03-01T00:00:00Z"), Locale.US},
                {"11-12-1999", Instant.parse("1999-12-11T00:00:00Z"), Locale.KOREA},
                {"11/12/1999", Instant.parse("1999-12-11T00:00:00Z"), Locale.KOREA},
                {"01/03/2023", Instant.parse("2023-03-01T00:00:00Z"), Locale.GERMANY},
                {"01-03-2023", Instant.parse("2023-03-01T00:00:00Z"), Locale.GERMANY},
                {"01.03.2023", Instant.parse("2023-03-01T00:00:00Z"), Locale.GERMANY},
        };
    }

    @Test(dataProvider = "parseDifferentLocalesDataProvider")
    public void testParseDifferentLocales(String given, Instant expected, Locale locale) {
        Assert.assertEquals(new AnyTime(AnyTime.UTC, locale, true).parse(given), expected, "Error parsing '" + given + "'");
    }

    @Test
    public void testParseTwoDigitYear() {
        Assert.assertEquals(
                AnyTime.UTCSeconds.parse("1.2.99"),
                Instant.parse("2099-02-01T00:00:00Z") // Year 2099 !
        );
    }
}