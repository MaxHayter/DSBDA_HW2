package bdtc;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;

/**
 * Serializable data class for input mapper (type) {@link Serializable}
 */
@AllArgsConstructor
@Data
public class Types implements Serializable {
    /**
     * Type identifier
     */
    @QuerySqlField(index = true)
    private Byte type;

    /**
     * Type value
     */
    @QuerySqlField
    private String name;
}
