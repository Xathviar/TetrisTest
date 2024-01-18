package screens;

import helper.ConfigHelper;
import helper.TerminalHelper;

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

/**
 * The LoginScreen class represents a screen for user login in a terminal-based application.
 * It implements the Screen interface.
 */
@Slf4j
public class LoginScreen implements Screen {

    /**
     * Represents the error message.
     *
     * <p>
     * The {@code errorMessage} variable is used to store the error message
     * associated with
     */
    private String errorMessage = "";

    /**
     * Represents a helper object for selection functionality.
     * This helper object is used internally within the class and can only be accessed within the class.
     * It is marked as final meaning its value cannot be changed once initialized.
     */
    private final SelectionHelper helper;

    /**
     * Represents a login screen for the application.
     * <p>
     * This screen allows users to enter their server IP, email, password, username, and configure
     * whether to save login configuration. It also provides a login button to initiate the login process.
     * </p>
     * <p>
     * The login screen is initialized with default configuration values. The server IP is set to "127.0.0.1".
     * </p>
     * <p>
     * The login screen also reads login configuration from a configuration file if available.
     * </p>
     *
     * @since 1.0
     */
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

    /**
     * Writes the configuration data to the config file if the "Save Configuration" checkbox is selected.
     * The method retrieves the relevant data from the UI components and saves it in a {@code Map} object.
     * The data is then passed to the {@code writeToConfig} method in the {@code tty_config} object of the {@code ConfigHelper} class.
     * <p>
     * Note: If the "Save Configuration" checkbox is not selected, nothing is written to the config file.
     */
    public void writeConfig() {
        if (((Checkbox) helper.getComponentByLabel("Save Configuration")).isState()) {
            Map<String, Object> data = new HashMap<>();
            data.put("server-ip", ((TextInput) helper.getComponentByLabel("Server-IP")).getInput());
            data.put("e-mail", ((TextInput) helper.getComponentByLabel("E-Mail")).getInput());
            data.put("username", ((TextInput) helper.getComponentByLabel("Username")).getInput());
            ConfigHelper.tty_config.writeToConfig(data);
        }
    }

    /**
     * Reads configuration values from a config file and sets them in the appropriate components.
     * The configuration values are read using the ConfigHelper class and stored in local variables.
     * The corresponding components are then found using the helper object and their values are set
     * based on the configuration values.
     */
    public void readFromConfig() {
        String server_ip = (String) ConfigHelper.tty_config.getObject("server-ip");
        String e_mail = (String) ConfigHelper.tty_config.getObject("e-mail");
        String username = (String) ConfigHelper.tty_config.getObject("username");
        ((TextInput) helper.getComponentByLabel("Server-IP")).setInput(server_ip);
        ((TextInput) helper.getComponentByLabel("E-Mail")).setInput(e_mail);
        ((TextInput) helper.getComponentByLabel("Username")).setInput(username);
    }

    /**
     * Clears the terminal window and displays the Tetris logo and all the components
     * at position (5, 10) in the AsciiPanel.
     * If an error message is set, it will be displayed at position (1, 15) with the
     * prefix "ERROR: ".
     *
     * @param terminal the AsciiPanel instance to display the output
     */
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

    /**
     * Responds to the user's input and returns the next screen to display.
     *
     * @param key      the KeyEvent object representing the key pressed by the user
     * @param terminal the AsciiPanel object representing the terminal
     * @return the next screen to display
     */
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

    /**
     * Finishes the user input by writing the configuration, authenticating the client, and setting the session.
     *
     * @return true if the input is successfully finished,*/
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

    /**
     * Checks if the current element is inside an input field.
     *
     * @return true if the element is inside an input field, false otherwise.
     */
    @Override
    public boolean isInsideInputField() {
        return helper.isSelected();
    }
}
