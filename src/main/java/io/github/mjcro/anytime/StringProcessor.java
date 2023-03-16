package io.github.mjcro.anytime;

import java.time.Instant;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * String processor is unit of work performed by {@link AnyTime#parse(String)} method
 * to convert string data to {@link Instant}.
 * <p>
 * First AnyTime will trigger {@link #test} method and if it returns true,
 * {@link #apply} will be invoked.
 */
public interface StringProcessor extends Predicate<String>, Function<String, Instant> {
}
