package io.github.mjcro.anytime;

/**
 * Exception thrown trying to create instant from empty value.
 */
public class EmptyInstantException extends RuntimeException {
    EmptyInstantException() {
        super("Unable to produce instant from empty source data");
    }
}
