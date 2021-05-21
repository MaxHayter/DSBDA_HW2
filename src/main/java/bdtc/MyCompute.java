package bdtc;

import lombok.AllArgsConstructor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;

import java.util.*;

/**
 * MyCompute class, that compute the results from the cache
 */
@AllArgsConstructor
public class MyCompute {
    /**
     * Class for interacting with the cache
     */
    private final Ignite ignite;

    /**
     * Method, that get ignite cache
     * Uses interactions class {@link Interactions}
     * @return ignite cache
     */
    private IgniteCache<UUID, Interactions> interactCache() {
        CacheConfiguration<UUID, Interactions> cacheConf = CacheData.getCacheDataConfig();
        return ignite.getOrCreateCache(cacheConf);
    }

    /**
     * Method, that compute the results
     * Uses customKey class {@link CustomKey}
     * @return results, consisting of a key and a quantity
     */
    public Map<CustomKey, Long> getResults() {
        IgniteCache<UUID, Interactions> cache = interactCache();

        SqlFieldsQuery query = new SqlFieldsQuery(
                "SELECT i.newsId, t.name, count(*) " +
                "FROM Interactions as i, Types as t " +
                "WHERE i.typeId = t.type " +
                "GROUP BY (i.newsId, t.name)").setEnforceJoinOrder(true);

        Map<CustomKey, Long> result = new HashMap<>();

        QueryCursor<List<?>> cursor = cache.query(query);
        for (List<?> row : cursor) {
            CustomKey key = new CustomKey((Long)row.get(0), (String) row.get(1));
            result.put(key, (Long) row.get(2));
        }

        return result;
    }
}
