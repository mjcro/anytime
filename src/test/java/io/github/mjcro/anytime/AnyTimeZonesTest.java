package io.github.mjcro.anytime;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.ZoneId;

public class AnyTimeZonesTest {
    @DataProvider
    public Object[][] parseZonedDataProvider() {
        return new Object[][]{
                {ZoneId.of("Europe/Kiev"), "2021-07-25", Instant.parse("2021-07-24T21:00:00Z")},
                {ZoneId.of("Europe/Kiev"), "2021-12-25", Instant.parse("2021-12-24T22:00:00Z")},
                {ZoneId.of("America/New_York"), "2021-07-25", Instant.parse("2021-07-25T04:00:00Z")},
                {ZoneId.of("America/New_York"), "2021-12-25", Instant.parse("2021-12-25T05:00:00Z")},

                {ZoneId.of("Europe/Kiev"), "2028-03-15T07+02", Instant.parse("2028-03-15T05:00:00Z")}, // Zone is ignored 'cause time offset is present
                {ZoneId.of("America/New_York"), "2028-03-15T07+02", Instant.parse("2028-03-15T05:00:00Z")}, // Zone is ignored 'cause time offset is present
        };
    }

    @Test(dataProvider = "parseZonedDataProvider")
    public void testParseZoned(ZoneId zoneId, String given, Instant expected) {
        Assert.assertEquals(
                new AnyTime(zoneId, AnyTime.ROOT, true).parse(given),
                expected,
                "Error parsing '" + given +"'"
        );
    }
}
