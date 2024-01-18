package dto;

import lombok.Getter;

/**
 * This Data Transfer Object is used for transferring the Ready State between two Clients
 */
@Getter
public class ReadyDTO {

    /**
     * The Player ID of the Sender
     */
    private final String player_id;

    /**
     * Which State the sender has
     */
    private final boolean readyState;

    /**
     * Creates a ReadyDTO object with the given player ID and ready state.
     *
     * @param player_id The player ID of the sender
     * @param readyState The ready state of the sender
     */
    public ReadyDTO(String player_id, boolean readyState) {
        this.player_id = player_id;
        this.readyState = readyState;
    }

}
