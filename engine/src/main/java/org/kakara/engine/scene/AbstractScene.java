package org.kakara.engine.scene;

import org.kakara.engine.Camera;
import org.kakara.engine.GameEngine;
import org.kakara.engine.GameHandler;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.item.ItemHandler;
import org.kakara.engine.item.Particles.ParticleEmitter;
import org.kakara.engine.item.Particles.ParticleHandler;
import org.kakara.engine.item.SkyBox;
import org.kakara.engine.lighting.LightHandler;
import org.kakara.engine.lighting.PointLight;
import org.kakara.engine.lighting.SpotLight;
import org.kakara.engine.ui.HUD;
import org.kakara.engine.ui.HUDItem;
import org.kakara.engine.weather.Fog;

/**
 * Primary Scene to derive from.
 */
public abstract class AbstractScene implements Scene {
    private ItemHandler itemHandler = new ItemHandler();
    private LightHandler lightHandler = new LightHandler();
    private ParticleHandler particleHandler = new ParticleHandler();
    private SkyBox skyBox;

    protected HUD hud = new HUD(this);
    private boolean mouseStatus;
    protected GameHandler gameHandler;

    private Fog fog;

    protected AbstractScene(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
        fog = Fog.NOFOG;
        try{
            hud.init(gameHandler.getWindow());
        }catch(Exception ex){
            GameEngine.LOGGER.error("Unable to load HUD", ex);
        }
    }

    @Override
    public void setCurserStatus(boolean status) {
        mouseStatus = status;
        gameHandler.getWindow().setCursorVisibility(status);
    }

    @Override
    public boolean getCurserStatus() {
        return mouseStatus;
    }

    @Override
    public ItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public LightHandler getLightHandler(){
        return lightHandler;
    }

    @Override
    public HUD getHUD(){
        return hud;
    }

    @Override
    public ParticleHandler getParticleHandler(){
        return particleHandler;
    }

    @Override
    public Camera getCamera(){
        return gameHandler.getCamera();
    }

    public void add(GameItem gameItem){
        itemHandler.addItem(gameItem);
    }

    public void add(PointLight pointLight){
        lightHandler.addPointLight(pointLight);
    }

    public void add(SpotLight spotLight){
        lightHandler.addSpotLight(spotLight);
    }

    public void add(HUDItem hudItem){
        hud.addItem(hudItem);
    }

    public void add(ParticleEmitter emitter){
        particleHandler.addParticleEmitter(emitter);
    }

    public SkyBox getSkyBox(){
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox){
        this.skyBox = skyBox;
    }

    public Fog getFog(){
        return fog;
    }

    public void setFog(Fog fog){
        this.fog = fog;
    }


}
