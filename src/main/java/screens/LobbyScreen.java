package screens;

import asciiPanel.AsciiPanel;
import com.heroiclabs.nakama.*;
import com.heroiclabs.nakama.api.Match;
import com.heroiclabs.nakama.api.MatchList;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static screens.PlayScreen.tetrisLogo;

@Slf4j
public class LobbyScreen implements Screen {
    private final ScheduledExecutorService exec;
    private MatchList matchList;

    public LobbyScreen() {
        exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(this::fetchLobbies, 0, 30, TimeUnit.SECONDS);
    }

    private void fetchLobbies() {
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.clear();
        for (int i = 0; i < tetrisLogo.length; i++) {
            terminal.write(tetrisLogo[i], 5, i + 1);
        }
        int y = 10;
        if (matchList != null) {
            for (Match match : matchList.getMatchesList()) {
                terminal.write(String.format("%s, %s", match.getMatchId(), match.hasLabel()), 5, y++);
            }
        }
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
                com.heroiclabs.nakama.Match match = MainClass.aClass.socket.createMatch().get();
                MainClass.aClass.socket.updateStatus(match.getMatchId()).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    @Override
    public boolean finishInput() {
        return false;
    }
}
