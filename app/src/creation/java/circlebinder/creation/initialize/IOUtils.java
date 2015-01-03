package circlebinder.creation.initialize;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {
    
    private IOUtils() {
        throw new RuntimeException("Can't instate");
    }
    
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
