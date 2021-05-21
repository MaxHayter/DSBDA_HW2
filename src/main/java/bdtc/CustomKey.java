package bdtc;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Serializable data class for output key {@link Serializable}
 */
@AllArgsConstructor
@Data
public class CustomKey implements Serializable {
    /**
     * News identifier
     */
    private long newsId;

    /**
     * Type value
     */
    private String typeInteraction;
}
