package genowa.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * Element Value codes (1-27) with labels that vary by LevelType.
 * 
 * From WABSTBRL.RC:
 * - Premium tables have element values like PREMIUM FIELD, TRANS PREMIUM
 * - Form tables have FORMS SEQ NUMBER instead
 * - Rating tables have PERIL CODE at position 6
 * - Support tables are mostly FUTURE USE placeholders
 */
public enum ElementValue {
    EV_01("01"),
    EV_02("02"),
    EV_03("03"),
    EV_04("04"),
    EV_05("05"),
    EV_06("06"),
    EV_07("07"),
    EV_08("08"),
    EV_09("09"),
    EV_10("10"),
    EV_11("11"),
    EV_12("12"),
    EV_13("13"),
    EV_14("14"),
    EV_15("15"),
    EV_16("16"),
    EV_17("17"),
    EV_18("18"),
    EV_19("19"),
    EV_20("20"),
    EV_21("21"),
    EV_22("22"),
    EV_23("23"),
    EV_24("24"),
    EV_25("25"),
    EV_26("26"),
    EV_27("27");

    private final String code;
    
    // Labels by LevelType
    private static final Map<LevelType, String[]> LABELS = new EnumMap<>(LevelType.class);
    
    static {
        LABELS.put(LevelType.PREMIUM, new String[] {
            "PREMIUM TABLE",      // 01
            "PRIMARY BREAK",      // 02
            "COV SEQ NUMBER",     // 03
            "COVERAGE CODE",      // 04
            "INSURANCE LINE CD",  // 05
            "PRM OVERRIDE IND",   // 06
            "PREMIUM FIELD",      // 07
            "TRANS PREMIUM",      // 08
            "COV CALC FACTOR",    // 09
            "REASON AMENDED",     // 10
            "SECONDARY BREAK",    // 11
            "TERTIARY BREAK",     // 12
            "QUARTENARY BREAK",   // 13
            "RATE BOOK DATE",     // 14
            "QUINTET BREAK",      // 15
            "PERIL CODE",         // 16
            "SEXENNIAL BREAK",    // 17
            "SEPTENARY BREAK",    // 18
            "OCTOGON BREAK",      // 19
            "OBJECT KEY 1",       // 20
            "OBJECT KEY 2",       // 21
            "TECH KEY 1",         // 22
            "TECH KEY 2",         // 23
            "TECH KEY 3",         // 24
            "TECH KEY 4",         // 25
            "TECH KEY 5",         // 26
            "TECH KEY 6"          // 27
        });
        
        LABELS.put(LevelType.FORM, new String[] {
            "FORM TABLE",         // 01
            "PRIMARY BREAK",      // 02
            "COV SEQ NUMBER",     // 03
            "COVERAGE CODE",      // 04
            "INSURANCE LINE CD",  // 05
            "FORMS SEQ NUMBER",   // 06
            "FUTURE USE 2",       // 07
            "FUTURE USE 3",       // 08
            "FUTURE USE 4",       // 09
            "REASON AMENDED",     // 10
            "SECONDARY BREAK",    // 11
            "TERTIARY BREAK",     // 12
            "QUARTENARY BREAK",   // 13
            "FUTURE USE 9",       // 14
            "QUINTET BREAK",      // 15
            "FUTURE USE 11",      // 16
            "SEXENNIAL BREAK",    // 17
            "SEPTENARY BREAK",    // 18
            "OCTOGON BREAK",      // 19
            "OBJECT KEY 1",       // 20
            "OBJECT KEY 2",       // 21
            "TECH KEY 1",         // 22
            "TECH KEY 2",         // 23
            "TECH KEY 3",         // 24
            "TECH KEY 4",         // 25
            "TECH KEY 5",         // 26
            "TECH KEY 6"          // 27
        });
        
        LABELS.put(LevelType.PRIMARY, new String[] {
            "RATING TABLE",       // 01
            "PRIMARY BREAK",      // 02
            "COV SEQ NUMBER",     // 03
            "COVERAGE CODE",      // 04
            "INSURANCE LINE CD",  // 05
            "PERIL CODE",         // 06
            "FUTURE USE 1",       // 07
            "FUTURE USE 2",       // 08
            "FUTURE USE 3",       // 09
            "FUTURE USE 4",       // 10
            "SECONDARY BREAK",    // 11
            "TERTIARY BREAK",     // 12
            "QUARTENARY BREAK",   // 13
            "FUTURE USE 8",       // 14
            "QUINTET BREAK",      // 15
            "FUTURE USE 10",      // 16
            "SEXENNIAL BREAK",    // 17
            "SEPTENARY BREAK",    // 18
            "OCTOGON BREAK",      // 19
            "OBJECT KEY 1",       // 20
            "OBJECT KEY 2",       // 21
            "TECH KEY 1",         // 22
            "TECH KEY 2",         // 23
            "TECH KEY 3",         // 24
            "TECH KEY 4",         // 25
            "TECH KEY 5",         // 26
            "TECH KEY 6"          // 27
        });
        
        LABELS.put(LevelType.COVERAGE, new String[] {
            "COVERAGE TABLE",     // 01
            "PRIMARY BREAK",      // 02
            "COV SEQ NUMBER",     // 03
            "COVERAGE CODE",      // 04
            "INSURANCE LINE CD",  // 05
            "FUTURE USE 1",       // 06
            "FUTURE USE 2",       // 07
            "FUTURE USE 3",       // 08
            "FUTURE USE 4",       // 09
            "FUTURE USE 5",       // 10
            "SECONDARY BREAK",    // 11
            "TERTIARY BREAK",     // 12
            "QUARTENARY BREAK",   // 13
            "FUTURE USE 9",       // 14
            "QUINTET BREAK",      // 15
            "FUTURE USE 11",      // 16
            "SEXENNIAL BREAK",    // 17
            "SEPTENARY BREAK",    // 18
            "OCTOGON BREAK",      // 19
            "OBJECT KEY 1",       // 20
            "OBJECT KEY 2",       // 21
            "TECH KEY 1",         // 22
            "TECH KEY 2",         // 23
            "TECH KEY 3",         // 24
            "TECH KEY 4",         // 25
            "TECH KEY 5",         // 26
            "TECH KEY 6"          // 27
        });
        
        LABELS.put(LevelType.SUPPORT, new String[] {
            "SUPPORT TABLE",      // 01
            "FUTURE USE 1",       // 02
            "FUTURE USE 2",       // 03
            "FUTURE USE 3",       // 04
            "FUTURE USE 4",       // 05
            "FUTURE USE 5",       // 06
            "FUTURE USE 6",       // 07
            "FUTURE USE 7",       // 08
            "FUTURE USE 8",       // 09
            "FUTURE USE 10",      // 10
            "FUTURE USE 11",      // 11
            "FUTURE USE 12",      // 12
            "FUTURE USE 13",      // 13
            "FUTURE USE 14",      // 14
            "FUTURE USE 15",      // 15
            "FUTURE USE 16",      // 16
            "FUTURE USE 17",      // 17
            "FUTURE USE 18",      // 18
            "FUTURE USE 19",      // 19
            "FUTURE USE 20",      // 20
            "FUTURE USE 21",      // 21
            "FUTURE USE 22",      // 22
            "FUTURE USE 23",      // 23
            "FUTURE USE 24",      // 24
            "FUTURE USE 25",      // 25
            "FUTURE USE 26",      // 26
            "FUTURE USE 27"       // 27
        });
    }

    ElementValue(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
    
    /**
     * Get the display label for this element value based on the level type.
     */
    public String getLabel(LevelType levelType) {
        String[] labels = LABELS.get(levelType);
        if (labels != null) {
            int index = ordinal();
            if (index < labels.length) {
                return labels[index];
            }
        }
        return "Element " + code;
    }
    
    /**
     * Get all element values that are meaningful (not FUTURE USE) for a level type.
     */
    public static ElementValue[] getActiveValues(LevelType levelType) {
        return java.util.Arrays.stream(values())
            .filter(ev -> !ev.getLabel(levelType).startsWith("FUTURE USE"))
            .toArray(ElementValue[]::new);
    }

    public static ElementValue fromCode(String code) {
        if (code == null) return null;
        String trimmed = code.trim();
        for (ElementValue ev : values()) {
            if (ev.code.equals(trimmed)) {
                return ev;
            }
        }
        // Try parsing as integer
        try {
            int num = Integer.parseInt(trimmed);
            if (num >= 1 && num <= 27) {
                return values()[num - 1];
            }
        } catch (NumberFormatException e) {
            // ignore
        }
        return null;
    }
    
    @Override
    public String toString() {
        return code;
    }
}
