package org.kakara.client;

import org.kakara.client.game.ClientResourceManager;
import org.kakara.client.game.IntegratedServer;
import org.kakara.client.game.commands.ClientCommandManager;
import org.kakara.client.join.LocalJoin;
import org.kakara.core.EnvType;
import org.kakara.core.command.CommandManager;
import org.kakara.core.crafting.CraftingManager;
import org.kakara.core.events.EventManager;
import org.kakara.core.game.*;
import org.kakara.core.game.entity.EntityManager;
import org.kakara.core.key.KeyBindManager;
import org.kakara.core.mod.ModManager;
import org.kakara.core.mod.game.GameModManager;
import org.kakara.core.player.OfflinePlayer;
import org.kakara.core.resources.ResourceManager;
import org.kakara.core.service.ServiceManager;
import org.kakara.core.sound.SoundManager;
import org.kakara.core.world.WorldGenerationManager;
import org.kakara.core.world.WorldManager;
import org.kakara.engine.utils.Utils;
import org.kakara.game.GameServiceManager;
import org.kakara.game.ServerLoadException;
import org.kakara.game.item.GameItemManager;
import org.kakara.game.items.GameItemStack;
import org.kakara.game.mod.KakaraMod;
import org.kakara.game.world.GameWorldGenerationManager;

import java.io.File;
import java.util.UUID;

public class LocalClient extends Client {
    private GameSettings settings;
    private KakaraGame kakaraGame;
    private EntityManager entityManager;
    private ItemManager itemManager;
    private CraftingManager craftingManager;
    private ResourceManager resourceManager;
    private SoundManager soundManager;
    private ModManager modManager;
    private File workingDirectory;
    private WorldGenerationManager worldGenerationManager;
    private EventManager eventManager;
    private CommandManager commandManager;
    private WorldManager worldManager;
    private ServiceManager serviceManager;
    private final LocalJoin localJoin;

    public LocalClient(LocalJoin joinDetails, KakaraGame kakaraGame, GameSettings gameSettings){
        this.settings = gameSettings;
        this.kakaraGame = kakaraGame;
        workingDirectory = Utils.getCurrentDirectory();
        worldGenerationManager = new GameWorldGenerationManager();
        itemManager = new GameItemManager();
        modManager = new GameModManager(new KakaraMod(this));
        resourceManager = new ClientResourceManager();
        eventManager = new GameEventManager();
        commandManager = new ClientCommandManager();
        serviceManager = new GameServiceManager();
        this.localJoin = joinDetails;
    }

    public void setup() throws ServerLoadException {
        if (settings.isTestMode()) {
            server = new IntegratedServer(localJoin.getSave(), localJoin.getSelf(), null);
            ((IntegratedServer) server).start();
        } else {
            //TODO implement world joining
        }
    }

    @Override
    public GameSettings getGameSettings() {
        return settings;
    }

    @Override
    public ItemStack createItemStack(Item item) {
        return new GameItemStack(1, item);
    }

    @Override
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    @Override
    public SoundManager getSoundManager() {
        return soundManager;
    }

    @Override
    public ItemManager getItemManager() {
        return itemManager;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public CraftingManager getCraftingManager() {
        return craftingManager;
    }

    @Override
    public WorldManager getWorldManager() {
        return worldManager;
    }

    public void setWorldManager(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @Override
    public KeyBindManager getKeyBindManager() {
        return null;
    }

    @Override
    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    @Override
    public ModManager getModManager() {
        return modManager;
    }

    @Override
    public File getWorkingDirectory() {
        return workingDirectory;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public WorldGenerationManager getWorldGenerationManager() {
        return worldGenerationManager;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public OfflinePlayer getOfflinePlayer(UUID uuid) {
        return null;
    }

    @Override
    public EnvType getType() {
        return EnvType.CLIENT;
    }


}
