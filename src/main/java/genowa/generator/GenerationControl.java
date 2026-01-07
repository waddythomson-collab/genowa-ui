package genowa.generator;

import genowa.core.ProcessType;
import genowa.util.IString;
import genowa.util.Line;
import genowa.util.Token;
import genowa.generator.trigger.Trigger;
import genowa.generator.trigger.TriggerRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls the generation process by orchestrating template processing.
 * Matches C++ GenerationControl class.
 */
public class GenerationControl
{
    // Template and file handling
    private InFile templateFile;
    private OutFile outFile;

    // Line and token handling
    private Line currentLine;
    private List<Token> currentTokens;

    // Core objects
    private GenCore genCore;
    private GenCore parentGenCore;
    private TriggerRegistry triggerRegistry;

    // Output file name
    private IString outputFileName;

    public GenerationControl()
    {
        this.genCore = new GenCore();
        this.triggerRegistry = new TriggerRegistry();
        this.currentLine = new Line(new IString(""));
        this.currentTokens = new ArrayList<>();
    }

    // Main generation method
    public void generate(GenCore genCore)
    {
        System.out.println("[DEBUG] GenerationControl::generate() called");
        System.out.println("[DEBUG] Current genCore: " + (this.genCore != null ? "exists" : "null"));
        System.out.println("[DEBUG] Passed genCore: " + (genCore != null ? "exists" : "null"));

        if (this.genCore != null)
        {
            System.out.println("[DEBUG] Current genCore has DataAccess: " + (this.genCore.getDataAccess() != null ? "yes" : "no"));
        }

        if (genCore != null)
        {
            System.out.println("[DEBUG] Passed genCore has DataAccess: " + (genCore.getDataAccess() != null ? "yes" : "no"));
        }

        // Don't replace the genCore if it already has DataAccess set up
        if (this.genCore == null || this.genCore.getDataAccess() == null)
        {
            this.genCore = genCore;
            System.out.println("[DEBUG] Replaced genCore");
        }
        else
        {
            System.out.println("[DEBUG] Kept existing genCore");
        }

        // Ensure the OutFile is set on the genCore
        if (this.genCore != null && this.outFile != null)
        {
            this.genCore.setOutFile(this.outFile);
            System.out.println("[DEBUG] Set OutFile on genCore");
        }

        processTpl();
    }

    // Template processing
    public void load(InFile inFile, OutFile outFile)
    {
        this.templateFile = inFile;
        this.outFile = outFile;
        if (genCore != null)
        {
            genCore.setOutFile(outFile);
        }
    }

    public void setTemplate(InFile templateFile)
    {
        this.templateFile = templateFile;
    }

    // Line and token processing
    public void setLine(Line line)
    {
        this.currentLine = line;
        if (genCore != null)
        {
            genCore.setLine(line);
        }
    }

    public void processTokens()
    {
        boolean markType = false;
        boolean lineReplacingTriggerHandled = false;
        currentTokens = currentLine.getTokens();

        if (currentTokens.isEmpty())
        {
            if (genCore != null && genCore.getOutFile() != null)
            {
                genCore.getOutFile().writeLine(currentLine.content());
            }
            return;
        }

        processTokens(currentTokens, markType);
    }

    // File handling
    public void setOutFile(OutFile outFile)
    {
        this.outFile = outFile;
        if (genCore != null)
        {
            genCore.setOutFile(outFile);
        }
    }

    public OutFile getOutFile()
    {
        return outFile;
    }

    // Insurance line handling
    public IString getInsLine()
    {
        return genCore != null ? genCore.getInsLineCd() : new IString("");
    }

    public void setInsLine(IString insLine)
    {
        if (genCore != null)
        {
            genCore.setInsLineCd(insLine);
        }
    }

    // Template type handling
    public boolean isMainTpl()
    {
        return genCore != null ? genCore.isMainTpl() : false;
    }

    public void setMainTpl(boolean mainTpl)
    {
        if (genCore != null)
        {
            genCore.setMainTpl(mainTpl);
        }
    }

    // Process type handling
    public ProcessType getProcType()
    {
        return genCore != null ? genCore.getProcType() : ProcessType.RATING;
    }

    public void setProcType(ProcessType procType)
    {
        if (genCore != null)
        {
            genCore.setProcType(procType);
        }
    }

    // GenCore handling
    public GenCore getGenCore()
    {
        return genCore;
    }

