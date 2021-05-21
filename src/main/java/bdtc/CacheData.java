package bdtc;

import lombok.AllArgsConstructor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.util.typedef.F;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * CacheData class implements the main set of functions necessary for working with the cache
 */
@AllArgsConstructor
public class CacheData {
    /**
     * Cache name
     */
    private final static String INTERACTIONS_CACHE = "interactions";

    /**
     * Class for interacting with the cache
     */
    private final Ignite ignite;

    /**
     * Datetime formatter
     */
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Regex pattern to split input interactions row
     */
    private final static Pattern splitStr = Pattern.compile(",");

    /**
     * Regex pattern to split input mapper (types) row
     */
    private final static Pattern splitStrMapper = Pattern.compile("-");

    /**
     * Method, which return cache configuration
     * Uses interactions class {@link Interactions}
     * @return cache configuration
     */
    public static CacheConfiguration<UUID, Interactions> getCacheDataConfig() {
        CacheConfiguration<UUID, Interactions> cacheConfig = new CacheConfiguration<>(INTERACTIONS_CACHE);
        cacheConfig.setStatisticsEnabled(true);

        cacheConfig.setIndexedTypes(UUID.class, Interactions.class);

        cacheConfig.setCacheMode(CacheMode.PARTITIONED);
        cacheConfig.setAtomicityMode(CacheAtomicityMode.ATOMIC);

        cacheConfig.setQueryEntities(F.asList(
                new QueryEntity()
                    .setKeyType(UUID.class.getName())
                    .setValueType(Interactions.class.getName())
                    .setTableName("Interactions")
                    .addQueryField("newsId", Long.class.getName(), "NEWSID")
                    .addQueryField("userId", Long.class.getName(), "USERID")
                    .addQueryField("timestamp", LocalDateTime.class.getName(), "TIMESTAMP")
                    .addQueryField("typeId", Byte.class.getName(), "TYPEID"),
                new QueryEntity()
                    .setKeyType(UUID.class.getName())
                    .setValueType(Types.class.getName())
                    .setTableName("Types")
                    .addQueryField("type", Byte.class.getName(), "TYPE")
                    .addQueryField("name", String.class.getName(), "NAME")));

        return cacheConfig;
    }

    /**
     * Method, which load interactions data in cache from file
     * @param path to file with data
     * @throws IOException from FileReader()
     */
    public void loadData(String path) throws IOException {
        ignite.getOrCreateCache(getCacheDataConfig());

        BufferedReader r = new BufferedReader(new FileReader(path));
        IgniteDataStreamer<UUID, Interactions> streamer = ignite.dataStreamer(INTERACTIONS_CACHE);

        String line;
        String[] splitData;
        while ((line = r.readLine()) != null) {
            splitData = splitStr.split(line);
            if (splitData.length != 4) throw new RuntimeException("Invalid number of columns in data line");
            Interactions inp = new Interactions(Long.parseLong(splitData[0]),
                    Long.parseLong(splitData[1]), LocalDateTime.parse(splitData[2], formatter),
                    Byte.parseByte(splitData[3]));
            streamer.addData(UUID.randomUUID(), inp);
        }
        streamer.close();
    }

    /**
     * Method, which load mapper (types) data in cache from file
     * @param path to file with mapper
     * @throws IOException from FileReader()
     */
    public void loadMapper(String path) throws IOException {
        ignite.getOrCreateCache(getCacheDataConfig());

        BufferedReader r = new BufferedReader(new FileReader(path));
        IgniteDataStreamer<UUID, Types> streamer = ignite.dataStreamer(INTERACTIONS_CACHE);
        String line;
        String[] splitData;
        while ((line = r.readLine()) != null) {
            splitData = splitStrMapper.split(line);
            if (splitData.length != 2) throw new RuntimeException("Invalid number of columns in data line");
            Types types = new Types(Byte.parseByte(splitData[0]), splitData[1]);
            streamer.addData(UUID.randomUUID(), types);
        }
        streamer.close();
    }

    /**
     * Method, which convert and put in cache row
     * @param row input string
     * @return id data in cache
     * @throws RuntimeException if the input string is invalid
     */
    public UUID put(String row) throws RuntimeException {
        IgniteCache<UUID, Interactions> cache =  ignite.getOrCreateCache(getCacheDataConfig());
        String[] splitData = splitStr.split(row);
        if (splitData.length != 4) throw new RuntimeException("Invalid number of columns in data line");
        Interactions inp = new Interactions(Long.parseLong(splitData[0]),
                Long.parseLong(splitData[1]), LocalDateTime.parse(splitData[2], formatter),
                Byte.parseByte(splitData[3]));
        UUID uuid = UUID.randomUUID();
        cache.put(uuid, inp);
        return uuid;
    }

    /**
     * Method, which delete data from cache by id
     * @param id row in cache
     */
    public void delete(UUID id) {
        IgniteCache<UUID, Interactions> cache =  ignite.getOrCreateCache(getCacheDataConfig());
        cache.remove(id);
    }
}
