package org.kakara.game;


import org.kakara.core.Kakara;
import org.kakara.core.game.*;
import org.kakara.core.player.Player;
import org.kakara.core.resources.TextureResolution;
import org.kakara.core.serializers.ods.ItemStackTag;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.GameBlock;
import org.kakara.core.world.Location;
import org.kakara.game.items.blocks.AirBlock;
import org.kakara.game.resources.GameResourceManager;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

public class GameUtils {
    public static ChunkLocation getChunkLocation(Location l) {
        return new ChunkLocation(l.getNullableWorld(), (int) (l.getX() - l.getX() % 16), (int) (l.getY() - l.getY() % 16), (int) (l.getZ() - l.getZ() % 16));
    }

    public static boolean isLocationInsideCurrentLocationRadius(ChunkLocation centerPoint, ChunkLocation location, int radius) {
        //(x-cx)^2 + (y-cy)^2 + (z-cz)^2 < r^2
        //so x-chunkX squared + x-chunkY squared + z- chunkZ squared is less than radius squared?
        return (Math.pow((centerPoint.getX() - location.getX()), 2) +
                Math.pow((centerPoint.getY() - location.getY()), 2) +
                Math.pow((centerPoint.getZ() - location.getZ()), 2)) <= Math.pow((radius * 16), 2);
    }

    public static boolean isLocationInsideCurrentLocationRadius(Location centerPoint, Location location, int radius) {
        //(x-cx)^2 + (y-cy)^2 + (z-cz)^2 < r^2
        //so x-chunkX squared + x-chunkY squared + z- chunkZ squared is less than radius squared?
        return (Math.pow((centerPoint.getX() - location.getX()), 2) +
                Math.pow((centerPoint.getY() - location.getY()), 2) +
                Math.pow((centerPoint.getZ() - location.getZ()), 2)) <= Math.pow((radius), 2);
    }

    public static boolean isLocationInsideCurrentLocationRadius(int cX, int cY, int cZ, int lX, int lY, int lZ, int radius) {
        //(x-cx)^2 + (y-cy)^2 + (z-cz)^2 < r^2
        //so x-chunkX squared + x-chunkY squared + z- chunkZ squared is less than radius squared?
        return (Math.pow((cX - lX), 2)) +
                Math.pow((cY - lY), 2) +
                Math.pow((cZ - lZ), 2) <= Math.pow((radius * 16), 2);
    }

    public static ChunkLocation getChunkFileLocation(ChunkLocation location) {
        int x = (int) Math.floor(location.getX() / 64D);
        int y = (int) Math.floor(location.getY() / 64D);
        int z = (int) Math.floor(location.getZ() / 64D);
        return new ChunkLocation(x, y, z);
    }

    public static boolean isLocationOnPerimeter(ChunkLocation centerPoint, ChunkLocation location, int radius) {
        return (Math.pow((centerPoint.getX() - location.getX()), 2) +
                Math.pow((centerPoint.getY() - location.getY()), 2) +
                Math.pow((centerPoint.getZ() - location.getZ()), 2)) == Math.pow((radius * 16), 2);
    }

    public static double getBreakingTime(GameBlock block, ItemStack itemStack, Player breaker) {
        if (breaker.getGameMode().getProperties().contains(GameModeProperties.INSTANT_BREAKING)) {
            return 50d;
        }
        float blockHardness = ((Block) block.getItemStack().getItem()).getHardness();
        float toolHardness = toolHardness(itemStack.getItem());
        if (blockHardness == toolHardness) {
            return 5d;
        } else if (blockHardness < toolHardness) {
            return 0d;
        } else {
            //TODO IDK
            return 5d;
        }
    }

    public static float toolHardness(Item item) {
        if (item instanceof Tool) {
            return ((Tool) item).getHardness();
        }
        return 1;
    }

    public static ItemStack getReadyForPlacement(ItemStack itemStack) {
        itemStack.setCount(itemStack.getCount() - 1);
        ItemStack placeStack = itemStack.clone();
        placeStack.setCount(1);
        return placeStack;
    }

    public static String getGameMode(GameMode gameMode) {
        return gameMode.getClass().getName() + "#" + gameMode.getName();
    }

    public static GameMode getGameMode(String mode) {
        String[] split = mode.split("#");
        try {
            Class<?> clazz = Class.forName(split[0]);
            if (clazz.isEnum()) {
                return (GameMode) Enum.valueOf((Class<? extends Enum>) clazz, split[1]);
            } else {
                return (GameMode) clazz.getConstructor().newInstance();
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}

