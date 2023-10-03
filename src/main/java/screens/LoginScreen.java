package screens;

import Components.Button;
import Components.SelectionHelper;
import Components.TextInput;
import Helper.TerminalHelper;
import com.googlecode.lanterna.input.KeyStroke;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import static screens.PlayScreen.tetrisLogo;

@Slf4j
public class LoginScreen implements Screen {

    private String errorMessage = "";

    private SelectionHelper helper;

    public LoginScreen() {
        helper = new SelectionHelper(
                new TextInput("Server-IP", false, false).setDefaultInput("127.0.0.1"),
                new TextInput("E-Mail", false, false),
                new TextInput("Password", false, true),
                new TextInput("Username", false, false),
                new Button("Login", this, new LobbyScreen())
        );
    }

    @Override
    public void displayOutput(TerminalHelper terminal) {
        terminal.clear();
        for (int i = 0; i < tetrisLogo.length; i++) {
            terminal.write(tetrisLogo[i], 5, i + 1);
        }
        helper.drawAllComponents(terminal, 5, 10);

        if (errorMessage.length() > 0) {
            terminal.write("ERROR: ", 1, 15);
            terminal.write(errorMessage, 4, 16);
        }
        terminal.hideCursor();
    }

    @Override
    public Screen respondToUserInput(KeyStroke key, TerminalHelper terminal) {
        Screen returnScreen = helper.manageKey(key);
        if (returnScreen != null) {
            return returnScreen;
        }
        return this;
    }

    @Override
    public boolean finishInput() {
        try {
            Client client = new DefaultClient("defaultkey", helper.getTextInput("Server-IP"), 7349, false);
            Session session = client.authenticateEmail(helper.getTextInput("E-Mail"), helper.getTextInput("Password"), helper.getTextInput("Username")).get();
            log.debug(session.toString());
            log.debug(session.getAuthToken()); // raw JWT token
            log.debug(session.getUserId());
            log.debug(session.getUsername());
            log.debug("Session has expired: " + session.isExpired(new Date()));
            log.debug("Session expires at: " + session.getExpireTime());
            MainClass.aClass.session = session;
            MainClass.aClass.client = client;
//            MainClass.aClass.screen = new LobbyScreen();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            errorMessage = e.getMessage();
            errorMessage = errorMessage.substring(errorMessage.indexOf(" ") + 1);
            return false;
        }
    }
}
