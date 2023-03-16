package io.github.mjcro.anytime;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Instant;

public class AnyTimeOptionalsTest {
    @Test
    public void testOptionalFromPresent() {
        Instant now = Instant.now();
        Assert.assertEquals(AnyTime.UTCSeconds.optionalFrom(now).get(), now);
    }

    @Test
    public void testOptionalParsePresent() {
        String pattern = "2009-02-13T23:31:37Z";
        Assert.assertEquals(AnyTime.UTCSeconds.optionalParse(pattern).get(), Instant.parse(pattern));
    }

    @DataProvider
    public Object[][] incorrectObjectsDataProvider() {
        return new Object[][]{
                {null},
                {""},
                {" "},
                {new Object()},
        };
    }

    @Test(dataProvider = "incorrectObjectsDataProvider", dependsOnMethods = "testOptionalFromPresent")
    public void testOptionalFromEmpty(Object given) {
        Assert.assertFalse(AnyTime.UTCSeconds.optionalFrom(given).isPresent());
    }

    @DataProvider
    public Object[][] incorrectStringsDataProvider() {
        return new Object[][]{
                {null},
                {""},
                {" "},
                {"abcdefg"},
        };
    }

    @Test(dataProvider = "incorrectStringsDataProvider", dependsOnMethods = "testOptionalParsePresent")
    public void testOptionalParseEmpty(String given) {
        Assert.assertFalse(AnyTime.UTCSeconds.optionalParse(given).isPresent());
    }
}
