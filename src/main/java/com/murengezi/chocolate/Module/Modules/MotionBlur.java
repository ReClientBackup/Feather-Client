package com.murengezi.chocolate.Module.Modules;

import com.darkmagician6.eventapi.EventTarget;
import com.murengezi.chocolate.Event.ModuleDisableEvent;
import com.murengezi.chocolate.Event.ModuleEnableEvent;
import com.murengezi.chocolate.Event.TickEvent;
import com.murengezi.chocolate.Module.Module;
import com.murengezi.chocolate.Module.ModuleInfo;
import com.murengezi.chocolate.Util.TimerUtil;
import com.murengezi.minecraft.client.gui.Chat.ChatScreen;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-13 at 23:11
 */

@ModuleInfo(name = "Motion Blur", description = "", version = "1.0.0", enabled = true)
public class MotionBlur extends Module {


    @EventTarget
    public void onTick(TickEvent event) {

        if (getMc().entityRenderer.isShaderActive()) {
            ShaderGroup shaderGroup = getMc().entityRenderer.getShaderGroup();
            List<Shader> shaders = shaderGroup.listShaders();
            /*shaders.forEach(shader -> {
                ShaderUniform shaderUniform = shader.getShaderManager().getShaderUniform("InSize");
                shaderUniform.set(4);
            });*/
        }

        EntityRenderer entityRenderer = getMc().entityRenderer;
        if (!entityRenderer.isShaderActive()) {
            getMc().entityRenderer.loadShader(new ResourceLocation("shaders/post/motionblur.json"));
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
}
