package screens;

import Components.Button;
import Components.SelectionHelper;
import Components.TextInput;
import Helper.TerminalHelper;
import com.googlecode.lanterna.input.KeyStroke;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Session;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import static screens.PlayScreen.tetrisLogo;

public class LoginScreen implements Screen {

    private String errorMessage = "";

    private SelectionHelper helper;

    public LoginScreen() {
        helper = new SelectionHelper(
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
        System.out.println("Start");
        Screen returnScreen = helper.manageKey(key);
        if (returnScreen != null) {
            return returnScreen;
        }
        return this;
    }

    @Override
    public boolean finishInput() {
        try {
            Client client = new DefaultClient("defaultkey", "127.0.0.1", 7349, false);
            Session session = client.authenticateEmail(helper.getTextInput("E-Mail"), helper.getTextInput("Password"), helper.getTextInput("Username")).get();
            System.out.println(session);
            System.out.println(session.getAuthToken()); // raw JWT token
            System.out.println(session.getUserId());
            System.out.println(session.getUsername());
            System.out.println("Session has expired: " + session.isExpired(new Date()));
            System.out.println("Session expires at: " + session.getExpireTime());
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
