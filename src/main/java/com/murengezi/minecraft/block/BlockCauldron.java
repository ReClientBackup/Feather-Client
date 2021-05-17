package com.murengezi.minecraft.block;

import java.util.List;
import java.util.Random;
import com.murengezi.minecraft.block.material.MapColor;
import com.murengezi.minecraft.block.material.Material;
import com.murengezi.minecraft.block.properties.PropertyInteger;
import com.murengezi.minecraft.block.state.BlockState;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import com.murengezi.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCauldron extends Block {

    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 3);

    public BlockCauldron() {
        super(Material.iron, MapColor.stoneColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
    }

    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
        super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
        float f = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
        this.setBlockBoundsForItemRender();
    }

    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entityIn) {
        int i = state.getValue(LEVEL);
        float f = (float)pos.getY() + (6.0F + (float)(3 * i)) / 16.0F;

        if (!world.isRemote && entityIn.isBurning() && i > 0 && entityIn.getEntityBoundingBox().minY <= (double)f) {
            entityIn.extinguish();
            this.setWaterLevel(world, pos, state, i - 1);
        }
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        } else {
            ItemStack itemstack = player.inventory.getCurrentItem();

            if (itemstack == null) {
                return true;
            } else {
                int i = state.getValue(LEVEL);
                Item item = itemstack.getItem();

                if (item == Items.water_bucket) {
                    if (i < 3) {
                        if (!player.capabilities.isCreativeMode) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.bucket));
                        }

                        player.triggerAchievement(StatList.field_181725_I);
                        this.setWaterLevel(world, pos, state, 3);
                    }

                    return true;
                } else if (item == Items.glass_bottle) {
                    if (i > 0) {
                        if (!player.capabilities.isCreativeMode) {
                            ItemStack itemstack2 = new ItemStack(Items.potionitem, 1, 0);

                            if (!player.inventory.addItemStackToInventory(itemstack2)) {
                                world.spawnEntityInWorld(new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.5D, (double)pos.getZ() + 0.5D, itemstack2));
                            } else if (player instanceof EntityPlayerMP) {
                                ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
                            }

                            player.triggerAchievement(StatList.field_181726_J);
                            --itemstack.stackSize;

                            if (itemstack.stackSize <= 0) {
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                            }
                        }

                        this.setWaterLevel(world, pos, state, i - 1);
                    }

                    return true;
                } else {
                    if (i > 0 && item instanceof ItemArmor) {
                        ItemArmor itemarmor = (ItemArmor)item;

                        if (itemarmor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER && itemarmor.hasColor(itemstack)) {
                            itemarmor.removeColor(itemstack);
                            this.setWaterLevel(world, pos, state, i - 1);
                            player.triggerAchievement(StatList.field_181727_K);
                            return true;
                        }
                    }

                    if (i > 0 && item instanceof ItemBanner && TileEntityBanner.getPatterns(itemstack) > 0) {
                        ItemStack itemstack1 = itemstack.copy();
                        itemstack1.stackSize = 1;
                        TileEntityBanner.removeBannerData(itemstack1);

                        if (itemstack.stackSize <= 1 && !player.capabilities.isCreativeMode) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, itemstack1);
                        } else {
                            if (!player.inventory.addItemStackToInventory(itemstack1)) {
                                world.spawnEntityInWorld(new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.5D, (double)pos.getZ() + 0.5D, itemstack1));
                            } else if (player instanceof EntityPlayerMP) {
                                ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
                            }

                            player.triggerAchievement(StatList.field_181728_L);

                            if (!player.capabilities.isCreativeMode) {
                                --itemstack.stackSize;
                            }
                        }

                        if (!player.capabilities.isCreativeMode) {
                            this.setWaterLevel(world, pos, state, i - 1);
                        }

                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
    }

    public void setWaterLevel(World world, BlockPos pos, IBlockState state, int level) {
        world.setBlockState(pos, state.withProperty(LEVEL, MathHelper.clamp_int(level, 0, 3)), 2);
        world.updateComparatorOutputLevel(pos, this);
    }

    public void fillWithRain(World world, BlockPos pos) {
        if (world.rand.nextInt(20) == 1) {
            IBlockState iblockstate = world.getBlockState(pos);

            if (iblockstate.getValue(LEVEL) < 3) {
                world.setBlockState(pos, iblockstate.cycleProperty(LEVEL), 2);
            }
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.cauldron;
    }

    public Item getItem(World world, BlockPos pos) {
        return Items.cauldron;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(World world, BlockPos pos) {
        return world.getBlockState(pos).getValue(LEVEL);
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LEVEL, meta);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(LEVEL);
    }

    protected BlockState createBlockState() {
        return new BlockState(this, LEVEL);
    }

}
