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

    public static final int textureWidth = 300;
    public static final int textureHeight = 225;

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
        return ((float) id % numberOfRows)/numberOfRows;
    }

    public float getYOffset(int id){
        return (float) Math.floor((double) id / (double) numberOfRows)/numberOfRows;
    }

    private void calculateTextureAtlas(List<RenderTexture> textures) throws IOException {
        // This might now work V
        List<InputStream> tempFiles = textures.stream().map(text -> text.getResource().getInputStream()).collect(Collectors.toList());



        int numOfRows = (int) Math.ceil(Math.sqrt(tempFiles.size()));
        this.numberOfRows = numOfRows;

        int w = textureWidth * numOfRows;
        int h = textureHeight * numOfRows;

        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = combined.getGraphics();

        int i = 0;
        for(int y = 0; y < h; y += textureHeight){
            for(int x = 0; x < w; x+=textureWidth){
                if(i >= tempFiles.size()) break;
                g.drawImage(scale(ImageIO.read(tempFiles.get(i)), textureWidth, textureHeight), x, y, null);
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
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.drawImage(imageToScale, 0, 0, dWidth, dHeight, null);
            graphics2D.dispose();
        }
        return scaledImage;
    }

    public Texture getTexture(){
        return texture;
    }
}
