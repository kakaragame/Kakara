package org.kakara.client.scenes;

import org.kakara.core.mod.Mod;

import java.util.Objects;

public class ResourceObject {
    private Mod mod;
    private String resource;

    public ResourceObject(Mod mod, String resource) {
        this.mod = mod;
        this.resource = resource;
    }

    public Mod getMod() {
        return mod;
    }

    public String getResource() {
        return resource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceObject that = (ResourceObject) o;
        return mod.equals(that.getMod()) &&
                Objects.equals(getResource(), that.getResource());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMod(), getResource());
    }
}
