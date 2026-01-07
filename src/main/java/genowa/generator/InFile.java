package genowa.generator;

import genowa.generator.trigger.TriggerRegistry;
import genowa.util.IString;
import genowa.util.Line;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Input file (template) reader.
 * Matches C++ InFile class - reads template files line by line.
 */
public class InFile
{
    private IString filename;
    private TriggerRegistry registry;
    private BufferedReader reader;
    private boolean isOpen = false;

    public InFile(IString templatePath, TriggerRegistry reg)
    {
        this.filename = templatePath;
        this.registry = reg;
        
        System.out.println("[DEBUG] Attempting to open template file: " + (templatePath != null ? templatePath.str() : ""));
        
        try
        {
            String pathStr = templatePath != null ? templatePath.str() : "";
            Path path = Paths.get(pathStr);
            
            if (!Files.exists(path))
            {
                System.err.println("[ERROR] Failed to open template file: " + pathStr);
                throw new RuntimeException("Failed to open template file: " + pathStr);
            }
            
            reader = new BufferedReader(new FileReader(pathStr));
            isOpen = true;
            System.out.println("[DEBUG] Successfully opened template file: " + pathStr);
        }
        catch (IOException e)
        {
            System.err.println("[ERROR] Failed to open template file: " + (templatePath != null ? templatePath.str() : "") + " - " + e.getMessage());
            throw new RuntimeException("Failed to open template file", e);
        }
    }

    public boolean hasNextLine()
    {
        if (!isOpen || reader == null)
        {
            return false;
        }
        
        try
        {
            reader.mark(1);
            int nextChar = reader.read();
            reader.reset();
            return nextChar != -1;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    public Line getNextLine()
    {
        if (!isOpen || reader == null)
        {
            return new Line(new IString(""));
        }
        
        try
        {
            String lineStr = reader.readLine();
            if (lineStr != null)
            {
                return new Line(new IString(lineStr));
            }
        }
        catch (IOException e)
        {
            System.err.println("[ERROR] Failed to read line: " + e.getMessage());
        }
        
        return new Line(new IString(""));
    }

    public boolean getLine(IString line)
    {
        if (!isOpen || reader == null || line == null)
        {
            return false;
        }
        
        try
        {
            String lineStr = reader.readLine();
            if (lineStr != null)
            {
                line.assign(new IString(lineStr));
                return true;
            }
        }
        catch (IOException e)
        {
            System.err.println("[ERROR] Failed to read line: " + e.getMessage());
        }
        
        return false;
    }

    public boolean isOpen()
    {
        return isOpen && reader != null;
    }

    public IString getFilename()
    {
        return filename;
    }

    public void processTemplate(OutFile outFile)
    {
        if (!isOpen || reader == null)
        {
            return;
        }
        
        try
        {
            String lineStr;
            while ((lineStr = reader.readLine()) != null)
            {
                Line line = new Line(new IString(lineStr));
                // The processLine method is typically called by GenerationControl,
                // not directly by InFile's processTemplate.
                // For now, just write the line directly if no trigger processing is done here.
                if (outFile != null)
                {
                    outFile.writeLine(line.content());
                }
            }
        }
        catch (IOException e)
        {
            System.err.println("[ERROR] Failed to process template: " + e.getMessage());
        }
    }

    public TriggerRegistry getTriggerRegistry()
    {
        return registry;
    }

    public void close()
    {
        if (reader != null)
        {
            try
            {
                reader.close();
            }
            catch (IOException e)
            {
                // Ignore close errors
            }
            reader = null;
        }
        isOpen = false;
    }
}
