package genowa.model;

/**
 * Table level types in the Insurance Line Table Assignment hierarchy.
 * Each type has its own set of applicable Element Values.
 */
public enum LevelType {
    
    PREMIUM("$", "Premium", "Premium calculation tables"),
    FORM("F", "Form", "Form/endorsement tables"),
    PRIMARY("P", "Primary", "Primary rating tables"),
    COVERAGE("C", "Coverage", "Coverage tables"),
    SUPPORT("S", "Support", "Support/reference tables");

    private final String code;
    private final String displayName;
    private final String description;

    LevelType(String code, String displayName, String description) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }

    /**
     * Finds a LevelType by its database code.
     */
    public static LevelType fromCode(String code) {
        for (LevelType lt : values()) {
            if (lt.code.equals(code)) {
                return lt;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
