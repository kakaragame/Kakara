package org.kakara.client.scenes.canvases;

import org.kakara.client.KakaraGame;
import org.kakara.client.local.game.ClientResourceManager;
import org.kakara.client.local.game.player.PlayerContentInventory;
import org.kakara.client.scenes.maingamescene.MainGameScene;
import org.kakara.client.scenes.maingamescene.RenderResourceManager;
import org.kakara.core.common.Kakara;
import org.kakara.core.common.game.Block;
import org.kakara.core.common.game.Item;
import org.kakara.core.common.game.ItemStack;
import org.kakara.core.common.resources.TextureResolution;
import org.kakara.engine.GameHandler;
import org.kakara.engine.engine.CubeData;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.KeyPressEvent;
import org.kakara.engine.gameitems.mesh.AtlasMesh;
import org.kakara.engine.gameitems.mesh.Mesh;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.ui.UserInterface;
import org.kakara.engine.ui.canvases.ComponentCanvas;
import org.kakara.engine.ui.canvases.ObjectCanvas;
import org.kakara.engine.ui.components.Panel;
import org.kakara.engine.ui.components.shapes.Rectangle;
import org.kakara.engine.ui.components.text.Text;
import org.kakara.engine.ui.font.Font;
import org.kakara.engine.ui.objectcanvas.UIObject;
import org.kakara.engine.utils.RGBA;
import org.kakara.engine.voxels.TextureAtlas;
import org.kakara.engine.voxels.VoxelTexture;
import org.kakara.engine.voxels.layouts.BlockLayout;
import org.kakara.game.items.blocks.AirBlock;
import org.kakara.game.resources.GameResourceManager;

import java.util.concurrent.ExecutionException;

public class HotBarCanvas extends ComponentCanvas {
    private final RGBA selected = new RGBA(255, 255, 255, 0.4f);
    private final RGBA normal = new RGBA(0, 0, 0, 0.4f);
    private final RenderResourceManager renderTextureCache;
    private final MainGameScene scene;
    private final TextureAtlas atlas;
    private final Panel mainPanel;
    private final Rectangle[] hotbarRectangles = new Rectangle[5];
    private int selectedIndex = 0;
    private final PlayerContentInventory contentInventory;
    private ObjectCanvas objectCanvas;
    private final ComponentCanvas numberCanvas;
    private final Font roboto;
    private boolean enabled = true;

    // Used for the delay with the scroll.
    private long previousScrollTime = 0;

    /**
     * Construct the HotBar Canvas.
     *
     * @param scene              The scene. (This must be the MainGameScene).
     * @param atlas              The texture atlas.
     * @param renderTextureCache The render texture cache.
     * @param contentInventory   The content of the inventory.
     * @param roboto             The font.
     */
    public HotBarCanvas(Scene scene, TextureAtlas atlas, RenderResourceManager renderTextureCache, PlayerContentInventory contentInventory, Font roboto) {
        super(scene);
        this.scene = (MainGameScene) scene;
        this.atlas = atlas;
        this.contentInventory = contentInventory;
        scene.getEventManager().registerHandler(this);

        this.renderTextureCache = renderTextureCache;

        mainPanel = new Panel();
        mainPanel.setAllowOverflow(true);

        for (int i = 0; i < 5; i++) {
            Rectangle rect = new Rectangle();
            rect.setScale(50, 50);
            rect.setColor(normal);
            rect.setPosition(400 + (55 * i), 670);
            hotbarRectangles[i] = rect;
            mainPanel.add(hotbarRectangles[i]);
        }

        hotbarRectangles[0].setColor(selected);

        numberCanvas = new ComponentCanvas(scene);
        numberCanvas.setAutoScale(false);
        numberCanvas.setTag("hot_bar_number_canvas");
        add(mainPanel);
        this.roboto = roboto;
        renderItems();
        setAutoScale(false);
        setTag("hotbar_canvas");

        GameHandler.getInstance().getMouseInput().addScrollCallback((xChange, yChange) -> {
            if (this.scene.getChatComponent().isFocused())
                return;

            // A delay of 30 ms.
            if (System.currentTimeMillis() - previousScrollTime < 30)
                return;
            previousScrollTime = System.currentTimeMillis();

            int number = selectedIndex;
            if (yChange < 0) {
                number++;
            } else if (yChange > 0) {
                number--;
            }

            if (number < 0)
                number = 4;
            if (number > 4)
                number = 0;

            hotbarRectangles[selectedIndex].setColor(normal);
            selectedIndex = number;
            hotbarRectangles[number].setColor(selected);
        });
    }

