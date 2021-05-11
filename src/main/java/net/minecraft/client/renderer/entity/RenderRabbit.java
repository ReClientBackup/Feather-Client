package net.minecraft.client.renderer.entity;

import com.murengezi.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class RenderRabbit extends RenderLiving<EntityRabbit> {

	private static final ResourceLocation BROWN = new ResourceLocation("textures/entity/rabbit/brown.png"), WHITE = new ResourceLocation("textures/entity/rabbit/white.png"), BLACK = new ResourceLocation("textures/entity/rabbit/black.png"), GOLD = new ResourceLocation("textures/entity/rabbit/gold.png"), SALT = new ResourceLocation("textures/entity/rabbit/salt.png"), WHITE_SPLOTCHED = new ResourceLocation("textures/entity/rabbit/white_splotched.png"), TOAST = new ResourceLocation("textures/entity/rabbit/toast.png"), CAERBANNOG = new ResourceLocation("textures/entity/rabbit/caerbannog.png");

	public RenderRabbit(RenderManager renderManager, ModelBase modelBase, float shadowSize) {
		super(renderManager, modelBase, shadowSize);
	}

	protected ResourceLocation getEntityTexture(EntityRabbit entity) {
		String s = EnumChatFormatting.getTextWithoutFormattingCodes(entity.getCommandSenderName());

		if (s != null && s.equals("Toast")) {
			return TOAST;
		} else {
			switch (entity.getRabbitType()) {
				case 0:
				default:
					return BROWN;
				case 1:
					return WHITE;
				case 2:
					return BLACK;
				case 3:
					return WHITE_SPLOTCHED;
				case 4:
					return GOLD;
				case 5:
					return SALT;
				case 99:
					return CAERBANNOG;
			}
		}
	}

}
