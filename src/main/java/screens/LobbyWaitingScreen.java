package screens;

import Helper.TerminalHelper;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.GroupUserList;
import com.heroiclabs.nakama.api.UserGroupList;
import communication.MatchSendHelper;
import communication.Player;
import lombok.extern.slf4j.Slf4j;
import nakama.com.google.common.reflect.TypeToken;
import nakama.com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LobbyWaitingScreen implements Screen, Runnable {

    private final ScheduledExecutorService exec;

    private final String groupID;

    private final String lobbyName;

    private static Player me;

    private static Player opponent;

    private static boolean startGame = false;

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
                MainClass.aClass.match = MainClass.aClass.socket.joinMatch(map.get("MatchID")).get();
            } catch (Exception e) {
                try {
                    throw e;
                } catch (InterruptedException | ExecutionException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        this.groupID = groupID;
        this.lobbyName = lobbyName;
        this.exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
        me = null;
        opponent = null;
    }

    public static void startGame() {
        startGame = true;
    }

    @Override
    public void run() {
        try {
            GroupUserList groupUserList = MainClass.aClass.client.listGroupUsers(MainClass.aClass.session, this.groupID).get();
            for (GroupUserList.GroupUser groupUser : groupUserList.getGroupUsersList()) {
                String userId = groupUser.getUser().getId();
                if (userId.equals(MainClass.aClass.user_id) && (me == null || !me.getPlayerId().equals(userId))) {
                    me = new Player(groupUser.getUser().getId(), groupUser.getUser().getUsername(), MainClass.aClass.createdGroup);
                } else {
                    if (userId.equals(MainClass.aClass.user_id)) {
                        continue;
                    }
                    if (opponent == null || !opponent.getPlayerId().equals(userId))
                        opponent = new Player(groupUser.getUser().getId(), groupUser.getUser().getUsername(), !MainClass.aClass.createdGroup);
                }
            }
            if (groupUserList.getGroupUsersList().size() == 1) {
                opponent = null;
            }
        } catch (Exception ignored) {
        }

    }

    @Override
    public void displayOutput(TerminalHelper terminal) {
        if (startGame) {
            MainClass.aClass.screen = new PlayOnlineScreen(terminal, me.isHost());
            MainClass.aClass.repaint();
            return;
        }
        terminal.clear();
        terminal.writeTetrisLogo();
        int y = 10;
        terminal.writeCenter(String.format("<--- LobbyName: %s --->", lobbyName), y++);
        terminal.write("Current Players waiting in the Lobby", 5, y++);
        y++;
        if (me != null && me.isHost()) {
            if (me.isReady()) {
                terminal.write(me.getDisplayName(), 5, y++, TextColor.ANSI.GREEN);
            } else {
                terminal.write(me.getDisplayName(), 5, y++);
            }
            if (opponent != null) {
                if (opponent.isReady()) {
                    terminal.write(opponent.getDisplayName(), 5, y, TextColor.ANSI.GREEN);
                } else {
                    terminal.write(opponent.getDisplayName(), 5, y);
                }
            }
        } else {
            if (opponent != null) {
                if (opponent.isReady()) {
                    terminal.write(opponent.getDisplayName(), 5, y++, TextColor.ANSI.GREEN);
                } else {
                    terminal.write(opponent.getDisplayName(), 5, y++);
                }
            }
            if (me != null) {
                if (me.isReady()) {
                    terminal.write(me.getDisplayName(), 5, y, TextColor.ANSI.GREEN);
                } else {
                    terminal.write(me.getDisplayName(), 5, y);
                }
            }
        }
        if (bothPlayersAreReady()) {
            if (me.isHost())
                terminal.writeCenter("-- Press [Enter] to start the game --", terminal.getHeightInCharacters());
            else
                terminal.writeCenter("-- Wait for the Host to start the game --", terminal.getHeightInCharacters());

        }
    }

    @Override
    public Screen respondToUserInput(KeyStroke key, TerminalHelper terminal) {
        if (key.getKeyType() == KeyType.Character) {
            switch (Character.toLowerCase(key.getCharacter())) {
                case 'r':
                    me.setReady(!me.isReady());
                    MatchSendHelper.READY.sendUpdate(me.getPlayerId(), me.isReady());
            }
        }
        if (key.getKeyType() == KeyType.Enter) {
            if (bothPlayersAreReady() && me.isHost()) {
                MatchSendHelper.START.sendUpdate();
                PlayOnlineScreen screen = new PlayOnlineScreen(terminal, me.isHost());
                screen.displayOutput(terminal);
                return screen;
            }
        }
        return this;
    }

    private boolean bothPlayersAreReady() {
        if (me != null && opponent != null)
            return me.isReady() && opponent.isReady();
        return false;
    }

    @Override
    public boolean finishInput() {
        return false;
    }

    public static void updatePlayerState(String playerId, boolean state) {
        if (playerId.equals(me.getPlayerId())) {
            me.setReady(state);
        } else if (playerId.equals(opponent.getPlayerId())) {
            opponent.setReady(state);
        }
    }

}