    public void update() {
        if (!enabled) return;

        boolean update = false;
/*        for (int i = 0; i < contentInventory.getHotBarContents().length; i++) {
            if (contentInventory.getContainer().getItemStack(i).getCount() <= 0) {
                //TODO rewrite this code to have Server and Client support
                ((ServerBoxedInventoryContainer) contentInventory.getContainer()).setItemStack(i, ((ServerGameInstance) Kakara.getGameInstance()).createItemStack(Kakara.getGameInstance().getItemManager().getItem(0)));
                update = true;
            }
        }*/
        if (update) renderItems();
    }

    @Override
    public void init(UserInterface userInterface, GameHandler handler) {
        if (!enabled) return;
        super.init(userInterface, handler);
        objectCanvas.init(userInterface, handler);
        numberCanvas.init(userInterface, handler);
    }

    @Override
    public void render(UserInterface hud, GameHandler handler) {
        if (!enabled) return;
        super.render(hud, handler);
        objectCanvas.render(hud, handler);
        numberCanvas.render(hud, handler);
    }

    public void renderItems() {
        if (objectCanvas != null) {
            objectCanvas.clearObjects();
            numberCanvas.clearComponents();
        }
        if (contentInventory == null) {
            KakaraGame.LOGGER.warn("No Player Inventory Found");
            enabled = false;
            return;
        }
        try {
            if (objectCanvas == null) {
                objectCanvas = new ObjectCanvas(scene);
                objectCanvas.setAutoScale(false);
                objectCanvas.setTag("hotbar_object_canvas");
            }
            for (int i = 0; i < 5; i++) {
                ItemStack itemStack = contentInventory.getHotBarContents()[i];
                Item item = itemStack.getItem();

                if (item instanceof AirBlock) continue;
                UIObject uiObject;
                if (item instanceof Block) {
                    VoxelTexture txt = getTexture(itemStack);
                    AtlasMesh mesh = new AtlasMesh(txt, atlas, new BlockLayout(), CubeData.vertex, CubeData.normal, CubeData.indices);
                    uiObject = new UIObject(mesh);
                    objectCanvas.add(uiObject);
                } else {
                    ClientResourceManager resourceManager = (ClientResourceManager) Kakara.getGameInstance().getResourceManager();
                    Mesh[] mesh = resourceManager.getModel(item.getModel(), item.getTexture(), item.getMod());
                    uiObject = new UIObject(mesh);
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

    private VoxelTexture getTexture(ItemStack is) throws ExecutionException {
        return renderTextureCache.get(GameResourceManager.correctPath(
                Kakara.getGameInstance().getResourceManager().getTexture(is.getItem().getTexture(),
                        TextureResolution._16, is.getItem().getMod()).getLocalPath()));
    }

    /**
     * Get the Voxel Texture for the current item.
     *
     * @return The voxel texture.
     */
    public VoxelTexture getCurrentItem() {
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
        if (scene.getChatComponent().isFocused())
            return;
        int number;
        try {
            number = Integer.parseInt(evt.getKeyName());
        } catch (NumberFormatException ex) {
            return;
        }

        if (number < 1 || number > 5) return;

        number--;

        hotbarRectangles[selectedIndex].setColor(normal);
        selectedIndex = number;
        hotbarRectangles[number].setColor(selected);
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