    public void setGenCore(GenCore core)
    {
        this.genCore = core;
    }

    // Parent GenCore for sub-templates
    public GenCore getParentGenCore()
    {
        return parentGenCore;
    }

    public void setParentGenCore(GenCore parent)
    {
        this.parentGenCore = parent;
    }

    // Trigger registry
    public void setTriggerRegistry(TriggerRegistry registry)
    {
        this.triggerRegistry = registry;
    }

    public TriggerRegistry getTriggerRegistry()
    {
        return triggerRegistry;
    }

    // Token replacement methods
    public void replaceTokenWith(IString replacement)
    {
        // Replace the current token in the current line
        if (genCore != null && genCore.getToken() != null && genCore.getToken().isTrigger())
        {
            IString tokenToReplace = genCore.getToken().getRaw();

            // Use Line's change method to replace the token
            currentLine.change(tokenToReplace, replacement);
            if (genCore != null)
            {
                genCore.setLine(currentLine);
            }
            System.out.println("[DEBUG] Replaced token " + tokenToReplace.str() + " with " + replacement.str());
        }
    }

    public void deleteCurrentLine()
    {
        // This method would mark the current line for deletion
        // For now, we'll just skip writing it to the output file
        // The line will not be written in the processTokens method
        currentLine = new Line(new IString(""));
    }

    public void updateCurrentLine(IString newContent)
    {
        currentLine = new Line(newContent);
        if (genCore != null)
        {
            genCore.setLine(currentLine);
        }
    }

    public IString getCurrentLineContent()
    {
        return currentLine.content();
    }

    private void processTpl()
    {
        System.out.println("[DEBUG] GenerationControl::processTpl() called");

        if (templateFile == null)
        {
            System.out.println("[DEBUG] No template file set");
            return;
        }

        if (!templateFile.hasNextLine())
        {
            System.out.println("[DEBUG] Template file has no lines");
            return;
        }

        System.out.println("[DEBUG] Processing template file");

        int lineCount = 0;
        while (templateFile.hasNextLine())
        {
            Line line = templateFile.getNextLine();
            lineCount++;
            IString lineContent = line.content();
            String preview = lineContent.length() > 50 ? lineContent.substr(0, 50).str() + "..." : lineContent.str();
            System.out.println("[DEBUG] Processing line " + lineCount + ": " + preview);
            setLine(line);
            processTokens();
        }

        System.out.println("[DEBUG] Processed " + lineCount + " lines total");

        if (isMainTpl() && genCore != null && genCore.getOutFile() != null)
        {
            genCore.getOutFile().flush();
            System.out.println("[DEBUG] Flushed output file");
        }
    }

