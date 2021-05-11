package com.murengezi.minecraft.client.resources;

import java.util.List;

public interface IReloadableResourceManager extends IResourceManager {

	void reloadResources(List<IResourcePack> resourcePacks);

	void registerReloadListener(IResourceManagerReloadListener reloadListener);

}
