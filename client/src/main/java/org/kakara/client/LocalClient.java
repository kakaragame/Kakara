package org.kakara.client;

import org.kakara.client.game.ClientResourceManager;
import org.kakara.client.game.IntegratedServer;
import org.kakara.client.game.commands.ClientCommandManager;
import org.kakara.client.join.LocalJoin;

import org.kakara.core.common.EnvType;
import org.kakara.core.common.Serverable;
import org.kakara.core.common.command.CommandManager;
import org.kakara.core.common.events.EventManager;
import org.kakara.core.common.events.game.GameEventManager;
import org.kakara.core.common.exceptions.NoServerVersionAvailableException;
import org.kakara.core.common.game.GameSettings;
import org.kakara.core.common.game.Item;
import org.kakara.core.common.game.ItemManager;
import org.kakara.core.common.game.ItemStack;
import org.kakara.core.common.mod.ModManager;
import org.kakara.core.common.mod.game.GameModManager;
import org.kakara.core.common.resources.ResourceManager;
import org.kakara.core.common.service.ServiceManager;
import org.kakara.core.common.world.WorldGenerationManager;
import org.kakara.core.server.ServerGameInstance;
import org.kakara.core.server.game.ServerItemStack;
import org.kakara.core.server.world.WorldManager;
import org.kakara.engine.utils.Utils;
import org.kakara.game.GameServiceManager;
import org.kakara.game.ServerLoadException;
import org.kakara.game.item.GameItemManager;
import org.kakara.game.items.GameItemStack;
import org.kakara.game.mod.KakaraMod;
import org.kakara.game.world.GameWorldGenerationManager;

import java.io.File;
import java.util.UUID;

public class LocalClient extends Client implements ServerGameInstance {
    private GameSettings settings;
    private KakaraGame kakaraGame;
    private ItemManager itemManager;
    private ResourceManager resourceManager;
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
        itemManager = new GameItemManager();
        modManager = new GameModManager(new KakaraMod(this));
        resourceManager = new ClientResourceManager();
        eventManager = new GameEventManager();
        commandManager = new ClientCommandManager();
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
    public ServerItemStack createItemStack(Item item) {
        return new GameItemStack(1, item);
    }

    @Override
    public ResourceManager getResourceManager() {
        return resourceManager;
    }


    @Override
    public ItemManager getItemManager() {
        return itemManager;
    }


    @Override
    public WorldManager getWorldManager() {
        return worldManager;
    }

    public void setWorldManager(WorldManager worldManager) {
        this.worldManager = worldManager;
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
    public EnvType getType() {
        return EnvType.CLIENT;
    }


    @Override
    public boolean isServerVersionAvailable() {
        return false;
    }

    @Override
    public <T extends Serverable> T getServerVersion() {
        return null;
    }

    @Override
    public void requiresServerVersion() throws NoServerVersionAvailableException {

    }
}
