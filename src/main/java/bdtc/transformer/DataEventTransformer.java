package bdtc.transformer;

import bdtc.Interactions;
import lombok.extern.log4j.Log4j;
import org.apache.flume.Event;
import org.apache.ignite.stream.flume.EventTransformer;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * DataEventTransformer class that implements the EventTransformer interface for input interactions data
 */
@Log4j
public class DataEventTransformer implements EventTransformer<Event, UUID, Interactions> {
    /**
     * Regex pattern to split input row
     */
    private final static Pattern splitStr = Pattern.compile(",");

    /**
     * Datetime formatter
     */
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Implemented method, that converts the input set of strings from flume to the ignite cache
     * Uses interactions class {@link Interactions}
     * @param list input rows
     * @return map of keys and values to be inserted into the cache
     */
    @Override
    public @Nullable Map<UUID, Interactions> transform(List<Event> list) {
        Map<UUID, Interactions> map = new HashMap<>();
        String[] splitData;
        try {
            for (Event event : list) {
                String row = new String(event.getBody());
                splitData = splitStr.split(row);
                if (splitData.length != 4) throw new RuntimeException("Invalid number of columns in data line");
                Interactions inp = new Interactions(Long.parseLong(splitData[0]),
                        Long.parseLong(splitData[1]), LocalDateTime.parse(splitData[2], formatter),
                        Byte.parseByte(splitData[3]));
                map.put(UUID.randomUUID(), inp);
            }
        } catch (Exception e) {
            log.info("----------------------------------------------------\nend input file(s)" +
                    "\n----------------------------------------------------");
        }
        return map;
    }
}
