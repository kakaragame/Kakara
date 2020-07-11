package org.kakara.client.scenes.maingamescene;

import org.kakara.engine.item.GameItem;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class SceneUtils {
    private MainGameScene gameScene;

    public SceneUtils(MainGameScene gameScene) {
        this.gameScene = gameScene;
    }

    protected Optional<GameItem> getItemByID(UUID uuid) {
        AtomicReference<GameItem> gameItemA = new AtomicReference<>();
        gameScene.getItemHandler().getNonInstancedMeshMap().forEach((mesh, gameItems) -> gameItems.stream().filter(gameItem -> gameItem.getId().equals(uuid)).findFirst().ifPresent(gameItemA::set));
        return Optional.ofNullable(gameItemA.get());
    }
}
