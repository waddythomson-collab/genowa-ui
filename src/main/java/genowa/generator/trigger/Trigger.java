package genowa.generator.trigger;

import genowa.generator.GenCore;

/**
 * Interface for template trigger handlers.
 * Matches C++ AbstractTrigger pattern.
 * 
 * Triggers process &KEYWORD| markers in templates and return
 * the replacement text.
 */
public interface Trigger
{
    /**
     * Process this trigger and return replacement text.
     * 
     * @param genCore The generation context
     * @param params  Parameters from the trigger marker (e.g., &VAR1|value|)
     * @return The text to replace the trigger marker with
     */
    String process(GenCore genCore, String[] params);
    
    /**
     * Whether this trigger replaces the entire line (vs. just the token).
     * Line-replacing triggers handle their own output.
     * 
     * @return true if this trigger replaces the whole line
     */
    default boolean replacesLine()
    {
        return false;
    }
    
    /**
     * Get the trigger keyword (e.g., "PGM", "TBL").
     * 
     * @return The keyword that activates this trigger
     */
    String getKeyword();
}
