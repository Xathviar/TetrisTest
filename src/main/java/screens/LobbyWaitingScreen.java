package screens;

import Helper.TerminalHelper;
import com.googlecode.lanterna.input.KeyStroke;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupUserList;
import com.heroiclabs.nakama.api.UserGroupList;
import nakama.com.google.common.reflect.TypeToken;
import nakama.com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static screens.PlayScreen.tetrisLogo;

// TODO implement friend thing maybe?
public class LobbyWaitingScreen implements Screen, Runnable {

    private final ScheduledExecutorService exec;

    private final LinkedHashMap<String, String> playerList;

    private final String groupID;

    private final String lobbyName;

    public LobbyWaitingScreen(String groupID, String lobbyName, boolean createdLobby) {
        if (!createdLobby) {
            MainClass.aClass.group_id = groupID;
            try {
                MainClass.aClass.createdGroup = false;
                UserGroupList userGroups = MainClass.aClass.client.listUserGroups(MainClass.aClass.session, MainClass.aClass.session.getUserId()).get();
                Group group = userGroups.getUserGroups(0).getGroup();
                Gson gson = new Gson();
                String s = group.getMetadata();
                Type type = new TypeToken<HashMap<String, String>>() {
                }.getType();
                HashMap<String, String> map = gson.fromJson(s, type);
                Match match = MainClass.aClass.socket.joinMatch(map.get("MatchID")).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        this.playerList = new LinkedHashMap<>();
        this.groupID = groupID;
        this.lobbyName = lobbyName;
        this.exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        synchronized (playerList) {
            playerList.clear();
            try {
                GroupUserList groupUserList = MainClass.aClass.client.listGroupUsers(MainClass.aClass.session, this.groupID).get();
                for (GroupUserList.GroupUser groupUser : groupUserList.getGroupUsersList()) {
                    playerList.put(groupUser.getUser().getId(), groupUser.getUser().getUsername());
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
        for (String player : playerList.values()) {
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
