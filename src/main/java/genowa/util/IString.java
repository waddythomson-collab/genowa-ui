package genowa.util;

import java.util.ArrayList;
import java.util.List;

/**
 * IString - Insurance String class.
 * Maintains C++ naming conventions and API.
 * Wraps Java String to provide IString interface.
 */
public class IString
{
    public static final int npos = -1;

    private String data;

    public IString()
    {
        this.data = "";
    }

    public IString(IString other)
    {
        this.data = other != null ? other.data : "";
    }

    public IString(String str)
    {
        this.data = str != null ? str : "";
    }

    public IString(int value)
    {
        this.data = String.valueOf(value);
    }

    public IString(long value)
    {
        this.data = String.valueOf(value);
    }

    public IString(double value)
    {
        this.data = String.valueOf(value);
    }

    public IString(char c)
    {
        this.data = String.valueOf(c);
    }

    public int length()
    {
        return data.length();
    }

    public String c_str()
    {
        return data;
    }

    public String str()
    {
        return data;
    }

    public char charAt(int index)
    {
        if (index >= 0 && index < data.length())
        {
            return data.charAt(index);
        }
        throw new IndexOutOfBoundsException("Index out of bounds: " + index);
    }

    public IString substr(int pos, int len)
    {
        if (pos < 0)
        {
            pos = 0;
        }
        if (pos >= data.length())
        {
            return new IString("");
        }
        int endPos = Math.min(pos + len, data.length());
        return new IString(data.substring(pos, endPos));
    }

    public IString substr(int pos)
    {
        if (pos < 0)
        {
            pos = 0;
        }
        if (pos >= data.length())
        {
            return new IString("");
        }
        return new IString(data.substring(pos));
    }

    public int find(IString str)
    {
        if (str == null)
        {
            return npos;
        }
        int index = data.indexOf(str.data);
        return index >= 0 ? index : npos;
    }

    public boolean contains(IString str)
    {
        if (str == null)
        {
            return false;
        }
        return data.contains(str.data);
    }

    public IString toUpper()
    {
        return new IString(data.toUpperCase());
    }

    public IString toLower()
    {
        return new IString(data.toLowerCase());
    }

    public boolean startsWith(IString prefix)
    {
        if (prefix == null)
        {
            return false;
        }
        if (prefix.length() > length())
        {
            return false;
        }
        return data.startsWith(prefix.data);
    }

    public boolean endsWith(IString suffix)
    {
        if (suffix == null)
        {
            return false;
        }
        if (suffix.length() > length())
        {
            return false;
        }
        return data.endsWith(suffix.data);
    }

    public IString trim()
    {
        return new IString(data.trim());
    }

    public IString stripTrailingSpaces()
    {
        int end = data.length() - 1;
        while (end >= 0 && Character.isWhitespace(data.charAt(end)))
        {
            end--;
        }
        if (end < 0)
        {
            return new IString("");
        }
        return new IString(data.substring(0, end + 1));
    }

    public List<IString> split(char delimiter)
    {
        List<IString> tokens = new ArrayList<>();
        String[] parts = data.split(String.valueOf(delimiter), -1);
        for (String part : parts)
        {
            tokens.add(new IString(part));
        }
        return tokens;
    }

    public IString assign(IString other)
    {
        this.data = other != null ? other.data : "";
        return this;
    }

    public IString add(IString other)
    {
        return new IString(data + (other != null ? other.data : ""));
    }

    public IString addAssign(IString other)
    {
        data += (other != null ? other.data : "");
        return this;
    }

    public boolean equals(IString other)
    {
        if (other == null)
        {
            return false;
        }
        return data.equals(other.data);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof IString)
        {
            return equals((IString) obj);
        }
        if (obj instanceof String)
        {
            return data.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return data.hashCode();
    }

    public boolean lessThan(IString other)
    {
        if (other == null)
        {
            return false;
        }
        return data.compareTo(other.data) < 0;
    }

    public boolean empty()
    {
        return data.isEmpty();
    }

    public int find(char ch, int pos)
    {
        int index = data.indexOf(ch, pos);
        return index >= 0 ? index : npos;
    }

    public int find(char ch)
    {
        return find(ch, 0);
    }

    public int indexOf(IString str, int pos)
    {
        if (str == null)
        {
            return npos;
        }
        int index = data.indexOf(str.data, pos);
        return index >= 0 ? index : npos;
    }

    public int indexOf(IString str)
    {
        return indexOf(str, 0);
    }

    public int indexOf(char ch, int pos)
    {
        int index = data.indexOf(ch, pos);
        return index >= 0 ? index : npos;
    }

    public int indexOf(char ch)
    {
        return indexOf(ch, 0);
    }

    public IString substring(int pos, int len)
    {
        return substr(pos, len);
    }

    public IString substring(int pos)
    {
        return substr(pos);
    }

    public IString replace(IString from, IString to)
    {
        if (from == null || from.data.isEmpty())
        {
            return new IString(this);
        }
        String toStr = to != null ? to.data : "";
        return new IString(data.replace(from.data, toStr));
    }

    public IString replaceAll(IString from, IString to)
    {
        if (from == null || from.data.isEmpty())
        {
            return new IString(this);
        }
        String toStr = to != null ? to.data : "";
        return new IString(data.replace(from.data, toStr));
    }

    @Override
    public String toString()
    {
        return data;
    }

    // Operator-like methods for Java
    public static IString add(IString lhs, IString rhs)
    {
        if (lhs == null && rhs == null)
        {
            return new IString("");
        }
        if (lhs == null)
        {
            return new IString(rhs);
        }
        if (rhs == null)
        {
            return new IString(lhs);
        }
        return lhs.add(rhs);
    }

    public static IString add(String lhs, IString rhs)
    {
        return add(new IString(lhs), rhs);
    }
}

