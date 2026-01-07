package genowa.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Line class - represents a line of template code.
 * Matches C++ Line class, maintaining IString usage.
 */
public class Line
{
    private IString raw;

    public Line()
    {
        this.raw = new IString("");
    }

    public Line(IString line)
    {
        this.raw = (line != null) ? line.trim() : new IString("");
    }

    public Line(String line)
    {
        this.raw = (line != null) ? new IString(line).trim() : new IString("");
    }

    public IString getRaw()
    {
        return raw;
    }

    // New change method to replace a substring within the line
    public void change(IString str1, IString str2)
    {
        raw = raw.replace(str1, str2);
        raw = raw.stripTrailingSpaces();
    }

    public boolean isEmpty()
    {
        return raw.trim().empty();
    }

    public boolean hasLength()
    {
        return raw.length() > 0;
    }

    public boolean isMeta()
    {
        return raw.startsWith(new IString("@")) || raw.startsWith(new IString("$$"));
    }

    public boolean isTrigger()
    {
        return raw.startsWith(new IString("$")) || raw.startsWith(new IString("&"));
    }

    public boolean shouldProcess()
    {
        return isTrigger() || isMeta();
    }

    public IString content()
    {
        return raw;
    }

    public IString asString()
    {
        return raw;
    }

    public IString getTrigger()
    {
        if (!isTrigger())
        {
            return new IString("");
        }

        int start = 1; // skip $ or &
        int end = raw.find('|', start);
        if (end == IString.npos)
        {
            return new IString("");
        }

        return raw.substr(start, end - start);
    }

    public List<IString> getParms()
    {
        List<IString> params = new ArrayList<>();

        int pos = raw.find('|');
        while (pos != IString.npos)
        {
            int next = raw.find('|', pos + 1);
            if (next == IString.npos)
            {
                break;
            }

            IString param = raw.substr(pos + 1, next - pos - 1);
            params.add(param.trim());

            pos = next;
        }

        return params;
    }

    public List<Token> getTokens()
    {
        List<Token> tokens = new ArrayList<>();

        if (!stringHasTokens(raw))
        {
            return tokens;
        }

        IString restOfLine = raw;

        // Process both $ and & tokens
        while ((restOfLine.contains(new IString("$")) && restOfLine.substring(restOfLine.indexOf(new IString("$"))).contains(new IString("|"))) ||
               (restOfLine.contains(new IString("&")) && restOfLine.substring(restOfLine.indexOf(new IString("&"))).contains(new IString("|"))))
        {
            int dollarPos = restOfLine.indexOf(new IString("$"));
            int ampersandPos = restOfLine.indexOf(new IString("&"));

            // Choose the first occurrence of either $ or &
            int triggerStart;
            if (dollarPos == IString.npos)
            {
                triggerStart = ampersandPos;
            }
            else if (ampersandPos == IString.npos)
            {
                triggerStart = dollarPos;
            }
            else
            {
                triggerStart = (dollarPos < ampersandPos) ? dollarPos : ampersandPos;
            }

            restOfLine = restOfLine.substring(triggerStart);

            // For conditional triggers like &<|IB08|, we need to include the parameter in the token
            // For regular triggers like &PGM|param|, we just need the trigger part
            IString tokenDes;
            if (restOfLine.length() > 2 && restOfLine.charAt(1) == '<' && restOfLine.charAt(2) == '|')
            {
                // Special case for conditional triggers: &<|param|
                int endPos = restOfLine.indexOf(new IString("|"), 3); // Find the closing |
                if (endPos != IString.npos)
                {
                    tokenDes = restOfLine.substring(0, endPos + 1); // Include the full &<|param|
                    restOfLine = restOfLine.substring(endPos + 1);
                }
                else
                {
                    int pipePos = restOfLine.indexOf(new IString("|"));
                    tokenDes = restOfLine.substring(0, pipePos + 1);
                    restOfLine = restOfLine.substring(pipePos + 1);
                }
            }
            else if (restOfLine.length() > 2 && restOfLine.charAt(1) == '!' && restOfLine.charAt(2) == '|')
            {
                // Special case for negative conditional triggers: &!|param|
                int endPos = restOfLine.indexOf(new IString("|"), 3); // Find the closing |
                if (endPos != IString.npos)
                {
                    tokenDes = restOfLine.substring(0, endPos + 1); // Include the full &!|param|
                    restOfLine = restOfLine.substring(endPos + 1);
                }
                else
                {
                    int pipePos = restOfLine.indexOf(new IString("|"));
                    tokenDes = restOfLine.substring(0, pipePos + 1);
                    restOfLine = restOfLine.substring(pipePos + 1);
                }
            }
            else
            {
                // Regular triggers: &TRIGGER|param|
                int pipePos = restOfLine.indexOf(new IString("|"));
                tokenDes = restOfLine.substring(0, pipePos + 1);
                if (pipePos < restOfLine.length() - 1)
                {
                    restOfLine = restOfLine.substring(pipePos + 1);
                }
                else
                {
                    restOfLine = new IString("");
                }
            }

            Token token = new Token(tokenDes);
            tokens.add(token);
        }

        return tokens;
    }

    private boolean stringHasTokens(IString str)
    {
        return ((str.contains(new IString("$")) && str.contains(new IString("|"))) || 
                (str.contains(new IString("&")) && str.contains(new IString("|"))));
    }
}
