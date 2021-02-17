package net.minecraft.village;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;

public class MerchantRecipeList extends ArrayList<MerchantRecipe> {

    public MerchantRecipeList() {}

    public MerchantRecipeList(NBTTagCompound compound) {
        this.readRecipiesFromTags(compound);
    }

    /**
     * can par1,par2 be used to in crafting recipe par3
     */
    public MerchantRecipe canRecipeBeUsed(ItemStack itemStack1, ItemStack itemStack2, int p_77203_3_) {
        if (p_77203_3_ > 0 && p_77203_3_ < this.size()) {
            MerchantRecipe merchantrecipe1 = this.get(p_77203_3_);
            return !this.func_181078_a(itemStack1, merchantrecipe1.getItemToBuy()) || (itemStack2 != null || merchantrecipe1.hasSecondItemToBuy()) && (!merchantrecipe1.hasSecondItemToBuy() || !this.func_181078_a(itemStack2, merchantrecipe1.getSecondItemToBuy())) || itemStack1.stackSize < merchantrecipe1.getItemToBuy().stackSize || merchantrecipe1.hasSecondItemToBuy() && Objects.requireNonNull(itemStack2).stackSize < merchantrecipe1.getSecondItemToBuy().stackSize ? null : merchantrecipe1;
        } else {
            for (int i = 0; i < this.size(); ++i) {
                MerchantRecipe merchantrecipe = this.get(i);
                if (this.func_181078_a(itemStack1, merchantrecipe.getItemToBuy()) && itemStack1.stackSize >= merchantrecipe.getItemToBuy().stackSize && (!merchantrecipe.hasSecondItemToBuy() && itemStack2 == null || merchantrecipe.hasSecondItemToBuy() && this.func_181078_a(itemStack2, merchantrecipe.getSecondItemToBuy()) && itemStack2.stackSize >= merchantrecipe.getSecondItemToBuy().stackSize))
                {
                    return merchantrecipe;
                }
            }

            return null;
        }
    }

    private boolean func_181078_a(ItemStack p_181078_1_, ItemStack p_181078_2_) {
        return ItemStack.areItemsEqual(p_181078_1_, p_181078_2_) && (!p_181078_2_.hasTagCompound() || p_181078_1_.hasTagCompound() && NBTUtil.func_181123_a(p_181078_2_.getTagCompound(), p_181078_1_.getTagCompound(), false));
    }

    public void writeToBuf(PacketBuffer buffer) {
        buffer.writeByte((byte)(this.size() & 255));

        for (MerchantRecipe merchantrecipe : this) {
            buffer.writeItemStackToBuffer(merchantrecipe.getItemToBuy());
            buffer.writeItemStackToBuffer(merchantrecipe.getItemToSell());
            ItemStack itemstack = merchantrecipe.getSecondItemToBuy();
            buffer.writeBoolean(itemstack != null);

            if (itemstack != null) {
                buffer.writeItemStackToBuffer(itemstack);
            }

            buffer.writeBoolean(merchantrecipe.isRecipeDisabled());
            buffer.writeInt(merchantrecipe.getToolUses());
            buffer.writeInt(merchantrecipe.getMaxTradeUses());
        }
    }

    public static MerchantRecipeList readFromBuf(PacketBuffer buffer) throws IOException {
        MerchantRecipeList merchantrecipelist = new MerchantRecipeList();
        int i = buffer.readByte() & 255;

        for (int j = 0; j < i; ++j) {
            ItemStack itemstack = buffer.readItemStackFromBuffer();
            ItemStack itemstack1 = buffer.readItemStackFromBuffer();
            ItemStack itemstack2 = null;

            if (buffer.readBoolean()) {
                itemstack2 = buffer.readItemStackFromBuffer();
            }

            boolean flag = buffer.readBoolean();
            int k = buffer.readInt();
            int l = buffer.readInt();
            MerchantRecipe merchantrecipe = new MerchantRecipe(itemstack, itemstack2, itemstack1, k, l);

            if (flag) {
                merchantrecipe.compensateToolUses();
            }

            merchantrecipelist.add(merchantrecipe);
        }

        return merchantrecipelist;
    }

    public void readRecipiesFromTags(NBTTagCompound compound) {
        NBTTagList nbttaglist = compound.getTagList("Recipes", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            this.add(new MerchantRecipe(nbttagcompound));
        }
    }

    public NBTTagCompound getRecipiesAsTags() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();

        for (MerchantRecipe merchantrecipe : this) {
            nbttaglist.appendTag(merchantrecipe.writeToTags());
        }

        nbttagcompound.setTag("Recipes", nbttaglist);
        return nbttagcompound;
    }
}
