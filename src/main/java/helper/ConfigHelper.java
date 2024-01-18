package helper;

import config.LdataParser;
import config.LdataWriter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

/**
 * Utility class to help handling the configuration.
 */
public class ConfigHelper {

    /**
     * A ConfigHeler instance for the tetris configuration File
     */
    public static ConfigHelper tty_config = new ConfigHelper(OsUtil.getConfigFile("tty-tetris.conf"));
    /**
     * A map holding the configuration
     */
    private final Map<String, Object> configuration;

    /**
     * Represents the location of the configuration file.
     */
    private final File configuration_location;

    /**
     * Constructs a ConfigHelper using a specified configuration file.
     *
     * @param filename The File object of the configuration file.
     */
    public ConfigHelper(File filename) {
        this.configuration_location = filename;
        configuration = LdataParser.loadFrom(filename);
        System.out.println(configuration);
    }

    /**
     * Writes the configuration details to a file.
     *
     * @param config The configuration Map to be written into the file.
     */
    @SneakyThrows
    public void writeToConfig (Map<String, Object> config) {
        for (String key : config.keySet()) {
            configuration.put(key, config.get(key));
        }
        LdataWriter.writeTo(false, configuration, new FileWriter(configuration_location));
    }

    /**
     * Retrieves an Object from the configuration Map using the provided key.
     *
     * @param key The key for the desired value in the configuration Map.
     * @return The Object retrieved from the configuration map, or null if there is no mapping for the provided key.
     */
    public Object getObject(String key) {
        return configuration.get(key);
    }

}
