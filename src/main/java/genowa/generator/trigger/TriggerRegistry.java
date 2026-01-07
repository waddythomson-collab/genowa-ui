package genowa.generator.trigger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registry for trigger handlers.
 * Matches C++ TriggerRegistry pattern - registers trigger factories by keyword.
 */
public class TriggerRegistry
{
    private Map<String, Supplier<Trigger>> triggerFactories;
    private Map<String, Trigger> singletonTriggers;
    
    public TriggerRegistry()
    {
        this.triggerFactories = new HashMap<>();
        this.singletonTriggers = new HashMap<>();
    }
    
    /**
     * Register a trigger factory by keyword.
     * A new instance is created each time the trigger is requested.
     */
    public void register(String keyword, Supplier<Trigger> factory)
    {
        triggerFactories.put(keyword.toUpperCase(), factory);
    }
    
    /**
     * Register a singleton trigger instance by keyword.
     * The same instance is returned each time.
     */
    public void registerSingleton(String keyword, Trigger trigger)
    {
        singletonTriggers.put(keyword.toUpperCase(), trigger);
    }
    
    /**
     * Get a trigger by keyword.
     * Returns singleton if registered, otherwise creates new instance.
     */
    public Trigger getTrigger(String keyword)
    {
        String key = keyword.toUpperCase();
        
        // Check singletons first
        if (singletonTriggers.containsKey(key))
        {
            return singletonTriggers.get(key);
        }
        
        // Try factory
        Supplier<Trigger> factory = triggerFactories.get(key);
        if (factory != null)
        {
            return factory.get();
        }
        
        return null;
    }
    
    /**
     * Check if a trigger is registered for the keyword.
     */
    public boolean hasTrigger(String keyword)
    {
        String key = keyword.toUpperCase();
        return singletonTriggers.containsKey(key) || triggerFactories.containsKey(key);
    }
    
    /**
     * Get all registered keywords.
     */
    public java.util.Set<String> getRegisteredKeywords()
    {
        java.util.Set<String> keywords = new java.util.HashSet<>();
        keywords.addAll(triggerFactories.keySet());
        keywords.addAll(singletonTriggers.keySet());
        return keywords;
    }
}
