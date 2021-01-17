package org.kakara.game.resources;


import org.kakara.core.common.resources.TextureResolution;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResourceCheckList {
    private Set<TextureResolution> checkedList = new HashSet<>();

    public ResourceCheckList() {

    }

    public void checked(TextureResolution resolution) {
        checkedList.add(resolution);
    }

    public boolean hasBeenChecked(TextureResolution resolution) {
        return checkedList.contains(resolution);
    }

    public boolean hasAllBeenChecked() {
        List<TextureResolution> resolutions = Arrays.asList(TextureResolution.values());
        return checkedList.containsAll(resolutions);

    }

    public int size() {
        return checkedList.size();
    }
}
