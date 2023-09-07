package screens;

import Helper.TerminalHelper;
import com.googlecode.lanterna.input.KeyStroke;
import com.heroiclabs.nakama.api.GroupUserList;
import com.sun.tools.javac.Main;
import nakama.com.google.common.util.concurrent.ListenableFuture;

import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static screens.PlayScreen.tetrisLogo;

// TODO implement friend thing maybe?
public class LobbyWaitingScreen implements Screen, Runnable {

    private final ScheduledExecutorService exec;

    private final HashMap<String, String> playerlist;

    private String groupID;

    private String lobbyName;

    public LobbyWaitingScreen(String groupID, String lobbyName) {
        this.playerlist = new HashMap<>();
        this.groupID = groupID;
        this.lobbyName = lobbyName;
        this.exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        synchronized (playerlist) {
            playerlist.clear();
            try {
                GroupUserList groupUserList = MainClass.aClass.client.listGroupUsers(MainClass.aClass.session, this.groupID).get();
                for (GroupUserList.GroupUser groupUser : groupUserList.getGroupUsersList()) {
                    playerlist.put(groupUser.getUser().getId(), groupUser.getUser().getUsername());
                }
            } catch (Exception ignored) {

            }
        }
    }

    @Override
    public void displayOutput(TerminalHelper terminal) {
        terminal.clear();
        for (int i = 0; i < tetrisLogo.length; i++) {
            terminal.write(tetrisLogo[i], 5, i + 1);
        }
        int y = 10;
        terminal.writeCenter(String.format("<--- LobbyName: %s --->", lobbyName), y++);
        terminal.write("Current Players waiting in the Lobby", 5, y++);
        for (String player : playerlist.values()) {
            terminal.write(player, 5, y++);
        }
    }

    @Override
    public Screen respondToUserInput(KeyStroke key, TerminalHelper terminal) {
        return this;
    }

    @Override
    public boolean finishInput() {
        return false;
    }
}
