package org.kakara.client.local.game;

import org.kakara.client.MoreUtils;
import org.kakara.client.scenes.maingamescene.MainGameScene;
import org.kakara.client.scenes.maingamescene.RenderResourceManager;
import org.kakara.core.common.Kakara;
import org.kakara.core.common.game.Block;
import org.kakara.core.common.game.Item;
import org.kakara.core.common.game.ItemStack;
import org.kakara.core.common.gui.Inventory;
import org.kakara.core.common.gui.InventoryProperties;
import org.kakara.core.common.gui.menu.items.ItemStackElement;
import org.kakara.core.common.gui.menu.items.MenuElement;
import org.kakara.core.common.resources.Texture;
import org.kakara.core.common.resources.TextureResolution;
import org.kakara.engine.GameHandler;
import org.kakara.engine.engine.CubeData;
import org.kakara.engine.gameitems.mesh.AtlasMesh;
import org.kakara.engine.gameitems.mesh.Mesh;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.renderobjects.RenderTexture;
import org.kakara.engine.renderobjects.renderlayouts.BlockLayout;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.ui.UserInterface;
import org.kakara.engine.ui.components.Panel;
import org.kakara.engine.ui.components.Sprite;
import org.kakara.engine.ui.constraints.HorizontalCenterConstraint;
import org.kakara.engine.ui.constraints.VerticalCenterConstraint;
import org.kakara.engine.ui.events.UIClickEvent;
import org.kakara.engine.ui.events.UIReleaseEvent;
import org.kakara.engine.ui.font.Font;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.ui.items.ObjectCanvas;
import org.kakara.engine.ui.objectcanvas.UIObject;
import org.kakara.game.items.blocks.AirBlock;
import org.kakara.game.resources.GameResourceManager;

import java.util.Set;

public class InventoryCanvas extends ComponentCanvas {
    private final Texture inventoryBackground;
    private final Set<MenuElement> elements;
    private final Inventory inventory;
    private ObjectCanvas objectCanvas;
    private ComponentCanvas numberCanvas;
    private final Font font;
    private final MainGameScene scene;
    private RenderResourceManager renderResourceManager;
    private final Panel panel;
    private boolean hadFirstRun = false;

    private HoldingItem holdingItem = null;
    private final Vector2 textureSize;
    private final int scale;

