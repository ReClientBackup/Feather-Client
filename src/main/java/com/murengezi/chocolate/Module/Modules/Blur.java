package com.murengezi.chocolate.Module.Modules;

import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.chocolate.Event.ModuleDisableEvent;
import com.murengezi.chocolate.Event.ModuleEnableEvent;
import com.murengezi.chocolate.Event.TickEvent;
import com.murengezi.chocolate.Module.Module;
import com.murengezi.chocolate.Module.ModuleInfo;
import com.murengezi.chocolate.Util.TimerUtil;
import com.murengezi.minecraft.client.gui.Chat.ChatScreen;
import com.murengezi.minecraft.client.renderer.EntityRenderer;
import com.murengezi.minecraft.client.shader.Shader;
import com.murengezi.minecraft.client.shader.ShaderGroup;
import com.murengezi.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-13 at 23:11
 */

@ModuleInfo(name = "Blur", description = "Blurs the background when gui is open.", version = "1.0.0", enabled = true)
public class Blur extends Module {

    private TimerUtil timer;
    private boolean shaderActive;

    @Override
    protected void Init() {
        timer = new TimerUtil();
        super.Init();
    }


    @EventTarget
    public void onEnable(ModuleEnableEvent event) {
        if (event.getModule() == this) {
            shaderActive = getMc().entityRenderer.isShaderActive();
        }
    }

    @EventTarget
    public void onDisable(ModuleDisableEvent event) {
        if (event.getModule() == this) {
            EntityRenderer entityRenderer = getMc().entityRenderer;
            if (entityRenderer.isShaderActive()) {
                entityRenderer.stopUserShader();
            }
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (getWorld() != null) {
            EntityRenderer entityRenderer = getMc().entityRenderer;
            if (getMc().currentScreen != null && !(getMc().currentScreen instanceof ChatScreen)) {
                if (!shaderActive) {
                    entityRenderer.loadShader(new ResourceLocation("shaders/post/fade_in_blur.json"));
                    timer.reset();
                    shaderActive = true;
                }
            } else {
                if (shaderActive) {
                    timer.reset();
                    shaderActive = false;
                } else {
                    if (getProgressReversed() <= 0.0f) {
                        entityRenderer.stopUserShader();
                    }
                }
            }
        }

        if (getMc().entityRenderer.isShaderActive()) {
            ShaderGroup shaderGroup = getMc().entityRenderer.getShaderGroup();
            List<Shader> shaders = shaderGroup.listShaders();
            shaders.forEach(shader -> {
                ShaderUniform shaderUniform = shader.getShaderManager().getShaderUniform("Progress");
                if (shaderUniform != null) {
                    if (getMc().currentScreen != null) {
                        if (shaderActive) {
                            if (getProgress() < 2.0f) {
                                shaderUniform.set(getProgress());
                            }
                        }
                    } else {
                        if (!shaderActive) {
                            if (getProgressReversed() > 0.0f) {
                                shaderUniform.set(getProgressReversed());
                            }
                        }
                    }
                }
            });
        }
    }

    private float getProgress() {
        return Math.min((float)(System.currentTimeMillis() - timer.getLastMS()) / 100, 1f);
    }


    private float getProgressReversed() {
        return Math.max(1f - ((float)(System.currentTimeMillis() - timer.getLastMS()) / 100), 0);
    }

}
