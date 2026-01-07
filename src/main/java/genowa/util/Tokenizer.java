package genowa.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Tokenizer class - static utility for tokenizing lines.
 * Matches C++ Tokenizer class, maintaining IString usage.
 */
public class Tokenizer
{
    public static List<Token> tokenize(IString line)
    {
        List<Token> tokens = new ArrayList<>();
        int pos = 0;
        IString currentText = new IString("");

        while (pos < line.length())
        {
            if (line.charAt(pos) == '$')
            {
                // Add any accumulated text
                if (!currentText.empty())
                {
                    tokens.add(new Token(Token.Type.TEXT, currentText));
                    currentText = new IString("");
                }

                // Find the end of the keyword/variable
                int end = pos + 1;
                while (end < line.length() && line.charAt(end) != '$')
                {
                    end++;
                }

                if (end < line.length())
                {
                    IString token = line.substr(pos + 1, end - pos - 1);
                    if (token.startsWith(new IString("KEYWORD:")))
                    {
                        tokens.add(new Token(Token.Type.TRIGGER, token.substr(8)));
                    }
                    else
                    {
                        tokens.add(new Token(Token.Type.VARIABLE, token));
                    }
                    pos = end + 1;
                }
                else
                {
                    // Unterminated token, treat as text
                    currentText = currentText.add(new IString(line.charAt(pos)));
                    pos++;
                }
            }
            else
            {
                currentText = currentText.add(new IString(line.charAt(pos)));
                pos++;
            }
        }

        // Add any remaining text
        if (!currentText.empty())
        {
            tokens.add(new Token(Token.Type.TEXT, currentText));
        }

        return tokens;
    }
}
