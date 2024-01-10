package config.keys;

import Helper.OsUtil;
import config.LdataParser;
import logic.TetrisField;
import screens.AsciiPanel;
import screens.PlayOfflineScreen;
import screens.PlayOnlineScreen;
import screens.Screen;

import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.*;

public enum KeyPlay {
    MOVELEFT() {
        @Override
        public void execute(TetrisField field) {
            if (this.counter == 0) {
                field.moveLeft();
            } else if (this.counter >= dasMS && this.counter % arrMS == 0) {
                field.dasLeft();
            }
        }
    },
    MOVERIGHT() {
        @Override
        public void execute(TetrisField field) {
            if (this.counter == 0) {
                field.moveRight();
            } else if (this.counter >= dasMS && this.counter % arrMS == 0) {
                field.dasRight();
            }
        }
    },
    ROTATECLOCKWISE() {
        @Override
        public void execute(TetrisField field) {
            if (this.counter % 250 == 0) {
                field.rotateClockwise();
            }
        }
    },
    ROTATECCLOCKWISE() {
        @Override
        public void execute(TetrisField field) {
            if (this.counter % 250 == 0) {
                field.rotateCClockwise();
            }
        }
    },
    SOFTDROP() {
        @Override
        public void execute(TetrisField field) {
            if (sdfFPS < 0) {
                field.instantsdf();
            } else if (this.counter % (sdfFPS * 10) == 0) {
                field.softDrop();
            }
        }
    },
    HARDDROP() {
        @Override
        public void execute(TetrisField field) {
            if (counter == 0)
                field.hardDrop();

        }
    },
    HOLD() {
        @Override
        public void execute(TetrisField field) {
            field.swapHold();
        }
    };

    int counter;

    private static long dasMS;

    private static long arrMS;

    private static long sdfFPS;


    public abstract void execute(TetrisField field);

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
        Map<String, Object> gameplay = (Map) ((Map) config.get("gameplay"));
        arrMS = (long) gameplay.get("ARR");
        dasMS = (long) gameplay.get("DAS");
        sdfFPS = (long) gameplay.get("SDF");
        System.out.println(arrMS);
        System.out.println(dasMS);
        System.out.println(sdfFPS);
    }

    public void incrementCounter() {
        counter++;
    }

    public KeyPlay resetCounter() {
        counter = 0;
        return this;
    }

    public static KeyPlay getKey(KeyEvent key) {
        if (playMap.get(keyStrokeToString(key)) != null) {
            System.out.println(playMap.get(keyStrokeToString(key)).toUpperCase());
            return KeyPlay.valueOf(playMap.get(keyStrokeToString(key)).toUpperCase()).resetCounter();
        }
        return null;
    }

    private static String keyStrokeToString(KeyEvent key) {
        return KeyEvent.getKeyText(key.getKeyCode()).toLowerCase();
    }

}
