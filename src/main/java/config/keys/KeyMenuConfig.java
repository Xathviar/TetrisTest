package config.keys;

import helper.OsUtil;

import config.LdataParser;
import screens.AsciiPanel;
import screens.PlayOfflineScreen;
import screens.PlayOnlineScreen;
import screens.Screen;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum KeyMenuConfig {

    MENUDOWN() {
        @Override
        public Screen execute(Screen screen, AsciiPanel terminal) {
            return screen.respondToUserInput(new KeyEvent(terminal, 0, 0L, 0, KeyEvent.VK_DOWN), terminal);
        }
    },
    MENUUP() {
        @Override
        public Screen execute(Screen screen, AsciiPanel terminal) {
            return screen.respondToUserInput(new KeyEvent(terminal, 0, 0L, 0, KeyEvent.VK_UP), terminal);
        }
    },
    SELECT() {
        @Override
        public Screen execute(Screen screen, AsciiPanel terminal) {
            return screen.respondToUserInput(new KeyEvent(terminal, 0, 0L, 0, KeyEvent.VK_SPACE), terminal);
        }
    },
    ENTER() {
        @Override
        public Screen execute(Screen screen, AsciiPanel terminal) {
            return screen.respondToUserInput(new KeyEvent(terminal, 0, 0L, 0, KeyEvent.VK_ENTER), terminal);
        }
    },
    REFRESH() {
        @Override
        public Screen execute(Screen screen, AsciiPanel terminal) {
            return screen.respondToUserInput(new KeyEvent(terminal, 0, 0L, 0, KeyEvent.VK_R), terminal);
        }
    },
    CREATELOBBY() {
        @Override
        public Screen execute(Screen screen, AsciiPanel terminal) {
            return screen.respondToUserInput(new KeyEvent(terminal, 0, 0L, 0, KeyEvent.VK_C), terminal);
        }
    },
    PLAYOFFLINE() {
        @Override
        public Screen execute(Screen screen, AsciiPanel terminal) {
            return screen.respondToUserInput(new KeyEvent(terminal, 0, 0L, 0, KeyEvent.VK_O), terminal);
        }
    };


    private static final Map<String, String> menuMap = new HashMap<>();


    public abstract Screen execute(Screen screen, AsciiPanel terminal);

    public static void initializeKeymap() {
        Map<String, Object> config = LdataParser.loadFrom(OsUtil.getConfigFile("tty-tetris.conf"));
        Map<String, Object> _menuMap = (Map) ((Map) config.get("keymap")).get("menuMap");
        for (String action : _menuMap.keySet()) {
            List<String> keyStrokes = (List) _menuMap.get(action);
            for (String keyStroke : keyStrokes) {
                menuMap.put(keyStroke.toLowerCase(), action);
            }
        }
    }


    public static Screen execute(KeyEvent key, Screen screen, AsciiPanel terminal) {
        if (!(screen instanceof PlayOnlineScreen || screen instanceof PlayOfflineScreen)) {
            if (menuMap.get(keyStrokeToString(key)) == null) {
                return screen;
            }
            return KeyMenuConfig.valueOf(menuMap.get(keyStrokeToString(key)).toUpperCase()).execute(screen, terminal);
        }
        return screen;
    }

    private static String keyStrokeToString(KeyEvent key) {
        System.out.println(KeyEvent.getKeyText(key.getKeyCode()).toLowerCase());
        return KeyEvent.getKeyText(key.getKeyCode()).toLowerCase();
    }
}
