package config.keys;

import Helper.OsUtil;
import asciiPanel.AsciiPanel;
import com.googlecode.lanterna.input.KeyStroke;
import config.LdataParser;
import screens.PlayOfflineScreen;
import screens.PlayOnlineScreen;
import screens.Screen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.googlecode.lanterna.input.KeyType.Character;
import static com.googlecode.lanterna.input.KeyType.*;

public enum KeyMenuConfig {

    MENUDOWN() {
        @Override
        public Screen execute(Screen screen, AsciiPanel terminal) {
            return screen.respondToUserInput(new KeyStroke('s', false, false), terminal);
        }
    },
    MENUUP() {
        @Override
        public Screen execute(Screen screen, AsciiPanel terminal) {
            return screen.respondToUserInput(new KeyStroke('w', false, false), terminal);
        }
    },
    SELECT() {
        @Override
        public Screen execute(Screen screen, AsciiPanel terminal) {
            return screen.respondToUserInput(new KeyStroke(' ', false, false), terminal);
        }
    },
    ENTER() {
        @Override
        public Screen execute(Screen screen, AsciiPanel terminal) {
            return screen.respondToUserInput(new KeyStroke(Enter), terminal);
        }
    },
    REFRESH() {
        @Override
        public Screen execute(Screen screen, AsciiPanel terminal) {
            return screen.respondToUserInput(new KeyStroke('r', false, false), terminal);
        }
    },
    CREATELOBBY() {
        @Override
        public Screen execute(Screen screen, AsciiPanel terminal) {
            return screen.respondToUserInput(new KeyStroke('c', false, false), terminal);
        }
    },
    PLAYOFFLINE() {
        @Override
        public Screen execute(Screen screen, AsciiPanel terminal) {
            return screen.respondToUserInput(new KeyStroke('o', false, false), terminal);
        }
    };


    private static Map<String, String> menuMap = new HashMap<>();


    public abstract Screen execute(Screen screen, AsciiPanel terminal);

    public static void initializeKeymap() {
        Map<String, Object> config = LdataParser.loadFrom(OsUtil.getConfigFile("tty-tetris.conf"));
        Map<String, Object> _menuMap = (Map) ((Map) config.get("keymap")).get("menuMap");
        for (String action : _menuMap.keySet()) {
            List<String> keyStrokes = (List) _menuMap.get(action);
            for (String keyStroke : keyStrokes) {
                menuMap.put(keyStroke, action);
            }
        }
    }


    public static Screen execute(KeyStroke key, Screen screen, AsciiPanel terminal) {
        if (!(screen instanceof PlayOnlineScreen || screen instanceof PlayOfflineScreen)) {
            if (menuMap.get(keyStrokeToString(key)) == null) {
                return screen;
            }
            return KeyMenuConfig.valueOf(menuMap.get(keyStrokeToString(key)).toUpperCase()).execute(screen, terminal);
        }
        return screen;
    }

    private static String keyStrokeToString(KeyStroke key) {
        if (key.getKeyType() == Character) {
            return key.getCharacter().toString();
        } else {
            System.out.println("SEND FUCKING HEPL PLS: " + key.getKeyType().toString());
            return key.getKeyType().toString();
        }
    }
}
