package org.kakara.engine.item;

import org.kakara.engine.engine.CubeData;

/**
 * Handles the skybox
 */
public class SkyBox extends MeshGameItem {

    public SkyBox(Texture skyBoxTexture) throws Exception {
        Mesh skyBoxMesh = new Mesh(CubeData.skyboxVertex, CubeData.texture, CubeData.normal, CubeData.indices);
        skyBoxMesh.setMaterial(new Material(skyBoxTexture, 0f));
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0);
    }

    /**
     * Change the current texture of the skybox
     * @param tx The texture to change to.
     */
    public void setTexture(Texture tx){
        this.getMesh().getMaterial().getTexture().cleanup();
        this.getMesh().setMaterial(new Material(tx, 0f));
    }

}
