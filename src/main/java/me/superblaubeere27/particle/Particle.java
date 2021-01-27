package me.superblaubeere27.particle;

import com.murengezi.feather.Util.MinecraftUtils;
import me.superblaubeere27.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import java.util.Random;

public class Particle extends MinecraftUtils {

    private static final Random random = new Random();
    private Vector2f velocity, pos;
    private float size, alpha;
    private int width, height;


    public Particle(Vector2f velocity, float x, float y, float size) {
        this.velocity = velocity;
        this.pos = new Vector2f(x, y);
        this.size = size;
        this.alpha = 120;
    }

    public static Particle generateParticle() {
        Vector2f velocity = new Vector2f((float) (Math.random() * 2.0f - 1.0f), (float) (Math.random() * 2.0f - 1.0f));
        float x = random.nextInt(1920);
        float y = random.nextInt(1080);
        float size = (float) (Math.random() * 4.0f) + 1.0f;
        return new Particle(velocity, x, y, size);
    }

    public float getAlpha() {
        return this.alpha;
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity = velocity;
    }

    public float getX() {
        return pos.getX();
    }

    public void setX(float x) {
        this.pos.setX(x);
    }

    public float getY() {
        return pos.getY();
    }

    public void setY(float y) {
        this.pos.setY(y);
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void tick(int delta, float speed) {
        pos.x += velocity.getX() * delta * speed;
        pos.y += velocity.getY() * delta * speed;

        if (pos.getX() > 1920) pos.setX(0);
        if (pos.getX() < 0) pos.setX(1920);

        if (pos.getY() > 1080) pos.setY(0);
        if (pos.getY() < 0) pos.setY(1080);
    }

    public float getDistanceTo(Particle particle) {
        return getDistanceTo(particle.getX(), particle.getY());
    }

    public float getDistanceTo(float x, float y) {
        return (float) MathUtil.distance(getX(), getY(), x, y);
    }
}
