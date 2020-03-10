package org.kakara.engine.item.Particles;

import org.kakara.engine.item.GameItem;

import java.util.List;

public interface ParticleEmitter {
    void cleanup();
    Particle getBaseParticle();
    List<GameItem> getParticles();
}
