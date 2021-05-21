package bdtc.transformer;

import bdtc.Interactions;
import bdtc.Types;
import lombok.extern.log4j.Log4j;
import org.apache.flume.Event;
import org.apache.ignite.stream.flume.EventTransformer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * MapperEventTransformer class that implements the EventTransformer interface for input types data
 */
@Log4j
public class MapperEventTransformer implements EventTransformer<Event, UUID, Types> {
    /**
     * Regex pattern to split input row
     */
    private final static Pattern splitStr = Pattern.compile("-");

    /**
     * Implemented method, that converts the input set of strings from flume to the ignite cache
     * Uses interactions class {@link Types}
     * @param list input rows
     * @return map of keys and values to be inserted into the cache
     */
    @Override
    public @Nullable Map<UUID, Types> transform(List<Event> list) {
        Map<UUID, Types> map = new HashMap<>();
        String[] splitData;
        try {
            for (Event event : list) {
                String row = new String(event.getBody());
                splitData = splitStr.split(row);
                if (splitData.length != 2) throw new RuntimeException("Invalid number of columns in data line");
                Types types = new Types(Byte.parseByte(splitData[0]), splitData[1]);
                map.put(UUID.randomUUID(), types);
            }
        } catch (Exception e) {
            log.info("----------------------------------------------------\nend input file(s)" +
                    "\n----------------------------------------------------");
        }
        return map;
    }
}