    private void processTokens(List<Token> tokens, boolean markType)
    {
        System.out.println("[DEBUG] processTokens called with " + tokens.size() + " tokens");

        // Process the line iteratively until no more tokens are found
        int maxIterations = 10; // Prevent infinite loops
        int iteration = 0;
        boolean lineReplacingTriggerHandled = false;

        while (iteration < maxIterations)
        {
            iteration++;
            IString lineContent = currentLine.content();
            String preview = lineContent.length() > 50 ? lineContent.substr(0, 50).str() + "..." : lineContent.str();
            System.out.println("[DEBUG] Processing line iteration " + iteration + ": " + preview);

            // Check if the current line has any tokens by checking if it contains trigger patterns
            if (!lineContent.contains(new IString("&")) || !lineContent.contains(new IString("|")))
            {
                System.out.println("[DEBUG] No more tokens found, line is complete");
                break;
            }

            // Get tokens from the current line
            List<Token> currentTokens = currentLine.getTokens();
            System.out.println("[DEBUG] Found " + currentTokens.size() + " tokens in iteration " + iteration);

            boolean tokensProcessed = false;

            for (Token token : currentTokens)
            {
                IString tokenRaw = token.getRaw();
                System.out.println("[DEBUG] Processing token: " + tokenRaw.str());

                if (genCore != null)
                {
                    genCore.setToken(token);
                }

                try
                {
                    IString tokenName = token.getRaw();
                    int pipePos = tokenName.find(new IString("|"));
                    if (pipePos != IString.npos)
                    {
                        // For conditional triggers like &<|IB08|, the trigger name is just the character after &
                        // For regular triggers like &PGM|, the trigger name is everything between & and |
                        IString triggerName;
                        if (tokenName.length() > 2 && tokenName.charAt(1) == '<' && tokenName.charAt(2) == '|')
                        {
                            // Special case for conditional triggers: &<|param|
                            triggerName = tokenName.substr(1, 1).toUpper(); // Just the '<'
                        }
                        else if (tokenName.length() > 2 && tokenName.charAt(1) == '!' && tokenName.charAt(2) == '|')
                        {
                            // Special case for negative conditional triggers: &!|param|
                            triggerName = tokenName.substr(1, 1).toUpper(); // Just the '!'
                        }
                        else
                        {
                            // Regular triggers: &TRIGGER|param|
                            triggerName = tokenName.substr(1, pipePos - 1).toUpper();
                        }
                        System.out.println("[DEBUG] Found trigger: " + triggerName.str());

                        if (templateFile != null)
                        {
                            TriggerRegistry registry = templateFile.getTriggerRegistry();
                            if (registry == null)
                            {
                                registry = this.triggerRegistry;
                            }
                            
                            genowa.generator.trigger.Trigger trigger = registry.getTrigger(triggerName.str());
                            if (trigger != null)
                            {
                                System.out.println("[DEBUG] Created trigger for: " + triggerName.str());
                                
                                // Convert IString parameters to String array
                                List<IString> parms = token.getParms();
                                String[] params = new String[parms.size()];
                                for (int i = 0; i < parms.size(); i++)
                                {
                                    params[i] = parms.get(i).str();
                                }
                                
                                String result = trigger.process(genCore, params);

                                // Check if this trigger replaces the entire line
                                if (trigger.replacesLine())
                                {
                                    System.out.println("[DEBUG] Trigger replaces entire line");
                                    markType = false;
                                    lineReplacingTriggerHandled = true;
                                    tokensProcessed = true;
                                    break;
                                }
                                else
                                {
                                    System.out.println("[DEBUG] Trigger replaces token only");
                                    if (result != null)
                                    {
                                        replaceTokenWith(new IString(result));
                                    }
                                    markType = true;
                                    tokensProcessed = true;
                                    // Continue processing other tokens in this iteration
                                }
                            }
                            else
                            {
                                System.out.println("[DEBUG] No trigger handler found for: " + triggerName.str());
                                // Logger warning would go here
                            }
                        }
                    }
                    else
                    {
                        System.out.println("[DEBUG] Token has no pipe, not a trigger");
                    }
                }
                catch (Exception ex)
                {
                    System.out.println("[DEBUG] Exception processing token: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

            // If no tokens were processed in this iteration, break to avoid infinite loop
            if (!tokensProcessed)
            {
                System.out.println("[DEBUG] No tokens were processed in iteration " + iteration + ", breaking");
                break;
            }
        }

        if (iteration >= maxIterations)
        {
            System.out.println("[DEBUG] WARNING: Reached maximum iterations (" + maxIterations + "), stopping token processing");
        }

        if (markType && !currentLine.isEmpty() && genCore != null && genCore.getOutFile() != null)
        {
            IString lineContent = currentLine.content();
            String preview = lineContent.length() > 50 ? lineContent.substr(0, 50).str() + "..." : lineContent.str();
            System.out.println("[DEBUG] Writing line to output (token-replacing trigger): " + preview);
            genCore.getOutFile().writeLine(currentLine.content());
        }
        else if (!lineReplacingTriggerHandled && !currentLine.isEmpty() && genCore != null && genCore.getOutFile() != null)
        {
            // No triggers or line-replacing trigger already handled output - write the line as-is
            IString lineContent = currentLine.content();
            String preview = lineContent.length() > 50 ? lineContent.substr(0, 50).str() + "..." : lineContent.str();
            System.out.println("[DEBUG] Writing line to output (no triggers): " + preview);
            genCore.getOutFile().writeLine(currentLine.content());
        }
        else if (lineReplacingTriggerHandled)
        {
            System.out.println("[DEBUG] Line-replacing trigger already handled output");
        }
        else
        {
            System.out.println("[DEBUG] Not writing line (markType=" + markType + ", lineReplacingTriggerHandled=" + lineReplacingTriggerHandled + ", isEmpty=" + currentLine.isEmpty() + ", hasOutFile=" + (genCore != null && genCore.getOutFile() != null) + ")");
        }
    }
}
