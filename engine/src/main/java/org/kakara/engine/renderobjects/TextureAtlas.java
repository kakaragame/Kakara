package org.kakara.engine.renderobjects;

import org.kakara.engine.GameEngine;
import org.kakara.engine.GameHandler;
import org.kakara.engine.item.Texture;
import org.kakara.engine.resources.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TextureAtlas {
    private String output;
    private List<RenderTexture> textures;

    private Texture texture;

    private int numberOfRows;

    public TextureAtlas(List<RenderTexture> textures, String output){
        this.textures = textures;
        this.output = output;
        try {
            calculateTextureAtlas(this.textures);
        }catch(IOException ex){
            GameEngine.LOGGER.error("Could not create texture atlas. Missing texture files?");
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
        return ((float) id % this.numberOfRows) / numberOfRows;
    }

    private void calculateTextureAtlas(List<RenderTexture> textures) throws IOException {
        // This might now work V
        List<File> tempFiles = textures.stream().map(text -> new File(text.getResource().getPath())).collect(Collectors.toList());


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
        this.texture = new Texture(rm.getResource(this.output + File.separator + "textureAtlas.png").getByteBuffer());
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
