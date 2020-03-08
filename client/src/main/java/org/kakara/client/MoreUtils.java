package org.kakara.client;

import org.kakara.core.resources.Resource;
import org.kakara.core.world.Location;
import org.kakara.engine.math.Vector3;

import java.net.MalformedURLException;
import java.nio.ByteBuffer;

public class MoreUtils {
    public static String[] stringArrayToStringArray(String property) {
        return property.split(",");
    }

    public static Vector3 locationToVector3(Location c) {
        return new Vector3(c.getX(), c.getY(), c.getZ());
    }

    public static org.kakara.engine.resources.Resource coreResourceToEngineResource(Resource resource, KakaraGame kakaraGame) {
        try {
            return kakaraGame.getGameHandler().getResourceManager().getResource(resource.getLocalPath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
