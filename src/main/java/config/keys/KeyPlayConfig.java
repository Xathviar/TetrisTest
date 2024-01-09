package config.keys;

import Helper.OsUtil;
import com.googlecode.lanterna.input.KeyStroke;
import config.LdataParser;
import logic.TetrisField;

import java.util.*;

import static com.googlecode.lanterna.input.KeyType.*;
import static com.googlecode.lanterna.input.KeyType.ArrowDown;

public enum KeyPlayConfig {
    MOVELEFT() {
        @Override
        public void execute(TetrisField field) {
            field.moveLeft();
        }
    },
    MOVERIGHT() {
        @Override
        public void execute(TetrisField field) {
            field.moveRight();
        }
    },
    ROTATECLOCKWISE() {
        @Override
        public void execute(TetrisField field) {
            field.rotateClockwise();
        }
    },
    ROTATECCLOCKWISE() {
        @Override
        public void execute(TetrisField field) {
            field.rotateCClockwise();
        }
    },
    SOFTDROP() {
        @Override
        public void execute(TetrisField field) {
            field.softDrop();
        }
    },
    HARDDROP() {
        @Override
        public void execute(TetrisField field) {
            field.hardDrop();

        }
    },
    HOLD() {
        @Override
        public void execute(TetrisField field) {
            field.swapHold();
        }
    };


    private int counter;

    private int max;
    public abstract void execute(TetrisField field);


    public static final Set<KeyPlayConfig> keys = new HashSet<>();

    private static Map<String, String> playMap = new HashMap<>();

    public static void initializeKeymap() {
        Map<String, Object> config = LdataParser.loadFrom(OsUtil.getConfigFile("tty-tetris.conf"));
        Map<String, Object> _playMap = (Map) ((Map) config.get("keymap")).get("playMap");
        for (String action : _playMap.keySet()) {
            List<String> keyStrokes = (List) _playMap.get(action);
            for (String keyStroke : keyStrokes) {
                playMap.put(keyStroke, action);
            }
        }
    }

}
