package org.kakara.engine.renderobjects;

import org.kakara.engine.GameEngine;
import org.kakara.engine.GameHandler;
import org.kakara.engine.item.Texture;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.scene.Scene;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class TextureAtlas {
    private String output;
    private List<RenderTexture> textures;

    private Texture texture;

    private int numberOfRows;

    private Scene currentScene;

    public TextureAtlas(List<RenderTexture> textures, String output, Scene currentScene){
        this.textures = textures;
        this.output = output;
        this.currentScene = currentScene;
        try {
            calculateTextureAtlas(this.textures);
        }catch(IOException ex){
            GameEngine.LOGGER.error("Could not create texture atlas. Missing texture files?", ex);
        }
    }

    public List<RenderTexture> getTextures(){
        return textures;
    }

    public int getNumberOfRows(){
        return numberOfRows;
    }

    public float getXOffset(int id){
        return (float) id / this.numberOfRows;
    }

    public float getYOffset(int id){
        return (float) Math.floor((double) id / (double) numberOfRows);
    }

    private void calculateTextureAtlas(List<RenderTexture> textures) throws IOException {
        // This might now work V
        List<InputStream> tempFiles = textures.stream().map(text -> text.getResource().getInputStream()).collect(Collectors.toList());



        int numOfRows = (int) Math.ceil(Math.sqrt(tempFiles.size()));
        this.numberOfRows = numOfRows;
        int w = 500 * numOfRows;
        int h = 375 * numOfRows;

        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = combined.getGraphics();

        int i = 0;
        for(int y = 0; y < h; y += 375){
            for(int x = 0; x < w; x+=500){
                if(i >= tempFiles.size()) break;
                g.drawImage(scale(ImageIO.read(tempFiles.get(i)), 500, 375), x, y, null);
                this.textures.get(i).init(i, this.getXOffset(i), this.getYOffset(i));
                i++;
            }
        }

        ImageIO.write(combined, "PNG", new File(this.output, "textureAtlas.png"));
        g.dispose();
        ResourceManager rm = GameHandler.getInstance().getResourceManager();
        this.texture = new Texture(this.output + File.separator + "textureAtlas.png", currentScene);
    }

    private BufferedImage scale(BufferedImage imageToScale, int dWidth, int dHeight) {
        BufferedImage scaledImage = null;
        if (imageToScale != null) {
            scaledImage = new BufferedImage(dWidth, dHeight, imageToScale.getType());
            Graphics2D graphics2D = scaledImage.createGraphics();
            graphics2D.drawImage(imageToScale, 0, 0, dWidth, dHeight, null);
            graphics2D.dispose();
        }
        return scaledImage;
    }

    public Texture getTexture(){
        return texture;
    }
}
