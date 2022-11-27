package screens;

import asciiPanel.AsciiPanel;
import com.heroiclabs.nakama.*;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupList;
import com.heroiclabs.nakama.api.Match;
import com.heroiclabs.nakama.api.MatchList;
import lombok.extern.slf4j.Slf4j;
import nakama.com.google.common.hash.BloomFilter;

import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static screens.PlayScreen.tetrisLogo;

@Slf4j
public class LobbyScreen implements Screen, Runnable {
    //TODO Add Group Selection and advancement to Lobby Waiting Screen
    private final ScheduledExecutorService exec;
    private static Map<String, String> lobbies = new HashMap<>();

    public LobbyScreen() {
        exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(this, 0, 30, TimeUnit.SECONDS);
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
    public void displayOutput(AsciiPanel terminal) {
        terminal.clear();
        for (int i = 0; i < tetrisLogo.length; i++) {
            terminal.write(tetrisLogo[i], 5, i + 1);
        }
        terminal.write(new Date().toString(), 5, 20);
        int y = 10;
        synchronized (lobbies) {
            for (Map.Entry<String, String> value : lobbies.entrySet()) {
                terminal.write("Lobby: " + value.getValue(), 5, y++);
            }
        }
        terminal.write("To refresh the lobby screen press [r]", 5, ++y);
        terminal.write("To create your own Lobby press [c]", 5, ++y);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        if (key.getKeyCode() == KeyEvent.VK_C) {
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
                LobbyCreateScreen screen = new LobbyCreateScreen();
                screen.displayOutput(terminal);
                return screen;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        if (key.getKeyCode() == KeyEvent.VK_R) {
            this.fetchLobbies();
        }
        return this;
    }

    @Override
    public boolean finishInput() {
        return false;
    }

    @Override
    public void run() {
        fetchLobbies();
    }
}
