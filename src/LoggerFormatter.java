import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LoggerFormatter extends Formatter{
    // public String format(LogRecord record) { return record.getLevel() + ": " + record.getMessage() + '\n'; }

    @Override
    public String format(LogRecord record) { return record.getMessage() + '\n'; }
}
