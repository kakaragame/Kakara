package org.kakara.engine.item.Particles;

import java.util.ArrayList;
import java.util.List;

public class ParticleHandler {

    private List<ParticleEmitter> particleEmitters;
    public ParticleHandler(){
        particleEmitters = new ArrayList<>();
    }

    public void addParticleEmitter(ParticleEmitter emitter){
        particleEmitters.add(emitter);
    }

    public List<ParticleEmitter> getParticleEmitters(){
        return particleEmitters;
    }

    public void clear(){
        particleEmitters.clear();
    }
}
