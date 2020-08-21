package org.kakara.client.game;

import org.kakara.client.MoreUtils;
import org.kakara.client.scenes.maingamescene.MainGameScene;
import org.kakara.client.scenes.maingamescene.RenderResourceManager;
import org.kakara.core.Kakara;
import org.kakara.core.game.Block;
import org.kakara.core.game.Item;
import org.kakara.core.game.ItemStack;
import org.kakara.core.gui.Inventory;
import org.kakara.core.gui.menu.items.MenuElement;
import org.kakara.core.resources.Texture;
import org.kakara.core.resources.TextureResolution;
import org.kakara.engine.GameHandler;
import org.kakara.engine.engine.CubeData;
import org.kakara.engine.gameitems.mesh.AtlasMesh;
import org.kakara.engine.gameitems.mesh.Mesh;
import org.kakara.engine.renderobjects.RenderTexture;
import org.kakara.engine.renderobjects.renderlayouts.BlockLayout;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.ui.UserInterface;
import org.kakara.engine.ui.components.Sprite;
import org.kakara.engine.ui.components.text.Text;
import org.kakara.engine.ui.constraints.HorizontalCenterConstraint;
import org.kakara.engine.ui.constraints.VerticalCenterConstraint;
import org.kakara.engine.ui.font.Font;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.ui.items.ObjectCanvas;
import org.kakara.engine.ui.objectcanvas.UIObject;
import org.kakara.game.items.blocks.AirBlock;
import org.kakara.game.resources.GameResourceManager;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class InventoryCanvas extends ComponentCanvas {
    private ObjectCanvas objectCanvas;
    private ComponentCanvas numberCanvas;
    private final Texture inventoryBackground;
    private final List<MenuElement> elements;
    private final Inventory inventory;
    private Font font;
    private MainGameScene scene;
    private RenderResourceManager renderResourceManager;

    public InventoryCanvas(Scene scene, Texture inventoryBackground, List<MenuElement> elementList, Inventory inventory, Font font) {
        super(scene);
        if (!(scene instanceof MainGameScene)) throw new IllegalArgumentException("Must be a MainGameScene");

        this.inventoryBackground = inventoryBackground;
        this.elements = elementList;
        this.inventory = inventory;
        this.font = font;
        this.scene = (MainGameScene) scene;

        Sprite sprite = new Sprite(MoreUtils.coreTextureToEngineTexture(inventoryBackground));
        sprite.addConstraint(new HorizontalCenterConstraint());
        sprite.addConstraint(new VerticalCenterConstraint());
        add(sprite);

        renderItems();
    }

    public void renderItems() {
        if (objectCanvas != null) {
            objectCanvas.clearObjects();
            numberCanvas.clearComponents();
        }
        if (objectCanvas == null) {
            objectCanvas = new ObjectCanvas(scene);
            numberCanvas = new ComponentCanvas(scene);

        }
        //TODO handle positioning
        for (int i = 0; i < elements.size(); i++) {

            ItemStack itemStack = inventory.getItemStack(i);
            Item item = itemStack.getItem();

            if (item instanceof AirBlock) continue;
            UIObject uiObject;
            if (item instanceof Block) {
                RenderTexture txt = getTexture(itemStack);
                AtlasMesh mesh = new AtlasMesh(txt, scene.getTextureAtlas(), new BlockLayout(), CubeData.vertex, CubeData.normal, CubeData.indices);
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

            Text itemCountTxt = new Text(itemStack.getCount() + "", font);
            itemCountTxt.setPosition(400 + (55 * i), 670 + 23);
            itemCountTxt.setSize(25);
            numberCanvas.add(itemCountTxt);
        }


    }

    private RenderTexture getTexture(ItemStack is) {
        return scene.getRenderResourceManager().get(GameResourceManager.correctPath(Kakara.getResourceManager().getTexture(is.getItem().getTexture(), TextureResolution._16, is.getItem().getMod()).getLocalPath()));
    }

    @Override
    public void init(UserInterface userInterface, GameHandler handler) {
        super.init(userInterface, handler);
        objectCanvas.init(userInterface, handler);
        numberCanvas.init(userInterface, handler);
    }

    @Override
    public void render(UserInterface userInterface, GameHandler handler) {
        super.render(userInterface, handler);
        objectCanvas.render(userInterface, handler);
        numberCanvas.render(userInterface, handler);
    }

    @Override
    public void cleanup(GameHandler handler) {
        super.cleanup(handler);
        objectCanvas.cleanup(handler);
        numberCanvas.cleanup(handler);
    }
}