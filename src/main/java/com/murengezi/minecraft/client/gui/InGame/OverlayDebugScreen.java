package com.murengezi.minecraft.client.gui.InGame;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map.Entry;

import com.murengezi.minecraft.client.gui.GUI;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.block.properties.IProperty;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.client.ClientBrandRetriever;
import com.murengezi.minecraft.client.renderer.GlStateManager;
import com.murengezi.minecraft.client.renderer.OpenGlHelper;
import com.murengezi.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.optifine.config.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.optifine.SmartAnimations;
import net.optifine.TextureAnimations;
import net.optifine.util.NativeMemory;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class OverlayDebugScreen extends GUI {

    private String debugOF = null;

    public void renderDebugInfo(ScaledResolution resolution) {
        getMc().mcProfiler.startSection("debug");
        GlStateManager.pushMatrix();
        this.renderDebugInfoLeft();
        this.renderDebugInfoRight(resolution);
        GlStateManager.popMatrix();

        getMc().mcProfiler.endSection();
    }

    private boolean isReducedDebug() {
        return getPlayer().hasReducedDebug() || getMc().gameSettings.reducedDebugInfo;
    }

    protected void renderDebugInfoLeft() {
        List<String> list = this.call();

        for (int i = 0; i < list.size(); ++i) {
            String s = list.get(i);

            if (!Strings.isNullOrEmpty(s)) {
                int fontHeight = getFr().FONT_HEIGHT;
                int stringWidth = getFr().getStringWidth(s);
                int i1 = 2 + fontHeight * i;
                drawRect(1, i1 - 1, 2 + stringWidth + 1, i1 + fontHeight - 1, -1873784752);
                getFr().drawString(s, 2, i1, 14737632);
            }
        }
    }

    protected void renderDebugInfoRight(ScaledResolution scaledResolution) {
        List<String> list = this.getDebugInfoRight();

        for (int i = 0; i < list.size(); ++i) {
            String s = list.get(i);

            if (!Strings.isNullOrEmpty(s)) {
                int j = getFr().FONT_HEIGHT;
                int k = getFr().getStringWidth(s);
                int l = scaledResolution.getScaledWidth() - 2 - k;
                int i1 = 2 + j * i;
                drawRect(l - 1, i1 - 1, l + k + 1, i1 + j - 1, -1873784752);
                getFr().drawString(s, l, i1, 14737632);
            }
        }
    }

    @SuppressWarnings("incomplete-switch")
    protected List<String> call() {
        BlockPos pos = new BlockPos(getMc().getRenderViewEntity().posX, getMc().getRenderViewEntity().getEntityBoundingBox().minY, getMc().getRenderViewEntity().posZ);

        if (!getMc().debug.equals(this.debugOF)) {
            StringBuilder stringBuilder = new StringBuilder(getMc().debug);
            int i = Config.getFpsMin();
            int j = getMc().debug.indexOf(" fps ");

            if (j >= 0) {
                stringBuilder.insert(j, "/" + i);
            }

            if (Config.isSmoothFps()) {
                stringBuilder.append(" sf");
            }

            if (Config.isFastRender()) {
                stringBuilder.append(" fr");
            }

            if (Config.isAnisotropicFiltering()) {
                stringBuilder.append(" af");
            }

            if (Config.isAntialiasing()) {
                stringBuilder.append(" aa");
            }

            if (Config.isRenderRegions()) {
                stringBuilder.append(" reg");
            }

            if (Config.isShaders()) {
                stringBuilder.append(" sh");
            }

            getMc().debug = stringBuilder.toString();
            this.debugOF = getMc().debug;
        }

        StringBuilder stringbuilder = new StringBuilder();
        TextureMap texturemap = Config.getTextureMap();
        stringbuilder.append(", A: ");

        if (SmartAnimations.isActive())
        {
            stringbuilder.append(texturemap.getCountAnimationsActive() + TextureAnimations.getCountAnimationsActive());
            stringbuilder.append("/");
        }

        stringbuilder.append(texturemap.getCountAnimations() + TextureAnimations.getCountAnimations());
        String s1 = stringbuilder.toString();

        if (this.isReducedDebug()) {
            return Lists.newArrayList("Minecraft 1.8.10 (" + getMc().getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", getMc().debug, getMc().renderGlobal.getDebugInfoRenders(), getMc().renderGlobal.getDebugInfoEntities(), "P: " + getMc().effectRenderer.getStatistics() + ". T: " + getWorld().getDebugLoadedEntities() + s1, getWorld().getProviderName(), "", String.format("Chunk-relative: %d %d %d", pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15));
        } else {
            Entity entity = getMc().getRenderViewEntity();
            EnumFacing enumfacing = entity.getHorizontalFacing();
            String s = "Invalid";

            switch (enumfacing) {
                case NORTH:
                    s = "Towards negative Z";
                    break;
                case SOUTH:
                    s = "Towards positive Z";
                    break;
                case WEST:
                    s = "Towards negative X";
                    break;
                case EAST:
                    s = "Towards positive X";
            }

            List<String> list = Lists.newArrayList("Minecraft 1.8.10 (" + getMc().getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", getMc().debug, getMc().renderGlobal.getDebugInfoRenders(), getMc().renderGlobal.getDebugInfoEntities(), "P: " + getMc().effectRenderer.getStatistics() + ". T: " + getWorld().getDebugLoadedEntities() + s1, getWorld().getProviderName(), "", String.format("XYZ: %.3f / %.5f / %.3f", getMc().getRenderViewEntity().posX, getMc().getRenderViewEntity().getEntityBoundingBox().minY, getMc().getRenderViewEntity().posZ), String.format("Block: %d %d %d", pos.getX(), pos.getY(), pos.getZ()), String.format("Chunk: %d %d %d in %d %d %d", pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4), String.format("Facing: %s (%s) (%.1f / %.1f)", enumfacing, s, MathHelper.wrapAngleTo180_float(entity.rotationYaw), MathHelper.wrapAngleTo180_float(entity.rotationPitch)));

            if (getWorld() != null && getWorld().isBlockLoaded(pos)) {
                Chunk chunk = getWorld().getChunkFromBlockCoords(pos);
                list.add("Biome: " + chunk.getBiome(pos, getWorld().getWorldChunkManager()).biomeName);
                list.add("Light: " + chunk.getLightSubtracted(pos, 0) + " (" + chunk.getLightFor(EnumSkyBlock.SKY, pos) + " sky, " + chunk.getLightFor(EnumSkyBlock.BLOCK, pos) + " block)");
                DifficultyInstance difficultyForLocation = getWorld().getDifficultyForLocation(pos);

                if (getMc().isIntegratedServerRunning() && getMc().getIntegratedServer() != null) {
                    EntityPlayerMP playerMP = getMc().getIntegratedServer().getConfigurationManager().getPlayerByUUID(getPlayer().getUniqueID());

                    if (playerMP != null) {
                        DifficultyInstance difficultyAsync = getMc().getIntegratedServer().getDifficultyAsync(playerMP.worldObj, new BlockPos(playerMP));

                        if (difficultyAsync != null) {
                            difficultyForLocation = difficultyAsync;
                        }
                    }
                }

                list.add(String.format("Local Difficulty: %.2f (Day %d)", difficultyForLocation.getAdditionalDifficulty(), getWorld().getWorldTime() / 24000L));
            }

            if (getMc().entityRenderer != null && getMc().entityRenderer.isShaderActive())
            {
                list.add("Shader: " + getMc().entityRenderer.getShaderGroup().getShaderGroupName());
            }

            if (getMc().objectMouseOver != null && getMc().objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && getMc().objectMouseOver.getBlockPos() != null)
            {
                BlockPos mouseOverBlockPos = getMc().objectMouseOver.getBlockPos();
                list.add(String.format("Looking at: %d %d %d", mouseOverBlockPos.getX(), mouseOverBlockPos.getY(), mouseOverBlockPos.getZ()));
            }

            return list;
        }
    }

    protected List<String> getDebugInfoRight() {
        long i = Runtime.getRuntime().maxMemory();
        long j = Runtime.getRuntime().totalMemory();
        long k = Runtime.getRuntime().freeMemory();
        long l = j - k;
        List<String> list = Lists.newArrayList(String.format("Java: %s %dbit", System.getProperty("java.version"), getMc().isJava64bit() ? 64 : 32), String.format("Mem: % 2d%% %03d/%03dMB", l * 100L / i, bytesToMb(l), bytesToMb(i)), String.format("Allocated: % 2d%% %03dMB", j * 100L / i, bytesToMb(j)), "", String.format("CPU: %s", OpenGlHelper.func_183029_j()), "", String.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), GL11.glGetString(GL11.GL_VENDOR)), GL11.glGetString(GL11.GL_RENDERER), GL11.glGetString(GL11.GL_VERSION));

        long i1 = NativeMemory.getBufferAllocated();
        long j1 = NativeMemory.getBufferMaximum();
        String s = "Native: " + bytesToMb(i1) + "/" + bytesToMb(j1) + "MB";
        list.add(4, s);

        if (!this.isReducedDebug()) {
            if (getMc().objectMouseOver != null && getMc().objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && getMc().objectMouseOver.getBlockPos() != null) {
                BlockPos blockpos = getMc().objectMouseOver.getBlockPos();
                IBlockState iblockstate = getWorld().getBlockState(blockpos);

                if (getWorld().getWorldType() != WorldType.DEBUG_WORLD) {
                    iblockstate = iblockstate.getBlock().getActualState(iblockstate, getWorld(), blockpos);
                }

                list.add("");
                list.add(String.valueOf(Block.blockRegistry.getNameForObject(iblockstate.getBlock())));

                for (Entry<IProperty, Comparable> entry : iblockstate.getProperties().entrySet()) {
                    String s1 = entry.getValue().toString();

                    if (entry.getValue() == Boolean.TRUE) {
                        s1 = EnumChatFormatting.GREEN + s1;
                    } else if (entry.getValue() == Boolean.FALSE) {
                        s1 = EnumChatFormatting.RED + s1;
                    }

                    list.add(entry.getKey().getName() + ": " + s1);
                }
            }
        }
        return list;
    }

    private static long bytesToMb(long bytes)
    {
        return bytes / 1024L / 1024L;
    }
}
