package com.murengezi.minecraft.profiler;

public interface IPlayerUsage {

    void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper);

    void addServerTypeToSnooper(PlayerUsageSnooper playerSnooper);

    boolean isSnooperEnabled();
}
