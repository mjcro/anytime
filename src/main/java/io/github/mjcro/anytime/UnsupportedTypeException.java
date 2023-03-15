package io.github.mjcro.anytime;

/**
 * Exception thrown attempting read data from unsupported class type.
 */
public class UnsupportedTypeException extends RuntimeException {
    UnsupportedTypeException(Class<?> clazz) {
        super("Unable to extract time from " + clazz.getName());
    }
}
