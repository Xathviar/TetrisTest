package dto;

import lombok.Getter;

@Getter
public class ReadyDTO {
    private final String player_id;

    private final boolean readyState;

    public ReadyDTO(String player_id, boolean readyState) {
        this.player_id = player_id;
        this.readyState = readyState;
    }

}
