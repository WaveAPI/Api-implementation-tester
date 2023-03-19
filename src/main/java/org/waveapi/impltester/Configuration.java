package org.waveapi.impltester;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

public class Configuration {
    private final String api;
    private final Map<String, String> implementations;

    public Configuration(File file) {
        try {
            Map<String, Object> map = (Map<String, Object>) new Yaml().load(new FileReader(file));

            api = (String) map.get("api");
            implementations = (Map<String, String>) map.get("implementations");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getApiUrl() {
        return api;
    }

    public Map<String, String> getImplementations() {
        return implementations;
    }
}
