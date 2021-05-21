package bdtc;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Serializable data class for input interactions {@link Serializable}
 */
@AllArgsConstructor
@Data
public class Interactions implements Serializable {
    /**
     * News identifier
     */
    @QuerySqlField(index = true)
    private Long newsId;

    /**
     * User identifier
     */
    private Long userId;

    /**
     * Interaction timestamp
     */
    private LocalDateTime timestamp;

    /**
     * Type identifier
     */
    @QuerySqlField
    private Byte typeId;
}
