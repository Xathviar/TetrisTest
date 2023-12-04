package config;

import Helper.OsUtil;
import Helper.TerminalHelper;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import screens.PlayOfflineScreen;
import screens.PlayOnlineScreen;
import screens.Screen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.googlecode.lanterna.input.KeyType.*;

public enum KeyConfig {
    MOVELEFT() {
        @Override
        public Screen execute(Screen screen, TerminalHelper terminal) {
            return screen.respondToUserInput(new KeyStroke(ArrowLeft), terminal);
        }
    },
    MOVERIGHT() {
        @Override
        public Screen execute(Screen screen, TerminalHelper terminal) {
            return screen.respondToUserInput(new KeyStroke(ArrowRight), terminal);
        }
    },
    ROTATECLOCKWISE() {
        @Override
        public Screen execute(Screen screen, TerminalHelper terminal) {
            return screen.respondToUserInput(new KeyStroke(ArrowUp), terminal);
        }
    },
    ROTATECCLOCKWISE() {
        @Override
        public Screen execute(Screen screen, TerminalHelper terminal) {
            return screen.respondToUserInput(new KeyStroke('z', false, false), terminal);
        }
    },
    SOFTDROP() {
        @Override
        public Screen execute(Screen screen, TerminalHelper terminal) {
            return screen.respondToUserInput(new KeyStroke(ArrowDown), terminal);
        }
    },
    HARDDROP() {
        @Override
        public Screen execute(Screen screen, TerminalHelper terminal) {
            return screen.respondToUserInput(new KeyStroke(' ', false, false), terminal);

        }
    },
    HOLD() {
        @Override
        public Screen execute(Screen screen, TerminalHelper terminal) {
            return screen.respondToUserInput(new KeyStroke('x', false, false), terminal);
        }
    },
    MENUDOWN() {
        @Override
        public Screen execute(Screen screen, TerminalHelper terminal) {
            return screen.respondToUserInput(new KeyStroke('s', false, false), terminal);
        }
    },
    MENUUP() {
        @Override
        public Screen execute(Screen screen, TerminalHelper terminal) {
            return screen.respondToUserInput(new KeyStroke('w', false, false), terminal);
        }
    },
    SELECT() {
        @Override
        public Screen execute(Screen screen, TerminalHelper terminal) {
            return screen.respondToUserInput(new KeyStroke(' ', false, false), terminal);
        }
    },
    ENTER() {
        @Override
        public Screen execute(Screen screen, TerminalHelper terminal) {
            return screen.respondToUserInput(new KeyStroke(Enter), terminal);
        }
    };


    private static Map<String, String> playMap = new HashMap<>();
    private static Map<String, String> menuMap = new HashMap<>();

    public abstract Screen execute(Screen screen, TerminalHelper terminal);

    public static void initializeKeymap() {
        Map<String, Object> config = LdataParser.loadFrom(OsUtil.getConfigFile("tty-tetris.conf"));
        Map<String, Object> _playMap = (Map) ((Map) config.get("keymap")).get("playMap");
        Map<String, Object> _menuMap = (Map) ((Map) config.get("keymap")).get("menuMap");
        for (String action : _playMap.keySet()) {
            List<String> keyStrokes = (List) _playMap.get(action);
            for (String keyStroke : keyStrokes) {
                playMap.put(keyStroke, action);
            }
        }
        for (String action : _menuMap.keySet()) {
            List<String> keyStrokes = (List) _menuMap.get(action);
            for (String keyStroke : keyStrokes) {
                menuMap.put(keyStroke, action);
            }
        }
        System.out.println(playMap);
        System.out.println();
        System.out.println(menuMap);

    }


    public static Screen execute(KeyStroke key, Screen screen, TerminalHelper terminal) {
        if (!(screen instanceof PlayOnlineScreen || screen instanceof PlayOfflineScreen)) {
            if (keyStrokeToString(key) == null) {
                return screen;
            }
            return KeyConfig.valueOf(menuMap.get(keyStrokeToString(key)).toUpperCase()).execute(screen, terminal);
        } else {
            if (keyStrokeToString(key) == null) {
                return screen;
            }
            return KeyConfig.valueOf(playMap.get(keyStrokeToString(key)).toUpperCase()).execute(screen, terminal);
        }
    }

    private static String keyStrokeToString(KeyStroke key) {
        if (key.getKeyType() == Character) {
            System.out.println(key.getCharacter().toString());
            return key.getCharacter().toString();
        } else {
            System.out.println(key.getKeyType().toString());
            return key.getKeyType().toString();
        }
    }
}
