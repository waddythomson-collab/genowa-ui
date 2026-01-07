package genowa.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger
{
    private static Logger instance;
    private PrintWriter logFile;
    private LogLevel currentLevel;
    private final Object lock = new Object();

    private Logger()
    {
        this.currentLevel = LogLevel.INFO;
    }

    public static Logger getInstance()
    {
        if (instance == null)
        {
            synchronized (Logger.class)
            {
                if (instance == null)
                {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }

    public void setLogFile(String filename) throws IOException
    {
        synchronized (lock)
        {
            if (logFile != null)
            {
                logFile.close();
            }
            logFile = new PrintWriter(new FileWriter(filename, true));
        }
    }

    public void setLogLevel(LogLevel level)
    {
        synchronized (lock)
        {
            this.currentLevel = level;
        }
    }

    public void log(LogLevel level, String message)
    {
        if (level.ordinal() < currentLevel.ordinal())
            return;

        synchronized (lock)
        {
            String timestamp = getTimestamp();
            String levelStr = getLevelString(level);
            String logMessage = timestamp + " [" + levelStr + "] " + message + "\n";

            if (logFile != null)
            {
                logFile.print(logMessage);
                logFile.flush();
            }
            System.err.print(logMessage);
        }
    }

    public void debug(String message)
    {
        log(LogLevel.DEBUG, message);
    }

    public void info(String message)
    {
        log(LogLevel.INFO, message);
    }

    public void warning(String message)
    {
        log(LogLevel.WARNING, message);
    }

    public void error(String message)
    {
        log(LogLevel.ERROR, message);
    }

    private String getTimestamp()
    {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String getLevelString(LogLevel level)
    {
        switch (level)
        {
            case DEBUG:
                return "DEBUG";
            case INFO:
                return "INFO";
            case WARNING:
                return "WARNING";
            case ERROR:
                return "ERROR";
            default:
                return "UNKNOWN";
        }
    }
}

