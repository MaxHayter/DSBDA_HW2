package bdtc;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tests for Ignite application
 */
public class MyComputeTest {
    private static CacheData cacheData;
    private static MyCompute compute;
    private final String row1 = "20,82,2021-03-20 14:10:37,3";
    private final String row2 = "20,42,2021-01-03 08:56:22,2";
    private final String row3 = "12,47,2021-03-05 04:22:58,3";
    private final String row4 = "12,26,2020-12-22 06:01:37,2";

    private final Map<CustomKey, Long> baseResult = getBaseResult();

    @BeforeClass
    public static void init(){
        Ignite ignite = Ignition.start("config/ignite-config.xml");
        String interactions = "src/test/java/resources/interactions";
        String mapper = "src/test/java/resources/types";
        cacheData =  new CacheData(ignite);
        try {
            cacheData.loadData(interactions);
            cacheData.loadMapper(mapper);
        } catch (IOException e) {
            e.printStackTrace();
        }
        compute = new MyCompute(ignite);
    }

    private Map<CustomKey, Long> getBaseResult() {
        Map<CustomKey, Long> result = new HashMap<>();
        result.put(new CustomKey(12L, "не взаимодействовал"), 1L);
        result.put(new CustomKey(12L, "открыл и прочитал"), 2L);
        result.put(new CustomKey(20L, "открыл и прочитал"), 1L);
        return result;
    }

    @Test
    public void testDefault() {
        Map <CustomKey, Long> result = compute.getResults();
        assert baseResult.equals(result);
    }

    @Test
    public void testAddOne() {
        UUID id = cacheData.put(row1);
        baseResult.put(new CustomKey(20L, "не взаимодействовал"), 1L);

        assert baseResult.equals(compute.getResults());

        cacheData.delete(id);
    }

    @Test
    public void testPatchOne() {
        UUID id = cacheData.put(row3);
        CustomKey key = new CustomKey(12L, "не взаимодействовал");
        baseResult.replace(key, baseResult.get(key)+1L);

        assert baseResult.equals(compute.getResults());

        cacheData.delete(id);
    }

    @Test
    public void testAddTwoDifferentTypes() {
        UUID id1 = cacheData.put(row1);
        UUID id2 = cacheData.put(row2);

        baseResult.put(new CustomKey(20L, "не взаимодействовал"), 1L);
        baseResult.put(new CustomKey(20L, "открыл на предпросмотр"), 1L);

        assert baseResult.equals(compute.getResults());

        cacheData.delete(id1);
        cacheData.delete(id2);
    }

    @Test
    public void testAddTwoDifferentNews() {
        UUID id1 = cacheData.put(row1);
        UUID id2 = cacheData.put(row3);

        baseResult.put(new CustomKey(20L, "не взаимодействовал"), 1L);
        CustomKey key = new CustomKey(12L, "не взаимодействовал");
        baseResult.replace(key, baseResult.get(key)+1L);

        assert baseResult.equals(compute.getResults());

        cacheData.delete(id1);
        cacheData.delete(id2);
    }

    @Test
    public void testAddTwoDifferentTypesAndNews() {
        UUID id1 = cacheData.put(row1);
        UUID id2 = cacheData.put(row4);

        baseResult.put(new CustomKey(20L, "не взаимодействовал"), 1L);
        baseResult.put(new CustomKey(12L, "открыл на предпросмотр"), 1L);

        assert baseResult.equals(compute.getResults());

        cacheData.delete(id1);
        cacheData.delete(id2);
    }

    @Test
    public void testAddAll() {
        UUID id1 = cacheData.put(row1);
        UUID id2 = cacheData.put(row2);
        UUID id3 = cacheData.put(row3);
        UUID id4 = cacheData.put(row4);

        baseResult.put(new CustomKey(20L, "не взаимодействовал"), 1L);
        baseResult.put(new CustomKey(20L, "открыл на предпросмотр"), 1L);
        CustomKey key = new CustomKey(12L, "не взаимодействовал");
        baseResult.replace(key, baseResult.get(key)+1L);
        baseResult.put(new CustomKey(12L, "открыл на предпросмотр"), 1L);

        assert baseResult.equals(compute.getResults());

        cacheData.delete(id1);
        cacheData.delete(id2);
        cacheData.delete(id3);
        cacheData.delete(id4);
    }
}
