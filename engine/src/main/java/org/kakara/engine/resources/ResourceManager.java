package org.kakara.engine.resources;

import org.kakara.engine.GameEngine;
import org.kakara.engine.utils.Utils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.MissingResourceException;

public class ResourceManager {
    private String internalLocation;
    private File externalLocation;

    public ResourceManager() {
        this("/resources/", new File(Utils.getCurrentDirectory(), "resources"));
    }

    public ResourceManager(String internalLocation, File externalLocation) {
        this.internalLocation = internalLocation;
        this.externalLocation = externalLocation;
    }

    /**
     * use / as the path seperator. it will be handled for different systems if needed
     *
     * @param resourcePath Path to the resource
     * @return path to the resource
     */
    public Resource getResource(String resourcePath) throws MalformedURLException {
        File externalResource = new File(externalLocation, resourcePath.replace("/", File.separator));
        GameEngine.LOGGER.debug(externalResource.getAbsolutePath());
        if (externalResource.exists()) {

            return new FileResource(externalResource.toURI().toURL());
        } else {
            String location = internalLocation + resourcePath;
            location = location.replace("//", "/");
            GameEngine.LOGGER.debug(location);
            return new JarResource(location);
        }
    }


    public Resource getExternalResource(String url) {
        return null;
    }
}