    public InventoryCanvas(Scene scene, Texture inventoryBackground, Set<MenuElement> elementList, Inventory inventory, Font font) {
        super(scene);
        if (!(scene instanceof MainGameScene)) throw new IllegalArgumentException("Must be a MainGameScene");

        this.inventoryBackground = inventoryBackground;
        this.elements = elementList;
        this.inventory = inventory;
        this.font = font;
        this.scene = (MainGameScene) scene;
        panel = new Panel();
        panel.addConstraint(new HorizontalCenterConstraint());
        panel.addConstraint(new VerticalCenterConstraint());
        panel.setScale(GameHandler.getInstance().getWindow().getWidth(), GameHandler.getInstance().getWindow().getHeight());
        org.kakara.engine.gameitems.Texture texture = MoreUtils.coreTextureToEngineTexture(inventoryBackground);
        Sprite sprite = new Sprite(texture);
        sprite.addConstraint(new HorizontalCenterConstraint());
        sprite.addConstraint(new VerticalCenterConstraint());
        panel.add(sprite);

        InventoryProperties inventoryProperties = inventory.getRenderer().getProperties();
        this.textureSize = new Vector2(texture.getWidth(), texture.getHeight());
        this.scale = inventoryProperties.getScale();
        sprite.setScale(texture.getWidth() * inventoryProperties.getScale(), texture.getHeight() * inventoryProperties.getScale());

        panel.setVisible(true);
        sprite.setVisible(true);
        add(panel);

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
        for (MenuElement element : elements) {
            if (element instanceof ItemStackElement) {
                ItemStackElement stackElement = (ItemStackElement) element;
                ItemStack itemStack = inventory.getContainer().getItemStack(stackElement.getSlot());
                Item item = itemStack.getItem();

                Vector2 vector2 = panel.getPosition().add(panel.getChildren().get(0).getPosition());
                Vector2 elementPosition = new Vector2(element.getPosition().x, element.getPosition().y);
                // TODO make a better way to do this in the engine.
                elementPosition.x /= textureSize.x;
                elementPosition.y /= textureSize.y;
                elementPosition.x *= textureSize.x * scale;
                elementPosition.y *= textureSize.y * scale;
                Vector2 uiObjectPosition = vector2.add(elementPosition).add(7 * scale, -5 * scale);
                //Make Small Box for item holder
                if (item instanceof AirBlock) continue;
                UIObject uiObject;
                if (item instanceof Block) {
                    RenderTexture txt = getTexture(itemStack);
                    AtlasMesh mesh = new AtlasMesh(txt, scene.getTextureAtlas(), new BlockLayout(), CubeData.vertex, CubeData.normal, CubeData.indices);
                    uiObject = new UIObject(mesh);
                    objectCanvas.add(uiObject);
                } else {
                    ClientResourceManager resourceManager = (ClientResourceManager) Kakara.getGameInstance().getResourceManager();
                    Mesh[] mesh = resourceManager.getModel(item.getModel(), item.getTexture(), item.getMod());
                    //TODO @Ryandw11 - We need to be able the pass all the meshes
                    uiObject = new UIObject(mesh[0]);

                }
                uiObject.addUActionEvent(UIClickEvent.class, (UIClickEvent) (location, mouseClickType) -> {
                    if (scene.getServer() instanceof IntegratedServer) {
                        //ItemGrabInventoryEvent itemGrabInventoryEvent = new ItemGrabInventoryEvent(scene.getServer().getPlayerEntity(), inventory, itemStack);
                        //EventUtils.executeIfNotNull(stackElement.getItemGrabEvent(), itemGrabInventoryEvent);
                        //if (itemGrabInventoryEvent.isCancelled()) return;
                        //TODO execute event in EventManager
                        //if (itemGrabInventoryEvent.isCancelled()) return; CheckAgain
                        holdingItem = new HoldingItem(itemStack, ((ItemStackElement) element).getSlot());

                    }
                    //TODO implement server
                });
                uiObject.addUActionEvent(UIReleaseEvent.class, (UIReleaseEvent) (pos, mouseClick) -> {
                    //TODO find nearest location and drop it.

                    holdingItem = null;
                });

                uiObject.setPosition(uiObjectPosition);

                uiObject.setScale(9 * scale);

                uiObject.getRotation().rotateX((float) Math.toRadians(50));
                uiObject.getRotation().rotateY((float) Math.toRadians(40));
            }
        }


    }

    private RenderTexture getTexture(ItemStack is) {
        return scene.getRenderResourceManager().get(GameResourceManager.correctPath(Kakara.getGameInstance().getResourceManager().getTexture(is.getItem().getTexture(), TextureResolution._16, is.getItem().getMod()).getLocalPath()));
    }

    @Override
    public void init(UserInterface userInterface, GameHandler handler) {
        super.init(userInterface, handler);
        if (hadFirstRun) {
            objectCanvas.init(userInterface, handler);
            numberCanvas.init(userInterface, handler);
        }

    }

    @Override
    public void render(UserInterface userInterface, GameHandler handler) {
        super.render(userInterface, handler);
        if (hadFirstRun) {

            objectCanvas.render(userInterface, handler);
            numberCanvas.render(userInterface, handler);
        }
        if (!hadFirstRun) {
            renderItems();
            hadFirstRun = true;
        }
    }

    @Override
    public void cleanup(GameHandler handler) {
        super.cleanup(handler);
        objectCanvas.cleanup(handler);
        numberCanvas.cleanup(handler);
    }


}
