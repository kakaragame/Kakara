package org.kakara.client;

import org.kakara.client.join.JoinDetails;
import org.kakara.core.EnvType;
import org.kakara.core.command.CommandManager;
import org.kakara.core.crafting.CraftingManager;
import org.kakara.core.events.EventManager;
import org.kakara.core.game.GameSettings;
import org.kakara.core.game.Item;
import org.kakara.core.game.ItemManager;
import org.kakara.core.game.ItemStack;
import org.kakara.core.game.entity.EntityManager;
import org.kakara.core.key.KeyBindManager;
import org.kakara.core.mod.ModManager;
import org.kakara.core.player.OfflinePlayer;
import org.kakara.core.resources.ResourceManager;
import org.kakara.core.service.ServiceManager;
import org.kakara.core.sound.SoundManager;
import org.kakara.core.world.WorldGenerationManager;
import org.kakara.core.world.WorldManager;
import org.kakara.game.ServerLoadException;

import java.io.File;
import java.util.UUID;

public class ServerClient extends Client {
    public ServerClient(JoinDetails joinDetails, KakaraGame kakaraGame, GameSettings settings) {

    }

    @Override
    public GameSettings getGameSettings() {
        return null;
    }

    @Override
    public ItemStack createItemStack(Item item) {
        return null;
    }

    @Override
    public ResourceManager getResourceManager() {
        return null;
    }

    @Override
    public SoundManager getSoundManager() {
        return null;
    }

    @Override
    public ItemManager getItemManager() {
        return null;
    }

    @Override
    public EntityManager getEntityManager() {
        return null;
    }

    @Override
    public CraftingManager getCraftingManager() {
        return null;
    }

    @Override
    public ModManager getModManager() {
        return null;
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
    public ServiceManager getServiceManager() {
        return null;
    }

    @Override
    public File getWorkingDirectory() {
        return null;
    }

    @Override
    public EventManager getEventManager() {
        return null;
    }

    @Override
    public EnvType getType() {
        return null;
    }

    @Override
    public WorldGenerationManager getWorldGenerationManager() {
        return null;
    }

    @Override
    public CommandManager getCommandManager() {
        return null;
    }

    @Override
    public OfflinePlayer getOfflinePlayer(UUID uuid) {
        return null;
    }

    @Override
    public void setup()throws ServerLoadException {

    }
}
