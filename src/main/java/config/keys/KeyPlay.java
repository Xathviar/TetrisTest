package config.keys;

import helper.OsUtil;
import config.LdataParser;
import logic.TetrisField;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.KeyEvent;
import java.util.*;

/**
 * Enum KeyPlay represents the different in-game operations that can be performed by the player
 * in the Tetris game. The operations include moving pieces left/right, rotating pieces,
 * soft and hard dropping pieces, and putting pieces on hold. Each of these operations is
 * represented as an enum and implement the method 'execute' which executes said operation.
 * <p>
 * Class also contains the initialization of the keymap (which binds keys to respective operations)
 * and count increment and reset methods which are part of the implementation of the repeat action function.
 */
@Slf4j
public enum KeyPlay {

    /**
     * Represents the 'Move Left' in-game operation.
     */
    MOVELEFT() {
        @Override
        public void execute(TetrisField field) {
            if (this.counter == 0) {
                log.debug("Move Left");
                field.moveLeft();
            } else if (this.counter >= dasMS && this.counter % arrMS == 0) {
                log.debug("DAS Left");
                field.dasLeft();
            }
        }
    },
    /**
     * Represents the 'Move Right' in-game operation.
     */
    MOVERIGHT() {
        @Override
        public void execute(TetrisField field) {
            if (this.counter == 0) {
                log.debug("Move Right");
                field.moveRight();
            } else if (this.counter >= dasMS && this.counter % arrMS == 0) {
                log.debug("DAS Right");
                field.dasRight();
            }
        }
    },
    /**
     * Represents the 'Rotate Clockwise' in-game operation.
     */
    ROTATECLOCKWISE() {
        @Override
        public void execute(TetrisField field) {
            if (this.counter % 250 == 0) {
                log.debug("Rotate Clockwise");
                field.rotateClockwise();
            }
        }
    },
    /**
     * Represents the 'Rotate Counter Clockwise' in-game operation.
     */
    ROTATECCLOCKWISE() {
        @Override
        public void execute(TetrisField field) {
            if (this.counter % 250 == 0) {
                log.debug("Rotate Counterclockwise");
                field.rotateCClockwise();
            }
        }
    },
    /**
     * Represents the 'Soft Drop' in-game operation.
     */
    SOFTDROP() {
        @Override
        public void execute(TetrisField field) {
            if (sdfFPS < 0 && counter == 0) {
                log.debug("Instant Softdrop");
                field.instantsdf();
            } else if (this.counter % (sdfFPS * 10) == 0) {
                log.debug("Soft Drop");
                field.softDrop();
            }
        }
    },
    /**
     * Represents the 'Hard Drop' in-game operation.
     */
    HARDDROP() {
        @Override
        public void execute(TetrisField field) {
            if (counter == 0) {
                log.debug("Hard Drop");
                field.hardDrop();
            }
        }
    },
    /**
     * Represents the 'Hold' in-game operation.
     */
    HOLD() {
        @Override
        public void execute(TetrisField field) {
            field.swapHold();
        }
    };
    /**
     * This counter is used to save how long the key has been pressed in ms
     */
    int counter;

    /**
     * This Long is used to represent after how many milliseconds DAS should be implemented <br>
     * It is set with {@link KeyPlay#initializeKeymap()}
     */
    private static long dasMS;

    /**
     * This Long is used to represent the delay in milliseconds between ARR that should be implemented <br>
     * It is set with {@link KeyPlay#initializeKeymap()}
     */
    private static long arrMS;

    /**
     * This Long is used to represent the delay between Soft Drops in milliseconds <br>
     * It is set with {@link KeyPlay#initializeKeymap()} and -1 equals to instant softdrop which is useful for T-Spins
     */
    private static long sdfFPS;

    /**
     * This Hashmap is used for mapping between the Enums and the KeyEvents and is filled by {@link KeyMenuConfig#initializeKeymap()}
     */
    private static final Map<String, String> playMap = new HashMap<>();

    /**
     * Executes the associated game operation.
     *
     * @param field The TetrisField object where the operation is to be performed.
     */
    public abstract void execute(TetrisField field);

    /**
     * Initializes the keymap from a configuration file using the LdataParser.
     */
    public static void initializeKeymap() {
        Map<String, Object> config = LdataParser.loadFrom(OsUtil.getConfigFile("tty-tetris.conf"));
        Map<String, Object> _playMap = (Map) ((Map) config.get("keymap")).get("playMap");
        for (String action : _playMap.keySet()) {
            List<String> keyStrokes = (List) _playMap.get(action);
            for (String keyStroke : keyStrokes) {
                playMap.put(keyStroke, action);
            }
        }
        Map<String, Object> gameplay = (Map) config.get("gameplay");
        arrMS = (long) gameplay.get("ARR");
        dasMS = (long) gameplay.get("DAS");
        sdfFPS = (long) gameplay.get("SDF");
    }

    /**
     * Increments the counter by one.
     */
    public void incrementCounter() {
        counter++;
    }

    /**
     * Resets the counter to zero.
     *
     * @return Current enum instance with counter reset
     */
    public KeyPlay resetCounter() {
        counter = 0;
        return this;
    }

    /**
     * Gets the associated KeyPlay value from the keyMap using provided key.
     * If found, the counter of associated operation is reset
     *
     * @param key The KeyEvent to be looked up in keyMap
     * @return The associated KeyPlay value from the keyMap, null if not found.
     */
    public static KeyPlay getKey(KeyEvent key) {
        if (playMap.get(keyStrokeToString(key)) != null) {
            return KeyPlay.valueOf(playMap.get(keyStrokeToString(key)).toUpperCase()).resetCounter();
        }
        return null;
    }

    /**
     * Converts KeyEvent to a lowercase string character representation
     *
     * @param key The KeyEvent to be converted to String
     * @return String representing the KeyEvent in lowercase character
     */
    private static String keyStrokeToString(KeyEvent key) {
        return KeyEvent.getKeyText(key.getKeyCode()).toLowerCase();
    }

    @Override
    public String toString() {
        return this.name() + ": " + counter;
    }
}
