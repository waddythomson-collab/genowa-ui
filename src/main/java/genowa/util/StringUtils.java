package genowa.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class StringUtils
{
    // --- Trimming Functions ---
    public static String stripLeadingSpaces(String str)
    {
        if (str == null)
            return "";
        return str.replaceAll("^\\s+", "");
    }

    public static String stripTrailingSpaces(String str)
    {
        if (str == null)
            return "";
        return str.replaceAll("\\s+$", "");
    }

    public static String stripMultiSpaces(String str)
    {
        if (str == null)
            return "";
        return str.replaceAll("\\s{2,}", " ");
    }

    public static String strip(String str)
    {
        if (str == null)
            return "";
        return stripMultiSpaces(stripLeadingSpaces(stripTrailingSpaces(str)));
    }

    // --- Template Replacement (%1, %2, %3, etc.) ---
    public static String buildQuery(String templateQuery, List<String> values)
    {
        if (templateQuery == null)
            return "";
        String result = templateQuery;
        for (int i = 0; i < values.size(); ++i)
        {
            String placeholder = "%" + (i + 1);
            result = result.replace(placeholder, values.get(i));
        }
        return result;
    }

    // --- Split Functions ---
    public static List<String> split(String input, char delimiter)
    {
        if (input == null || input.isEmpty())
            return new ArrayList<>();
        String[] parts = input.split(Pattern.quote(String.valueOf(delimiter)));
        return new ArrayList<>(Arrays.asList(parts));
    }

    // --- Replace All ---
    public static String replaceAll(String str, String from, String to)
    {
        if (str == null)
            return "";
        return str.replace(from, to);
    }

    // --- To Upper/Lower ---
    public static String toUpperCase(String str)
    {
        return (str != null) ? str.toUpperCase() : "";
    }

    public static String toLowerCase(String str)
    {
        return (str != null) ? str.toLowerCase() : "";
    }

    // --- Starts With / Ends With ---
    public static boolean startsWith(String str, String prefix)
    {
        return str != null && str.startsWith(prefix);
    }

    public static boolean endsWith(String str, String suffix)
    {
        return str != null && str.endsWith(suffix);
    }

    // Convert a string to camel case
    public static String convertToCamelCase(String str, boolean capitalizeFirst)
    {
        if (str == null || str.isEmpty())
            return "";
        StringBuilder result = new StringBuilder();
        boolean capitalize = capitalizeFirst;
        for (int i = 0; i < str.length(); ++i)
        {
            char c = str.charAt(i);
            if (c == '_')
            {
                capitalize = true;
                continue;
            }
            if (capitalize)
            {
                result.append(Character.toUpperCase(c));
                capitalize = false;
            }
            else
            {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }

    public static String convertToCamelCase(String str)
    {
        return convertToCamelCase(str, true);
    }

    public static String convertToGetter(String str)
    {
        return "get" + convertToCamelCase(str, true);
    }

    public static String convertToSetter(String str)
    {
        return "set" + convertToCamelCase(str, true);
    }

    public static String stripView(String str)
    {
        if (str != null && str.endsWith("_VW"))
        {
            return str.substring(0, str.length() - 3);
        }
        return (str != null) ? str : "";
    }

    // --- IString versions ---
    
    public static IString stripLeadingSpaces(IString str)
    {
        if (str == null)
        {
            return new IString("");
        }
        String result = str.str().replaceAll("^\\s+", "");
        return new IString(result);
    }

    public static IString stripTrailingSpaces(IString str)
    {
        if (str == null)
        {
            return new IString("");
        }
        String result = str.str().replaceAll("\\s+$", "");
        return new IString(result);
    }

    public static IString stripMultiSpaces(IString str)
    {
        if (str == null)
        {
            return new IString("");
        }
        String result = str.str().replaceAll("\\s{2,}", " ");
        return new IString(result);
    }

    public static IString strip(IString str)
    {
        if (str == null)
        {
            return new IString("");
        }
        return str.trim();
    }

    public static IString convertToCamelCase(IString str, boolean capitalizeFirst)
    {
        if (str == null || str.empty())
        {
            return new IString("");
        }
        IString result = new IString("");
        boolean capitalize = capitalizeFirst;
        for (int i = 0; i < str.length(); ++i)
        {
            char c = str.charAt(i);
            if (c == '_')
            {
                capitalize = true;
                continue;
            }
            if (capitalize)
            {
                result = result.add(new IString(Character.toUpperCase(c)));
                capitalize = false;
            }
            else
            {
                result = result.add(new IString(Character.toLowerCase(c)));
            }
        }
        return result;
    }

    public static IString convertToCamelCase(IString str)
    {
        return convertToCamelCase(str, true);
    }

    public static IString convertToGetter(IString str)
    {
        return new IString("get").add(convertToCamelCase(str, true));
    }

    public static IString convertToSetter(IString str)
    {
        return new IString("set").add(convertToCamelCase(str, true));
    }

    public static IString stripView(IString str)
    {
        if (str == null)
        {
            return new IString("");
        }
        if (str.endsWith(new IString("_VW")))
        {
            return str.substr(0, str.length() - 3);
        }
        return str;
    }
}

