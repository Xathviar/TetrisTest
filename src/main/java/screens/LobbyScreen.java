package screens;

import Helper.TerminalHelper;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupList;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;

import static screens.PlayScreen.tetrisLogo;

@Slf4j
public class LobbyScreen implements Screen, Runnable {
    //TODO Add Group Selection and advancement to Lobby Waiting Screen
    private final ScheduledExecutorService exec;
    private static Map<String, String> lobbies = new LinkedHashMap<>();

    ScheduledFuture<?> result;
    private int selected = -1;
    private boolean runnable;

    public LobbyScreen() {
        runnable = true;
        exec = Executors.newSingleThreadScheduledExecutor();
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
                log.info(lobbies.toString());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void displayOutput(TerminalHelper terminal) {
        terminal.clear();
        for (int i = 0; i < tetrisLogo.length; i++) {
            terminal.write(tetrisLogo[i], 5, i + 1);
        }
        terminal.write(new Date().toString(), 5, 20);
        int y = 10;
        synchronized (lobbies) {
            for (Map.Entry<String, String> value : lobbies.entrySet()) {
                if (selected == y - 10) {
                    terminal.write("Lobby: " + value.getValue(), 5, y++, TextColor.ANSI.GREEN);
                } else {
                    terminal.write("Lobby: " + value.getValue(), 5, y++);
                }
            }
        }
        terminal.write("To refresh the lobby screen press [r]", 5, ++y);
        terminal.write("To create your own Lobby press [c]", 5, ++y);
    }

    @Override
    public Screen respondToUserInput(KeyStroke key, TerminalHelper terminal) {
        if (key.getCharacter() != null && Character.toLowerCase(key.getCharacter()) == 'c') {
            try {
                MainClass.aClass.socket = MainClass.aClass.client.createSocket();
                SocketListener listener = new AbstractSocketListener() {
                    @Override
                    public void onDisconnect(final Throwable t) {
                        log.info("Socket disconnected.");
                    }
                };
                MainClass.aClass.socket.connect(MainClass.aClass.session, listener).get();
                log.info("Socket connected.");
                result.cancel(true);
                exec.shutdownNow();
                runnable = false;
                LobbyCreateScreen screen = new LobbyCreateScreen();
                screen.displayOutput(terminal);
                return screen;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        if (key.getCharacter() != null && Character.toLowerCase(key.getCharacter()) == 'r') {
            this.fetchLobbies();
        }
        if (key.getKeyType() == KeyType.ArrowUp || (key.getCharacter() != null && Character.toLowerCase(key.getCharacter()) == 'w')) {
            this.selectAbove();
        } else if (key.getKeyType() == KeyType.ArrowDown || (key.getCharacter() != null && Character.toLowerCase(key.getCharacter()) == 's')) {
            System.out.println("Select Below!");
            this.selectBelow();
        }
        try {
            if (key.getKeyType() == KeyType.Enter || key.getCharacter() == ' ') {
                if (selected == -1) {
                    return this;
                }
                int c = 0;
                for (String group_id : lobbies.keySet()) {
                    if (c == selected) {
                        try {
                            MainClass.aClass.client.joinGroup(MainClass.aClass.session, group_id).get();
                            result.cancel(true);
                            exec.shutdownNow();
                            runnable = false;
                            LobbyWaitingScreen waitingScreen = new LobbyWaitingScreen(group_id, lobbies.get(group_id), false);
                            waitingScreen.displayOutput(terminal);
                            return waitingScreen;
                        } catch (InterruptedException | ExecutionException e) {
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
        if (runnable) {
            fetchLobbies();
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
