package screens;

import Helper.ConfigHelper;
import Helper.TerminalHelper;

import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;
import components.Button;
import components.Checkbox;
import components.SelectionHelper;
import components.TextInput;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
public class LoginScreen implements Screen {

    private String errorMessage = "";

    private final boolean isInsideInput = false;

    private final SelectionHelper helper;

    public LoginScreen() {
        helper = new SelectionHelper(
                new TextInput("Server-IP", false, false).setDefaultInput("127.0.0.1"),
                new TextInput("E-Mail", false, false),
                new TextInput("Password", false, true),
                new TextInput("Username", false, false),
                new Checkbox("Save Configuration", this),
                new Button("Login", this, new LobbyScreen())
        );
        readFromConfig();
    }

    public void writeConfig() {
        if (((Checkbox) helper.getComponentByLabel("Save Configuration")).isState()) {
            Map<String, Object> data = new HashMap<>();
            data.put("server-ip", ((TextInput) helper.getComponentByLabel("Server-IP")).getInput());
            data.put("e-mail", ((TextInput) helper.getComponentByLabel("E-Mail")).getInput());
            data.put("username", ((TextInput) helper.getComponentByLabel("Username")).getInput());
            ConfigHelper.tty_config.writeToConfig(data);
        }
    }

    public void readFromConfig() {
//        Map<String, Object> config = LdataParser.loadFrom(OsUtil.getConfigFile("tty-tetris.conf"));
        String server_ip = (String) ConfigHelper.tty_config.getObject("server-ip");
        String e_mail = (String) ConfigHelper.tty_config.getObject("e-mail");
        String username = (String) ConfigHelper.tty_config.getObject("username");
        ((TextInput) helper.getComponentByLabel("Server-IP")).setInput(server_ip);
        ((TextInput) helper.getComponentByLabel("E-Mail")).setInput(e_mail);
        ((TextInput) helper.getComponentByLabel("Username")).setInput(username);
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.clear();
        TerminalHelper.writeTetrisLogo(terminal);
        helper.drawAllComponents(terminal, 5, 10);
        if (errorMessage.length() > 0) {
            terminal.write("ERROR: ", 1, 15);
            terminal.write(errorMessage, 4, 16);
        }
    }

    @Override
    public Screen respondToUserInput(KeyEvent key, AsciiPanel terminal) {
        Screen returnScreen = helper.manageKey(key);
        if (returnScreen != null) {
            LobbyScreen screen = (LobbyScreen) returnScreen;
            screen.startThread();
            return returnScreen;
        }
        return this;
    }

    @Override
    public boolean finishInput() {
        try {
            writeConfig();
            Client client = new DefaultClient("defaultkey", helper.getTextInput("Server-IP"), 7349, false);
            Session session = client.authenticateEmail(helper.getTextInput("E-Mail"), helper.getTextInput("Password"), helper.getTextInput("Username")).get();
            log.debug(session.toString());
            log.debug(session.getAuthToken());
            log.debug(session.getUserId());
            log.debug(session.getUsername());
            log.debug("Session has expired: " + session.isExpired(new Date()));
            log.debug("Session expires at: " + session.getExpireTime());
            MainClass.aClass.session = session;
            MainClass.aClass.client = client;
            MainClass.aClass.user_id = session.getUserId();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            errorMessage = e.getMessage();
            errorMessage = errorMessage.substring(errorMessage.indexOf(" ") + 1);
            return false;
        }
    }

    @Override
    public boolean isInsideInputField() {
        return helper.isSelected();
    }
}
