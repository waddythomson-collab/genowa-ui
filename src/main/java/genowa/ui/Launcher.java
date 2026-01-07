package genowa.ui;

/**
 * Launcher class to bypass JavaFX module system check.
 * JavaFX 11+ requires module path when running Application directly.
 * This workaround launches from a non-Application class.
 */
public class Launcher {
    public static void main(String[] args) {
        GenowaApp.main(args);
    }
}
