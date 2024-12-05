package config;

import java.io.FileInputStream;
import java.util.Properties;

public class AppConfig {
    private Properties properties = new Properties();

    public AppConfig(String configPath) throws Exception {
        properties.load(new FileInputStream(configPath));
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}
