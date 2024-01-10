package communication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player implements Comparable<Player> {

    private String displayName;
    private boolean isHost;
    private boolean isReady;
    private String playerId;

    public Player(String playerId, String displayName, boolean isHost) {
        this.displayName = displayName;
        this.isHost = isHost;
        isReady = false;
        this.playerId = playerId;
    }

    @Override
    public int compareTo(Player other) {
        return playerId.compareTo(other.getPlayerId());
    }
}