package me.superblaubeere27.particle;

import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ParticleSystem {
    private static final float SPEED = 0.0025f;
    private static final float MIN_DISTANCE = 50.0F;

    private List<Particle> particleList = new ArrayList<>();

    public ParticleSystem(int initAmount) {
        addParticles(initAmount);
    }

    public void addParticles(int amount) {
        for (int i = 0; i < amount; i++) {
            particleList.add(Particle.generateParticle());
        }
    }

    public void tick(int delta) {
        for (Particle particle : particleList) {
            particle.tick(delta, SPEED);
        }
    }

    public void render(int mouseX, int mouseY) {
        for (Particle particle : particleList) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, particle.getAlpha() / 255.0f);
            GL11.glPointSize(particle.getSize());
            GL11.glBegin(GL11.GL_POINTS);

            GL11.glVertex2f(particle.getX(), particle.getY());
            GL11.glEnd();

            for (Particle particle1 : this.particleList) {
                if (particle1 != particle) {
                    float distance = particle.getDistanceTo(particle1);
                    if (distance < MIN_DISTANCE && particle.getDistanceTo(mouseX, mouseY) < MIN_DISTANCE || particle1.getDistanceTo(mouseX, mouseY) < MIN_DISTANCE) {
                        float alpha = Math.min(1.0F, Math.min(1.0F, 1.0F - distance / 150.0F));
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
                        GL11.glLineWidth(0.5F);
                        GL11.glBegin(1);
                        GL11.glVertex2f(particle.getX(), particle.getY());
                        GL11.glVertex2f(particle1.getX(), particle1.getY());
                        GL11.glEnd();
                    }
                }
            }
        }
    }
}
