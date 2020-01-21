package org.kakara.engine.resources;

import org.kakara.engine.utils.Utils;

import java.io.File;
import java.net.MalformedURLException;

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
    public Resource getResource(String resourcePath) throws Exception {
        File externalResource = new File(externalLocation, resourcePath.replace("/", File.separator));
        if (externalResource.exists()) {

            return new LocalResource(externalResource.toURI().toURL());
        } else {
            String internalResource = internalLocation + resourcePath;
            System.out.println(internalResource);
            return new LocalResource(ResourceManager.class.getResource(internalResource));
        }
    }


    public Resource getExternalResource(String url) {
        return null;
    }
}
