package org.kakara.client.scenes.canvases;

import org.kakara.client.game.ClientResourceManager;
import org.kakara.client.game.player.PlayerContentInventory;
import org.kakara.client.scenes.maingamescene.RenderResourceManager;
import org.kakara.core.Kakara;
import org.kakara.core.NameKey;
import org.kakara.core.game.Block;
import org.kakara.core.game.Item;
import org.kakara.core.game.ItemStack;
import org.kakara.core.resources.Texture;
import org.kakara.core.resources.TextureResolution;
import org.kakara.engine.GameHandler;
import org.kakara.engine.engine.CubeData;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.KeyPressEvent;

import org.kakara.engine.gameitems.mesh.AtlasMesh;
import org.kakara.engine.gameitems.mesh.Mesh;
import org.kakara.engine.renderobjects.RenderTexture;
import org.kakara.engine.renderobjects.TextureAtlas;
import org.kakara.engine.renderobjects.renderlayouts.BlockLayout;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.ui.RGBA;
import org.kakara.engine.ui.UserInterface;
import org.kakara.engine.ui.components.Panel;
import org.kakara.engine.ui.components.shapes.Rectangle;
import org.kakara.engine.ui.components.text.Text;
import org.kakara.engine.ui.font.Font;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.ui.items.ObjectCanvas;
import org.kakara.engine.ui.objectcanvas.UIObject;
import org.kakara.game.items.blocks.AirBlock;
import org.kakara.game.resources.GameResourceManager;

import java.util.concurrent.ExecutionException;

public class HotBarCanvas extends ComponentCanvas {
    private Panel mainPanel;
    private Rectangle[] rects = new Rectangle[5];
    private int selectedIndex = 0;
    private final RGBA selected = new RGBA(255, 255, 255, 0.4f);
    private final RGBA normal = new RGBA(0, 0, 0, 0.4f);
    private PlayerContentInventory contentInventory;

    private ObjectCanvas objectCanvas;
    private ComponentCanvas numberCanvas;

    private final RenderResourceManager renderTextureCache;
    private final Scene scene;
    private final TextureAtlas atlas;
    private Font roboto;

    public HotBarCanvas(Scene scene, TextureAtlas atlas, RenderResourceManager renderTextureCache, PlayerContentInventory contentInventory, Font roboto) {
        super(scene);
        this.scene = scene;
        this.atlas = atlas;
        this.contentInventory = contentInventory;
        scene.getEventManager().registerHandler(this);

        this.renderTextureCache = renderTextureCache;

        mainPanel = new Panel();

        for (int i = 0; i < 5; i++) {
            Rectangle rect = new Rectangle();
            rect.setScale(50, 50);
            rect.setColor(normal);
            rect.setPosition(400 + (55 * i), 670);
            rects[i] = rect;
            mainPanel.add(rects[i]);
        }

        rects[0].setColor(selected);

        numberCanvas = new ComponentCanvas(scene);

        add(mainPanel);
        this.roboto = roboto;
        renderItems();
    }

    public void update() {
        boolean update = false;
        for (int i = 0; i < contentInventory.getHotBarContents().length; i++) {
            if (contentInventory.getItemStack(i).getCount() <= 0) {
                contentInventory.setItemStack(Kakara.createItemStack(Kakara.getItemManager().getItem(0).get()), i);
                update = true;
            }
        }
        if (update) renderItems();
    }

    @Override
    public void render(UserInterface hud, GameHandler handler) {
        super.render(hud, handler);
        objectCanvas.render(hud, handler);
        numberCanvas.render(hud, handler);
    }

    public void renderItems() {
        if (objectCanvas != null) {
            objectCanvas.clearObjects();
            numberCanvas.clearComponents();
        }
        try {
            if (objectCanvas == null) {
                objectCanvas = new ObjectCanvas(scene);
            }
            for (int i = 0; i < 5; i++) {
                ItemStack itemStack = contentInventory.getHotBarContents()[i];
                Item item = itemStack.getItem();

                if (item instanceof AirBlock) continue;
                UIObject uiObject;
                if (item instanceof Block) {
                    RenderTexture txt = getTexture(itemStack);
                    AtlasMesh mesh = new AtlasMesh(txt, atlas, new BlockLayout(), CubeData.vertex, CubeData.normal, CubeData.indices);
                    uiObject = new UIObject(mesh);
                    objectCanvas.add(uiObject);
                } else {
                    ClientResourceManager resourceManager = (ClientResourceManager) Kakara.getResourceManager();
                    Mesh[] mesh = resourceManager.getModel(item.getModel(), item.getTexture(), item.getMod());
                    //TODO @Ryandw11 - We need to be able the pass all the meshes
                    uiObject = new UIObject(mesh[0]);
                }
                uiObject.setPosition(400 + 25 + (55 * i), 670 + 25);

                uiObject.setScale(25);

                uiObject.getRotation().rotateX((float) Math.toRadians(50));
                uiObject.getRotation().rotateY((float) Math.toRadians(40));

                Text itemCountTxt = new Text(itemStack.getCount() + "", roboto);
                itemCountTxt.setPosition(400 + (55 * i), 670 + 23);
                itemCountTxt.setSize(25);
                numberCanvas.add(itemCountTxt);
            }

        } catch (ExecutionException ee) {
            ee.printStackTrace();
        }
    }

    private RenderTexture getTexture(ItemStack is) throws ExecutionException {
        return renderTextureCache.get(GameResourceManager.correctPath(Kakara.getResourceManager().getTexture(is.getItem().getTexture(), TextureResolution._16, is.getItem().getMod()).getLocalPath()));
    }

    public RenderTexture getCurrentItem() {
        if (contentInventory.getHotBarContents()[selectedIndex] instanceof AirBlock) {
            return null;
        }
        try {
            return getTexture(contentInventory.getHotBarContents()[selectedIndex]);
        } catch (ExecutionException ee) {
            ee.printStackTrace();
            return null;
        }
    }

    public ItemStack getCurrentItemStack() {
        return contentInventory.getHotBarContents()[selectedIndex];
    }

    @EventHandler
    public void onKeyHit(KeyPressEvent evt) {
        int number;
        try {
            number = Integer.parseInt(evt.getKeyName());
        } catch (NumberFormatException ex) {
            return;
        }

        if (number < 0 || number > 6) return;

        number--;

        rects[selectedIndex].setColor(normal);
        selectedIndex = number;
        rects[number].setColor(selected);
    }

    public void show() {
        mainPanel.setVisible(true);
    }

    public void hide() {
        mainPanel.setVisible(false);
    }

//    public ObjectCanvas getObjectCanvas() {
//        return objectCanvas;
//    }

    public PlayerContentInventory getContentInventory() {
        return contentInventory;
    }
}
