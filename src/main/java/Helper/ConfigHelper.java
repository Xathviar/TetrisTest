package Helper;

import config.LdataParser;
import config.LdataWriter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class ConfigHelper {

    public static ConfigHelper tty_config = new ConfigHelper(OsUtil.getConfigFile("tty-tetris.conf"));
    private final Map<String, Object> configuration;

    private final File configuration_location;

    public ConfigHelper(File filename) {
        this.configuration_location = filename;
        configuration = LdataParser.loadFrom(filename);
        System.out.println(configuration);
    }

    @SneakyThrows
    public void writeToConfig (Map<String, Object> config) {
        for (String key : config.keySet()) {
            configuration.put(key, config.get(key));
        }
        LdataWriter.writeTo(false, configuration, new FileWriter(configuration_location));
    }

    public Object getObject(String key) {
        return configuration.get(key);
    }

}
