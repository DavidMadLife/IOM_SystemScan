package org.chemtrovina.iom_systemscan.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.chemtrovina.iom_systemscan.model.FxmlPage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FXMLCacheManager {
    private static final Map<String, FxmlPage> cache = new HashMap<>();

    public static FxmlPage getPage(String fxmlPath) throws IOException {
        if (cache.containsKey(fxmlPath)) {
            return cache.get(fxmlPath);
        } else {
            FXMLLoader loader = new FXMLLoader(FXMLCacheManager.class.getResource(fxmlPath));
            Parent view = loader.load();
            Object controller = loader.getController();
            FxmlPage page = new FxmlPage(view, controller);
            cache.put(fxmlPath, page);
            return page;
        }
    }

    public static void clearCache() {
        cache.clear();
    }

    public static void removePage(String fxmlPath) {
        cache.remove(fxmlPath);
    }
}

