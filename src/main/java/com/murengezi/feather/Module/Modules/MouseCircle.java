package com.murengezi.feather.Module.Modules;

import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.feather.Event.RenderOverlayEvent;
import com.murengezi.feather.Module.Adjustable;
import com.murengezi.feather.Module.ModuleInfo;
import com.murengezi.feather.Util.RenderHelper;
import com.murengezi.minecraft.client.gui.GUI;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-12 at 13:37
 */
@ModuleInfo(name = "MouseCircle", description = "A circle that simulates your mouse movement.", version = "1.0.0", enabled = true)
public class MouseCircle extends Adjustable {

    private float deltaX;
    private float deltaY;

    @EventTarget
    public void onRender(RenderOverlayEvent event) {
        setWidth(40);
        setHeight(40);

        float circleX = MathHelper.clamp_float(getMc().mouseHelper.deltaX * 2.0f, -(getWidth() / 4), getWidth() / 4);
        float circleY = MathHelper.clamp_float(-getMc().mouseHelper.deltaY * 2.0f, -(getHeight() / 4), getHeight() / 4);
        float delta = 0.3f;
        if (this.deltaX < circleX) {
            if (this.deltaX + delta > circleX) {
                this.deltaX = circleX;
            } else {
                this.deltaX += delta;
            }
        } else if (this.deltaX > circleX) {
            if (this.deltaX - delta < circleX) {
                this.deltaX = circleX;
            } else {
                this.deltaX -= delta;
            }
        }

        if (this.deltaY < circleY) {
            if (this.deltaY + delta > circleY) {
                this.deltaY = circleY;
            } else {
                this.deltaY += delta;
            }
        } else if (this.deltaY > circleY) {
            if (this.deltaY - delta < circleY) {
                this.deltaY = circleY;
            } else {
                this.deltaY -= delta;
            }
        }

        GUI.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), Integer.MIN_VALUE);

        float baseX = getX() + getWidth() / 2;
        float baseY = getY() + getHeight() / 2;
        float radius = 4.5F;

        RenderHelper.prepare();

        GL11.glPointSize(4.0F);
        RenderHelper.start();
        RenderHelper.points();
        RenderHelper.color(255, 255, 255, 255);
        RenderHelper.drawPoint(baseX, baseY);
        RenderHelper.finish();

        RenderHelper.lineWidth(2F);
        RenderHelper.start();
        RenderHelper.circle(false);
        RenderHelper.color(255, 255, 255, 255);
        RenderHelper.drawCircle(baseX + this.deltaX, baseY + this.deltaY, radius, false);
        RenderHelper.finish();

        RenderHelper.restore();
    }
}
