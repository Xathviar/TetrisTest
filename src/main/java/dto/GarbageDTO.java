package dto;

import lombok.Getter;

/**
 * This Data Transfer Object is used for transferring Garbage Data between two Clients
 */
@Getter
public class GarbageDTO {

    /**
     * How much Garbage Lines should be sent
     */
    private final int garbageHeight;

    /**
     * On which X Position the Garbage Gap should be
     */
    private final int garbageGap;

    /**
     * Initializes a new instance of the GarbageDTO class.
     *
     * @param garbageHeight the height of the garbage lines to be sent
     * @param garbageGap    the x position of the garbage gap
     */
    public GarbageDTO(int garbageHeight, int garbageGap) {
        this.garbageHeight = garbageHeight;
        this.garbageGap = garbageGap;
    }

}
