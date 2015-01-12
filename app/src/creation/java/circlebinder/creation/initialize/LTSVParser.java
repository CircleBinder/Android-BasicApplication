package circlebinder.creation.initialize;

import net.ichigotake.common.util.Optional;

import java.util.Map;

public interface LTSVParser<T> {
    
    Optional<T> parseLTSV(Map<String, String> line);
    
}
