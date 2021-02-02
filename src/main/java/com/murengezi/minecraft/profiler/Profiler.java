package com.murengezi.minecraft.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.src.Config;
import net.optifine.Lagometer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler {
    private static final Logger logger = LogManager.getLogger();
    private final List<String> sectionList = Lists.newArrayList();
    private final List<Long> timestampList = Lists.newArrayList();
    public boolean profilingEnabled;
    private String profilingSection = "";
    private final Map<String, Long> profilingMap = Maps.newHashMap();
    public boolean profilerGlobalEnabled = true;
    private boolean profilerLocalEnabled;
    private static final String SCHEDULED_EXECUTABLES = "scheduledExecutables";
    private static final String TICK = "tick";
    private static final String PRE_RENDER_ERRORS = "preRenderErrors";
    private static final String RENDER = "render";
    private static final String DISPLAY = "display";
    private static final int HASH_SCHEDULED_EXECUTABLES = SCHEDULED_EXECUTABLES.hashCode();
    private static final int HASH_TICK = TICK.hashCode();
    private static final int HASH_PRE_RENDER_ERRORS = PRE_RENDER_ERRORS.hashCode();
    private static final int HASH_RENDER = RENDER.hashCode();
    private static final int HASH_DISPLAY = DISPLAY.hashCode();

    public Profiler() {
        this.profilerLocalEnabled = true;
    }

    public void clearProfiling() {
        this.profilingMap.clear();
        this.profilingSection = "";
        this.sectionList.clear();
        this.profilerLocalEnabled = this.profilerGlobalEnabled;
    }

    public void startSection(String name) {
        int hashCode = name.hashCode();

        if (Lagometer.isActive()) {
            if (hashCode == HASH_SCHEDULED_EXECUTABLES && name.equals(SCHEDULED_EXECUTABLES)) {
                Lagometer.timerScheduledExecutables.start();
            } else if (hashCode == HASH_TICK && name.equals(TICK) && Config.isMinecraftThread()) {
                Lagometer.timerScheduledExecutables.end();
                Lagometer.timerTick.start();
            } else if (hashCode == HASH_PRE_RENDER_ERRORS && name.equals(PRE_RENDER_ERRORS)) {
                Lagometer.timerTick.end();
            }
        }

        if (Config.isFastRender()) {
            if (hashCode == HASH_RENDER && name.equals(RENDER)) {
                GlStateManager.clearEnabled = false;
            } else if (hashCode == HASH_DISPLAY && name.equals(DISPLAY)) {
                GlStateManager.clearEnabled = true;
            }
        }

        if (this.profilerLocalEnabled && this.profilingEnabled) {
            if (this.profilingSection.length() > 0) {
                this.profilingSection = this.profilingSection + ".";
            }

            this.profilingSection = this.profilingSection + name;
            this.sectionList.add(this.profilingSection);
            this.timestampList.add(System.nanoTime());
        }
    }

    public void endSection() {
        if (this.profilerLocalEnabled) {
            if (this.profilingEnabled) {
                long nanoTime = System.nanoTime();
                long timeStamp = this.timestampList.remove(this.timestampList.size() - 1);
                this.sectionList.remove(this.sectionList.size() - 1);
                long pastNanoTime = nanoTime - timeStamp;

                if (this.profilingMap.containsKey(this.profilingSection)) {
                    this.profilingMap.put(this.profilingSection, this.profilingMap.get(this.profilingSection) + pastNanoTime);
                } else {
                    this.profilingMap.put(this.profilingSection, pastNanoTime);
                }

                if (pastNanoTime > 100000000L) {
                    logger.warn("Something's taking too long! '" + this.profilingSection + "' took approx " + (double)pastNanoTime / 1000000.0D + " ms");
                }

                this.profilingSection = !this.sectionList.isEmpty() ? this.sectionList.get(this.sectionList.size() - 1) : "";
            }
        }
    }

    public List<Profiler.Result> getProfilingData(String p_76321_1_) {
        if (!this.profilingEnabled) {
            return null;
        } else {
            long i = this.profilingMap.getOrDefault("root", 0L);
            long j = this.profilingMap.getOrDefault(p_76321_1_, -1L);
            List<Profiler.Result> list = Lists.newArrayList();

            if (p_76321_1_.length() > 0) {
                p_76321_1_ = p_76321_1_ + ".";
            }

            long k = 0L;

            for (String s : this.profilingMap.keySet()) {
                if (s.length() > p_76321_1_.length() && s.startsWith(p_76321_1_) && s.indexOf(".", p_76321_1_.length() + 1) < 0) {
                    k += this.profilingMap.get(s);
                }
            }

            float f = (float)k;

            if (k < j) {
                k = j;
            }

            if (i < k) {
                i = k;
            }

            for (String s1 : this.profilingMap.keySet()) {
                if (s1.length() > p_76321_1_.length() && s1.startsWith(p_76321_1_) && s1.indexOf(".", p_76321_1_.length() + 1) < 0) {
                    long l = this.profilingMap.get(s1);
                    double d0 = (double)l * 100.0D / (double)k;
                    double d1 = (double)l * 100.0D / (double)i;
                    String s2 = s1.substring(p_76321_1_.length());
                    list.add(new Profiler.Result(s2, d0, d1));
                }
            }

            this.profilingMap.replaceAll((s, v) -> this.profilingMap.get(s) * 950L / 1000L);

            if ((float)k > f) {
                list.add(new Profiler.Result("unspecified", (double)((float)k - f) * 100.0D / (double)k, (double)((float)k - f) * 100.0D / (double)i));
            }

            Collections.sort(list);
            list.add(0, new Profiler.Result(p_76321_1_, 100.0D, (double)k * 100.0D / (double)i));
            return list;
        }
    }

    public void endStartSection(String name) {
        if (this.profilerLocalEnabled) {
            this.endSection();
            this.startSection(name);
        }
    }

    public String getNameOfLastSection() {
        return this.sectionList.size() == 0 ? "[UNKNOWN]" : this.sectionList.get(this.sectionList.size() - 1);
    }

    public void startSection(Class<?> clazz) {
        if (this.profilingEnabled) {
            this.startSection(clazz.getSimpleName());
        }
    }

    public static final class Result implements Comparable<Profiler.Result> {
        public double field_76332_a;
        public double field_76330_b;
        public String field_76331_c;

        public Result(String p_i1554_1_, double p_i1554_2_, double p_i1554_4_) {
            this.field_76331_c = p_i1554_1_;
            this.field_76332_a = p_i1554_2_;
            this.field_76330_b = p_i1554_4_;
        }

        public int compareTo(Profiler.Result result) {
            return result.field_76332_a < this.field_76332_a ? -1 : (result.field_76332_a > this.field_76332_a ? 1 : result.field_76331_c.compareTo(this.field_76331_c));
        }

        public int func_76329_a() {
            return (this.field_76331_c.hashCode() & 11184810) + 4473924;
        }
    }
}
