package screens;

import Helper.TerminalHelper;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupList;
import communication.MatchSendHelper;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
public class LobbyScreen implements Screen, Runnable {
    private static Map<String, String> lobbies = new LinkedHashMap<>();
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
    public void displayOutput(TerminalHelper terminal) {
        terminal.clear();
        terminal.writeTetrisLogo();
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
            MainClass.aClass.createSocket();
                synchronized (runnable) {
                    runnable.set(false);
                }
                LobbyCreateScreen screen = new LobbyCreateScreen();
                screen.displayOutput(terminal);
                return screen;
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
                MainClass.aClass.createSocket();
                int c = 0;
                for (String group_id : lobbies.keySet()) {
                    if (c == selected) {
                        try {
                            MainClass.aClass.client.joinGroup(MainClass.aClass.session, group_id).get();
                            synchronized (runnable) {
                                runnable.set(false);
                            }
                            //TODO figure out why this does not go to the next screen
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
            System.out.println(runnable);
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
