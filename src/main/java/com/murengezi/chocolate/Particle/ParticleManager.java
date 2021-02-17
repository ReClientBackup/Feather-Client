package com.murengezi.chocolate.Particle;

import com.murengezi.chocolate.Util.MinecraftUtils;
import com.murengezi.chocolate.Util.RenderHelper;
import me.superblaubeere27.particle.ParticleSystem;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-14 at 10:57
 */
public class ParticleManager extends MinecraftUtils {

    private final ParticleSystem particleSystem;

    public ParticleManager() {
        particleSystem = new ParticleSystem(300);
    }

    public void render(int mouseX, int mouseY) {
        RenderHelper.prepare();
        getParticleSystem().render(mouseX, mouseY);
        RenderHelper.restore();
    }

    public void tick(int delta) {
        getParticleSystem().tick(delta);
    }

    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }
}
