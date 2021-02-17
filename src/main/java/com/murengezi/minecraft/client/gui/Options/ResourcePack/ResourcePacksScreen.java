package com.murengezi.minecraft.client.gui.Options.ResourcePack;

import com.google.common.collect.Lists;
import com.murengezi.chocolate.Chocolate;
import com.murengezi.minecraft.client.gui.GuiButton;
import com.murengezi.minecraft.client.gui.Screen;
import net.minecraft.client.gui.GuiOptionButton;
import com.murengezi.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.Util;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-24 at 15:23
 */
public class ResourcePacksScreen extends Screen {

	private final Screen previousScreen;
	private List<ResourcePackListEntry> availableResourcePacks;
	private List<ResourcePackListEntry> selectedResourcePacks;
	private ResourcePackAvailableList availableResourcePacksList;
	private ResourcePackSelectedList selectedResourcePacksList;
	private boolean changed;

	private static final int FOLDER = 0, DONE = 1;

	public ResourcePacksScreen(Screen previousScreen) {
		this.previousScreen = previousScreen;
	}

	@Override
	public void initGui() {
		addButton(new GuiOptionButton(FOLDER, this.width / 2 - 154, this.height - 40, I18n.format("resourcePack.openFolder")));
		addButton(new GuiOptionButton(DONE, this.width / 2 + 4, this.height - 40, I18n.format("gui.done")));

		if (!this.changed) {
			availableResourcePacks = new ArrayList<>();
			selectedResourcePacks = new ArrayList<>();
			ResourcePackRepository repository = getMc().getResourcePackRepository();
			repository.updateRepositoryEntriesAll();
			List<ResourcePackRepository.Entry> list = Lists.newArrayList(repository.getRepositoryEntriesAll());
			list.removeAll(repository.getRepositoryEntries());


			for (ResourcePackRepository.Entry entry : list) {
				this.availableResourcePacks.add(new ResourcePackListEntryFound(this, entry));
			}

			for (ResourcePackRepository.Entry entry : Lists.reverse(repository.getRepositoryEntries())) {
				this.selectedResourcePacks.add(new ResourcePackListEntryFound(this, entry));
			}

			this.selectedResourcePacks.add(new ResourcePackListEntryDefault(this));
		}

		this.availableResourcePacksList = new ResourcePackAvailableList(200, this.height, this.availableResourcePacks);
		this.availableResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
		this.availableResourcePacksList.registerScrollButtons(7, 8);
		this.selectedResourcePacksList = new ResourcePackSelectedList(200, this.height, this.selectedResourcePacks);
		this.selectedResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 + 4);
		this.selectedResourcePacksList.registerScrollButtons(7, 8);
		super.initGui();
	}

	@Override
	public void handleMouseInput() throws IOException {
		this.availableResourcePacksList.handleMouseInput();
		this.selectedResourcePacksList.handleMouseInput();
		super.handleMouseInput();
	}

	public boolean hasResourcePackEntry(ResourcePackListEntry entry) {
		return this.selectedResourcePacks.contains(entry);
	}

	public List<ResourcePackListEntry> getListContaining(ResourcePackListEntry entry) {
		return this.hasResourcePackEntry(entry) ? this.selectedResourcePacks : this.availableResourcePacks;
	}

	public List<ResourcePackListEntry> getAvailableResourcePacks() {
		return this.availableResourcePacks;
	}

	public List<ResourcePackListEntry> getSelectedResourcePacks() {
		return this.selectedResourcePacks;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.isEnabled()) {
			switch (button.getId()) {
				case FOLDER:
					File file1 = getMc().getResourcePackRepository().getDirResourcepacks();
					String s = file1.getAbsolutePath();

					if (Util.getOSType() == Util.EnumOS.OSX) {
						try {
							Chocolate.getLogger().info(s);
							Runtime.getRuntime().exec(new String[] {"/usr/bin/open", s});
							return;
						} catch (IOException ioexception1) {
							Chocolate.getLogger().error("Couldn't open file", ioexception1);
						}
					} else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
						String s1 = String.format("cmd.exe /C start \"Open file\" \"%s\"", s);

						try {
							Runtime.getRuntime().exec(s1);
							return;
						} catch (IOException ioException)
						{
							Chocolate.getLogger().error("Couldn't open file", ioException);
						}
					}

					boolean flag = false;

					try {
						Class<?> clazz = Class.forName("java.awt.Desktop");
						Object object = clazz.getMethod("getDesktop").invoke(null);
						clazz.getMethod("browse", URI.class).invoke(object, file1.toURI());
					} catch (Throwable throwable) {
						Chocolate.getLogger().error("Couldn't open link", throwable);
						flag = true;
					}

					if (flag) {
						Chocolate.getLogger().info("Opening via system class!");
						Sys.openURL("file://" + s);
					}
					break;
				case DONE:
					if (this.changed) {
						List<ResourcePackRepository.Entry> list = new ArrayList<>();

						for (ResourcePackListEntry resourcepacklistentry : this.selectedResourcePacks) {
							if (resourcepacklistentry instanceof ResourcePackListEntryFound) {
								list.add(((ResourcePackListEntryFound)resourcepacklistentry).getEntry());
							}
						}

						Collections.reverse(list);
						getMc().getResourcePackRepository().setRepositories(list);
						getMc().gameSettings.resourcePacks.clear();
						getMc().gameSettings.field_183018_l.clear();

						for (ResourcePackRepository.Entry entry : list)
						{
							getMc().gameSettings.resourcePacks.add(entry.getResourcePackName());

							if (entry.getPackFormat() != 1)
							{
								getMc().gameSettings.field_183018_l.add(entry.getResourcePackName());
							}
						}

						saveSettings();
						getMc().refreshResources();
					}

					changeScreen(this.previousScreen);
					break;
			}
		}
		super.actionPerformed(button);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		this.availableResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
		this.selectedResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		this.drawWorldBackground(mouseX, mouseY, 60);

		scissorBox(availableResourcePacksList.getLeft(), availableResourcePacksList.getTop(), availableResourcePacksList.getRight(), availableResourcePacksList.getBottom(), new ScaledResolution());
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		this.availableResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);


		scissorBox(selectedResourcePacksList.getLeft(), selectedResourcePacksList.getTop(), selectedResourcePacksList.getRight(), selectedResourcePacksList.getBottom(), new ScaledResolution());
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		this.selectedResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		drawRect(0, 0, this.width, availableResourcePacksList.getTop(), Integer.MIN_VALUE);
		drawRect(0, availableResourcePacksList.getBottom(), this.width, this.height, Integer.MIN_VALUE);

		getFr().drawCenteredString(I18n.format("resourcePack.title"), this.width / 2, 16, 16777215);
		getFr().drawCenteredString(I18n.format("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 18, 8421504);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void markChanged() {
		this.changed = true;
	}
}
