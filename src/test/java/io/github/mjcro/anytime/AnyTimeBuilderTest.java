package io.github.mjcro.anytime;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

public class AnyTimeBuilderTest {
    @Test
    public void testBuilderDefaultNotFails() {
        AnyTime.Builder().build();
    }

    @Test(dependsOnMethods = "testBuilderDefaultNotFails")
    public void testBuilderZones() {
        AnyTime.Builder builder = AnyTime.Builder();
        Assert.assertEquals(builder.withZone(ZoneId.of("CET")).build().getZoneId(), ZoneId.of("CET"));
        Assert.assertEquals(builder.withZone("Asia/Kolkata").build().getZoneId(), ZoneId.of("Asia/Kolkata"));
        Assert.assertEquals(builder.withZone(TimeZone.getTimeZone("Asia/Kolkata")).build().getZoneId(), ZoneId.of("Asia/Kolkata"));
    }

    @Test(dependsOnMethods = "testBuilderDefaultNotFails")
    public void testBuilderLocales() {
        AnyTime.Builder builder = AnyTime.Builder();
        Assert.assertEquals(builder.withLocale(Locale.GERMANY).build().getLocale(), Locale.GERMANY);
    }
}
