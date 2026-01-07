package genowa.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Token class - represents a parsed token from a template line.
 * Matches C++ Token class, maintaining IString usage.
 */
public class Token
{
    public enum Type
    {
        TEXT,
        TRIGGER,
        VARIABLE
    }

    private Type type;
    private IString value;
    private IString tokenDes;
    private IString name;
    private List<IString> parms;
    private int numberOfParms;
    private boolean parmsTrue;

    public Token()
    {
        this.type = Type.TEXT;
        this.value = new IString("");
        this.tokenDes = new IString("");
        this.name = new IString("");
        this.numberOfParms = 0;
        this.parmsTrue = false;
        this.parms = new ArrayList<>();
    }

    public Token(Type type, IString value)
    {
        this.type = type;
        this.value = value != null ? value : new IString("");
        this.tokenDes = this.value;
        this.name = new IString("");
        this.numberOfParms = 0;
        this.parmsTrue = false;
        this.parms = new ArrayList<>();
    }

    public Token(IString rawValue)
    {
        this.type = Type.TEXT;
        this.value = rawValue != null ? rawValue : new IString("");
        this.tokenDes = this.value;
        this.name = new IString("");
        this.numberOfParms = 0;
        this.parmsTrue = false;
        this.parms = new ArrayList<>();
        set(rawValue != null ? rawValue : new IString(""));
    }

    public Type getType()
    {
        return type;
    }

    public IString getValue()
    {
        return value;
    }

    public IString getRaw()
    {
        return tokenDes;
    }

    public boolean isTrigger()
    {
        return type == Type.TRIGGER;
    }

    public boolean isVariable()
    {
        return type == Type.VARIABLE;
    }

    public boolean isText()
    {
        return type == Type.TEXT;
    }

    public IString getName()
    {
        return name;
    }

    public int getNumberOfParms()
    {
        return numberOfParms;
    }

    public IString getTokenDes()
    {
        return tokenDes;
    }

    public IString getTrigger()
    {
        if (!isTrigger())
        {
            return new IString("");
        }

        int start = 1; // skip $ or &
        int end = value.find('|', start);
        if (end == IString.npos)
        {
            return new IString("");
        }

        return value.substr(start, end - start);
    }

    public IString getParmsAsString()
    {
        IString result = new IString("");
        for (int i = 0; i < parms.size(); i++)
        {
            if (i > 0)
            {
                result = result.add(new IString(" "));
            }
            result = result.add(parms.get(i));
        }
        return result.trim();
    }

    public List<IString> getParms()
    {
        return new ArrayList<>(parms);
    }

    public boolean hasParms()
    {
        return parmsTrue;
    }

    private void set(IString name)
    {
        setTokenDes(name);
        IString token;

        int parenPos = name.find(new IString("("));
        if (parenPos != IString.npos)
        {
            token = name.substr(0, parenPos).add(new IString("|"));

            int closeParen = name.find(new IString(")"));
            IString parmStr = name.substr(parenPos + 1, closeParen - parenPos - 1);
            setParms(parmStr);
            this.parmsTrue = true;
        }
        // Handle conditional triggers with |param| syntax like &<|IB08|
        else if (name.length() > 3 && name.charAt(1) == '<' && name.charAt(2) == '|' && name.charAt(name.length() - 1) == '|')
        {
            // Extract the parameter from &<|param|
            IString param = name.substr(3, name.length() - 4); // Everything between the two |
            setParms(param);
            this.parmsTrue = true;
            token = name.substr(0, 3); // Just &<|
        }
        // Handle negative conditional triggers with |param| syntax like &!|AI02|
        else if (name.length() > 3 && name.charAt(1) == '!' && name.charAt(2) == '|' && name.charAt(name.length() - 1) == '|')
        {
            // Extract the parameter from &!|param|
            IString param = name.substr(3, name.length() - 4); // Everything between the two |
            setParms(param);
            this.parmsTrue = true;
            token = name.substr(0, 3); // Just &!|
        }
        else
        {
            token = name;
        }

        setName(token);

        // Try to determine type from the raw value
        if (name.startsWith(new IString("$")) || name.startsWith(new IString("&")))
        {
            type = Type.TRIGGER;
        }
    }

    private void setParms(IString strParms)
    {
        this.numberOfParms = 0;
        parms.clear();

        if (strParms.find(new IString(",")) != IString.npos)
        {
            List<IString> parts = strParms.split(',');
            for (IString part : parts)
            {
                parms.add(part.trim());
                this.numberOfParms++;
            }
        }
        else
        {
            parms.add(strParms);
            this.numberOfParms = 1;
        }
    }

    private void setTokenDes(IString tokenDes)
    {
        this.tokenDes = tokenDes;
    }

    private void setName(IString name)
    {
        this.name = name;
    }
}
