package screens;

import Helper.TerminalHelper;

import com.heroiclabs.nakama.api.Group;
import com.heroiclabs.nakama.api.Rpc;
import lombok.extern.slf4j.Slf4j;
import nakama.com.google.gson.Gson;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
public class LobbyCreateScreen implements Screen {
    private final int selected = 0;
    private String groupName = "";

    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.clear();
        TerminalHelper.writeTetrisLogo(terminal);
        int y = 10;
        terminal.write("LobbyName: " + groupName, 5, y);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_1234567890";
        if (key.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (groupName.length() > 0) {
                groupName = groupName.substring(0, groupName.length() - 1);
            }
        } else if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            String desc = "Will be ignored anyways";
            String avatarURL = "";
            String langTag = "";
            boolean open = true;
            int maxSize = 2;
            try {
                Group group = MainClass.aClass.client.createGroup(MainClass.aClass.session, groupName, desc, avatarURL, langTag, open, maxSize).get();
                MainClass.aClass.group_id = group.getId();
                MainClass.aClass.createdGroup = true;
                MainClass.aClass.match = MainClass.aClass.socket.createMatch().get();
                Map<String, Object> payload = new HashMap<>();
                payload.put("GroupId", group.getId());
                payload.put("MatchID", MainClass.aClass.match.getMatchId());
                Rpc rpcResult = MainClass.aClass.client.rpc(MainClass.aClass.session, "UpdateGroupMetadata", new Gson().toJson(payload, payload.getClass())).get();
                log.debug(rpcResult.toString());
                LobbyWaitingScreen waitingScreen = new LobbyWaitingScreen(group.getId(), groupName, true);
                waitingScreen.displayOutput(terminal);
                return waitingScreen;
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage());
            }
        } else {
            char c = key.getKeyChar();
            if (letters.indexOf(c) > -1) {
                groupName += c;
            }
            if (key.getKeyChar() == ' ') {
                groupName += '_';
            }

        }
        return this;
    }

    @Override
    public boolean isInsideInputField() {
        return true;
    }


    @Override
    public boolean finishInput() {
        return false;
    }
}
