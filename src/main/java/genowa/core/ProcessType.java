package genowa.core;

/**
 * Process types for code generation.
 * All processes use gen_tables and gen_fields as the foundation.
 */
public enum ProcessType
{
    RATING,        // Rating engine generation
    EDITS,         // Edit/validation code generation
    UNDERWRITING,  // Underwriting code generation
    KEYSET,        // Keyset generation
    IO,            // Input/Output bulk process generation
    ISSUANCE,      // Issuance bulk process generation
    RENEWAL        // Renewal bulk process generation
}

