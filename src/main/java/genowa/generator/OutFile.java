package genowa.generator;

import genowa.util.IString;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Output file writer with COBOL formatting support.
 * Matches C++ OutFile class.
 */
public class OutFile
{
    private IString filename;
    private BufferedWriter writer;
    private boolean isOpen;
    
    // COBOL column positions
    public static final int COBOL_AREA_A = 8;      // Columns 8-11 (Area A)
    public static final int COBOL_AREA_B = 12;     // Columns 12-72 (Area B)
    public static final int COBOL_LINE_END = 72;   // Column 72 (end of program text)
    public static final int COBOL_SEQUENCE = 1;    // Columns 1-6 (sequence numbers)
    public static final int COBOL_INDICATOR = 7;   // Column 7 (indicator area)
    
    public OutFile(IString outputPath)
    {
        this.filename = outputPath;
        String pathStr = outputPath != null ? outputPath.str() : "";
        
        try
        {
            // Ensure parent directories exist
            Path path = Paths.get(pathStr);
            Path parent = path.getParent();
            if (parent != null)
            {
                Files.createDirectories(parent);
            }
            
            writer = new BufferedWriter(new FileWriter(pathStr));
            isOpen = true;
            System.out.println("[DEBUG] Opened output file: " + pathStr);
        }
        catch (IOException e)
        {
            System.err.println("[ERROR] Failed to open output file: " + pathStr + " - " + e.getMessage());
            throw new RuntimeException("Failed to open output file: " + pathStr, e);
        }
    }
    
    public OutFile(String filePath)
    {
        this(new IString(filePath));
    }
    
    /**
     * Open the file for writing (if not already open).
     */
    public boolean open()
    {
        if (isOpen && writer != null)
        {
            return true;
        }
        
        String pathStr = filename != null ? filename.str() : "";
        try
        {
            // Ensure parent directories exist
            Path path = Paths.get(pathStr);
            Path parent = path.getParent();
            if (parent != null)
            {
                Files.createDirectories(parent);
            }
            
            writer = new BufferedWriter(new FileWriter(pathStr));
            isOpen = true;
            System.out.println("[DEBUG] Opened output file: " + pathStr);
            return true;
        }
        catch (IOException e)
        {
            System.err.println("[ERROR] Failed to open output file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Write a line to the output file.
     */
    public void writeLine(String line)
    {
        if (!isOpen)
        {
            if (!open())
            {
                return;
            }
        }
        
        try
        {
            writer.write(line);
            writer.newLine();
        }
        catch (IOException e)
        {
            System.err.println("[ERROR] Failed to write line: " + e.getMessage());
        }
    }

    /**
     * Write a line to the output file (IString overload).
     */
    public void writeLine(IString line)
    {
        if (line != null)
        {
            writeLine(line.str());
        }
    }
    
    /**
     * Write text without newline.
     */
    public void write(String text)
    {
        if (!isOpen)
        {
            if (!open())
            {
                return;
            }
        }
        
        try
        {
            writer.write(text);
        }
        catch (IOException e)
        {
            System.err.println("[ERROR] Failed to write: " + e.getMessage());
        }
    }
    
    /**
     * Write text without newline (IString overload).
     */
    public void write(IString text)
    {
        if (text != null)
        {
            write(text.str());
        }
    }
    
    /**
     * Write a COBOL-formatted line (with proper column positioning).
     */
    public void writeCobolLine(String content, int startColumn)
    {
        StringBuilder line = new StringBuilder();
        
        // Pad to start column
        for (int i = 0; i < startColumn - 1; i++)
        {
            line.append(' ');
        }
        
        // Add content (truncate at column 72)
        int maxLength = COBOL_LINE_END - startColumn + 1;
        if (content.length() > maxLength)
        {
            line.append(content.substring(0, maxLength));
        }
        else
        {
            line.append(content);
        }
        
        writeLine(line.toString());
    }
    
    /**
     * Write a COBOL comment line.
     */
    public void writeCobolComment(String comment)
    {
        StringBuilder line = new StringBuilder();
        
        // 6 spaces for sequence area
        line.append("      ");
        
        // Asterisk in indicator column
        line.append('*');
        
        // Comment text
        line.append(comment);
        
        writeLine(line.toString());
    }
    
    /**
     * Write content in Area A (columns 8-11).
     */
    public void writeAreaA(String content)
    {
        writeCobolLine(content, COBOL_AREA_A);
    }
    
    /**
     * Write content in Area B (columns 12-72).
     */
    public void writeAreaB(String content)
    {
        writeCobolLine(content, COBOL_AREA_B);
    }
    
    /**
     * Flush the output buffer.
     */
    public void flush()
    {
        if (writer != null)
        {
            try
            {
                writer.flush();
                System.out.println("[DEBUG] Flushed output file: " + (filename != null ? filename.str() : ""));
            }
            catch (IOException e)
            {
                System.err.println("[ERROR] Failed to flush: " + e.getMessage());
            }
        }
    }
    
    /**
     * Close the file.
     */
    public void close()
    {
        if (writer != null)
        {
            try
            {
                writer.close();
                System.out.println("[DEBUG] Closed output file: " + (filename != null ? filename.str() : ""));
            }
            catch (IOException e)
            {
                System.err.println("[ERROR] Failed to close: " + e.getMessage());
            }
            writer = null;
        }
        isOpen = false;
    }
    
    /**
     * Get the file path.
     */
    public String getFilePath()
    {
        return filename != null ? filename.str() : "";
    }
    
    /**
     * Get the filename (IString).
     */
    public IString getFilename()
    {
        return filename != null ? filename : new IString("");
    }
    
    /**
     * Check if file is open.
     */
    public boolean isOpen()
    {
        return isOpen;
    }
}
