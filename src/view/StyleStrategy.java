package view;

/**
 * Strategy interface for board styling.
 */
public interface StyleStrategy {
    default String getName() {
        return getClass().getSimpleName();
    }
}
