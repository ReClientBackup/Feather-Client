package com.murengezi.minecraft.profiler;

import com.google.common.collect.Maps;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.util.HttpUtil;

public class PlayerUsageSnooper {
    private final Map<String, Object> serverStats = Maps.newHashMap(), clientStats = Maps.newHashMap();
    private final String uniqueID = UUID.randomUUID().toString();
    private final URL serverUrl;
    private final IPlayerUsage playerStatsCollector;
    private final Timer threadTrigger = new Timer("Snooper Timer", true);
    private final Object syncLock = new Object();
    private final long minecraftStartTimeMillis;
    private boolean isRunning;
    private int selfCounter;

    public PlayerUsageSnooper(String p_i1563_1_, IPlayerUsage playerStatCollector, long startTime) {
        try {
            this.serverUrl = new URL("http://snoop.minecraft.net/" + p_i1563_1_ + "?version=" + 2);
        } catch (MalformedURLException var6) {
            throw new IllegalArgumentException();
        }

        this.playerStatsCollector = playerStatCollector;
        this.minecraftStartTimeMillis = startTime;
    }

    public void startSnooper() {
        if (!this.isRunning) {
            this.isRunning = true;
            this.addSystemStatsToSnooper();
            this.threadTrigger.schedule(new TimerTask() {
                public void run() {
                    if (PlayerUsageSnooper.this.playerStatsCollector.isSnooperEnabled()) {
                        Map<String, Object> map;

                        synchronized (PlayerUsageSnooper.this.syncLock) {
                            map = Maps.newHashMap(PlayerUsageSnooper.this.clientStats);

                            if (PlayerUsageSnooper.this.selfCounter == 0) {
                                map.putAll(PlayerUsageSnooper.this.serverStats);
                            }

                            map.put("snooper_count", PlayerUsageSnooper.this.selfCounter++);
                            map.put("snooper_token", PlayerUsageSnooper.this.uniqueID);
                        }

                        HttpUtil.postMap(PlayerUsageSnooper.this.serverUrl, map, true);
                    }
                }
            }, 0L, 900000L);
        }
    }

    private void addSystemStatsToSnooper() {
        this.addJvmArgsToSnooper();
        this.addClientStat("snooper_token", this.uniqueID);
        this.addStatToSnooper("snooper_token", this.uniqueID);
        this.addStatToSnooper("os_name", System.getProperty("os.name"));
        this.addStatToSnooper("os_version", System.getProperty("os.version"));
        this.addStatToSnooper("os_architecture", System.getProperty("os.arch"));
        this.addStatToSnooper("java_version", System.getProperty("java.version"));
        this.addClientStat("version", "1.8.10");
        this.playerStatsCollector.addServerTypeToSnooper(this);
    }

    private void addJvmArgsToSnooper() {
        RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
        List<String> list = runtimemxbean.getInputArguments();
        int i = 0;

        for (String s : list) {
            if (s.startsWith("-X")) {
                this.addClientStat("jvm_arg[" + i++ + "]", s);
            }
        }

        this.addClientStat("jvm_args", i);
    }

    public void addMemoryStatsToSnooper() {
        this.addStatToSnooper("memory_total", Runtime.getRuntime().totalMemory());
        this.addStatToSnooper("memory_max", Runtime.getRuntime().maxMemory());
        this.addStatToSnooper("memory_free", Runtime.getRuntime().freeMemory());
        this.addStatToSnooper("cpu_cores", Runtime.getRuntime().availableProcessors());
        this.playerStatsCollector.addServerStatsToSnooper(this);
    }

    public void addClientStat(String key, Object value) {
        synchronized (this.syncLock) {
            this.clientStats.put(key, value);
        }
    }

    public void addStatToSnooper(String key, Object value) {
        synchronized (this.syncLock) {
            this.serverStats.put(key, value);
        }
    }

    public Map<String, String> getCurrentStats() {
        Map<String, String> map = Maps.newLinkedHashMap();

        synchronized (this.syncLock) {
            this.addMemoryStatsToSnooper();

            for (Entry<String, Object> entry : this.serverStats.entrySet()) {
                map.put(entry.getKey(), entry.getValue().toString());
            }

            for (Entry<String, Object> entry1 : this.clientStats.entrySet()) {
                map.put(entry1.getKey(), entry1.getValue().toString());
            }

            return map;
        }
    }

    public boolean isSnooperRunning() {
        return this.isRunning;
    }

    public void stopSnooper() {
        this.threadTrigger.cancel();
    }

    public String getUniqueID() {
        return this.uniqueID;
    }

    public long getMinecraftStartTimeMillis() {
        return this.minecraftStartTimeMillis;
    }
}
