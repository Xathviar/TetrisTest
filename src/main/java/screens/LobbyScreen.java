package screens;

import config.Constants;
import Helper.TerminalHelper;

import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupList;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
public class LobbyScreen implements Screen, Runnable {
    private static final Map<String, String> lobbies = new LinkedHashMap<>();
    private final ScheduledExecutorService exec;
    public volatile AtomicBoolean runnable;
    ScheduledFuture<?> result;
    private int selected = -1;

    public LobbyScreen() {
        runnable = new AtomicBoolean();
        runnable.set(true);
        exec = Executors.newSingleThreadScheduledExecutor();
    }


    public void startThread() {
        result = exec.scheduleAtFixedRate(this, 0, 10, TimeUnit.SECONDS);
    }

    private void fetchLobbies() {
        synchronized (lobbies) {
            lobbies.clear();
            try {
                GroupList list = MainClass.aClass.client.listGroups(MainClass.aClass.session, "%").get();
                for (Group group : list.getGroupsList()) {
                    lobbies.put(group.getId(), group.getName());
                }
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.clear();
        TerminalHelper.writeTetrisLogo(terminal);
        terminal.write(new Date().toString(), 5, 20);
        int y = 10;
        synchronized (lobbies) {
            for (Map.Entry<String, String> value : lobbies.entrySet()) {
                if (selected == y - 10) {
                    terminal.write("Lobby: " + value.getValue(), 5, y++, Constants.selectedColor);
                } else {
                    terminal.write("Lobby: " + value.getValue(), 5, y++);
                }
            }
        }
        terminal.write("To refresh the lobby screen press [r]", 5, ++y);
        terminal.write("To create your own Lobby press [c]", 5, ++y);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        if (Character.toLowerCase(key.getKeyChar()) == 'c') {
            MainClass.aClass.createSocket();
                synchronized (runnable) {
                    runnable.set(false);
                }
                LobbyCreateScreen screen = new LobbyCreateScreen();
                screen.displayOutput(terminal);
                return screen;
        }
        if (Character.toLowerCase(key.getKeyCode()) == 'r') {
            this.fetchLobbies();
        }
        if (key.getKeyCode() == KeyEvent.VK_UP || Character.toLowerCase(key.getKeyChar()) == 'w') {
            this.selectAbove();
        } else if (key.getKeyCode() == KeyEvent.VK_DOWN || Character.toLowerCase(key.getKeyChar()) == 's') {
            this.selectBelow();
        }
        try {
            if (key.getKeyCode() == KeyEvent.VK_ENTER || key.getKeyChar() == ' ') {
                if (selected == -1) {
                    return this;
                }
                MainClass.aClass.createSocket();
                int c = 0;
                for (String group_id : lobbies.keySet()) {
                    if (c == selected) {
                        try {
                            MainClass.aClass.client.joinGroup(MainClass.aClass.session, group_id).get();
                            synchronized (runnable) {
                                runnable.set(false);
                            }
                            LobbyWaitingScreen waitingScreen = new LobbyWaitingScreen(group_id, lobbies.get(group_id), false);
                            waitingScreen.displayOutput(terminal);
                            return waitingScreen;
                        } catch (InterruptedException | ExecutionException e) {
                            log.error(e.toString());
                            // TODO Handle if Group is full. Maybe just display Groups that aren't full
                        }
                    }
                    c++;
                }
            }
        } catch (NullPointerException ignored) {

        }
        return this;
    }

    @Override
    public boolean finishInput() {
        return false;
    }

    @Override
    public void run() {
        synchronized (runnable) {
            if (runnable.get()) {
                fetchLobbies();
            } else {
                exec.shutdownNow();
                throw new RuntimeException();
            }
        }
    }

    public void selectAbove() {
        if (selected == 0) {
            selected = lobbies.size() - 1;
        } else {
            selected--;
        }
    }

    public void selectBelow() {
        if (selected == lobbies.size() - 1) {
            selected = 0;
        } else {
            selected++;
        }
    }

}
