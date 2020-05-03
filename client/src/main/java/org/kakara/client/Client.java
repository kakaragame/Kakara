package org.kakara.client;

import org.kakara.client.game.commands.ClientCommandManager;
import org.kakara.core.GameInstance;
import org.kakara.core.GameTypes;
import org.kakara.core.command.CommandManager;
import org.kakara.core.crafting.CraftingManager;
import org.kakara.core.data.SerializerManager;
import org.kakara.core.events.EventManager;
import org.kakara.core.game.*;
import org.kakara.core.game.entity.EntityManager;
import org.kakara.core.key.KeyBindManager;
import org.kakara.core.mod.ModManager;
import org.kakara.core.mod.game.GameModManager;
import org.kakara.core.player.OfflinePlayer;
import org.kakara.core.resources.ResourceManager;
import org.kakara.core.serializers.messagepack.MPSerializerRegistrar;
import org.kakara.core.sound.SoundManager;
import org.kakara.core.world.WorldGenerationManager;
import org.kakara.core.world.WorldManager;
import org.kakara.engine.utils.Utils;
import org.kakara.game.item.GameItemManager;
import org.kakara.game.items.GameItemStack;
import org.kakara.game.mod.KakaraMod;
import org.kakara.game.resources.GameResourceManager;
import org.kakara.game.world.GameWorldGenerationManager;

import java.io.File;
import java.util.UUID;

public class Client implements GameInstance {
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
    private SerializerManager serializerManager;
    private CommandManager commandManager;

    public Client(KakaraGame kakaraGame, GameSettings gameSettings) {
        this.settings = gameSettings;
        this.kakaraGame = kakaraGame;
        workingDirectory = Utils.getCurrentDirectory();
        worldGenerationManager = new GameWorldGenerationManager();
        itemManager = new GameItemManager();
        modManager = new GameModManager(new KakaraMod());
        resourceManager = new GameResourceManager();
        eventManager = new GameEventManager();
        serializerManager = new SerializerManager();
        commandManager = new ClientCommandManager();
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
        return null;
    }

    @Override
    public KeyBindManager getKeyBindManager() {
        return null;
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
    public SerializerManager getSerializerManager() {
        return serializerManager;
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
    public GameTypes getType() {
        return GameTypes.CLIENT;
    }


}
