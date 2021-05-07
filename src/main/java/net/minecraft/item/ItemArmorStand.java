package net.minecraft.item;

import java.util.List;
import java.util.Random;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Rotations;
import net.minecraft.world.World;

public class ItemArmorStand extends Item
{
    public ItemArmorStand()
    {
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Called when a Block is right-clicked with this Item
     *  
     * @param pos The block being right-clicked
     * @param side The side being right-clicked
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (side == EnumFacing.DOWN)
        {
            return false;
        }
        else
        {
            boolean flag = world.getBlockState(pos).getBlock().isReplaceable(world, pos);
            BlockPos blockpos = flag ? pos : pos.offset(side);

            if (!player.canPlayerEdit(blockpos, side, stack))
            {
                return false;
            }
            else
            {
                BlockPos blockpos1 = blockpos.up();
                boolean flag1 = !world.isAirBlock(blockpos) && !world.getBlockState(blockpos).getBlock().isReplaceable(world, blockpos);
                flag1 = flag1 | (!world.isAirBlock(blockpos1) && !world.getBlockState(blockpos1).getBlock().isReplaceable(world, blockpos1));

                if (flag1)
                {
                    return false;
                }
                else
                {
                    double d0 = blockpos.getX();
                    double d1 = blockpos.getY();
                    double d2 = blockpos.getZ();
                    List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB.fromBounds(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));

                    if (list.size() > 0)
                    {
                        return false;
                    }
                    else
                    {
                        if (!world.isRemote)
                        {
                            world.setBlockToAir(blockpos);
                            world.setBlockToAir(blockpos1);
                            EntityArmorStand entityarmorstand = new EntityArmorStand(world, d0 + 0.5D, d1, d2 + 0.5D);
                            float f = (float)MathHelper.floor_float((MathHelper.wrapAngleTo180_float(player.rotationYaw - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                            entityarmorstand.setLocationAndAngles(d0 + 0.5D, d1, d2 + 0.5D, f, 0.0F);
                            this.applyRandomRotations(entityarmorstand, world.rand);
                            NBTTagCompound nbttagcompound = stack.getTagCompound();

                            if (nbttagcompound != null && nbttagcompound.hasKey("EntityTag", 10))
                            {
                                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                                entityarmorstand.writeToNBTOptional(nbttagcompound1);
                                nbttagcompound1.merge(nbttagcompound.getCompoundTag("EntityTag"));
                                entityarmorstand.readFromNBT(nbttagcompound1);
                            }

                            world.spawnEntityInWorld(entityarmorstand);
                        }

                        --stack.stackSize;
                        return true;
                    }
                }
            }
        }
    }

    private void applyRandomRotations(EntityArmorStand armorStand, Random rand)
    {
        Rotations rotations = armorStand.getHeadRotation();
        float f = rand.nextFloat() * 5.0F;
        float f1 = rand.nextFloat() * 20.0F - 10.0F;
        Rotations rotations1 = new Rotations(rotations.getX() + f, rotations.getY() + f1, rotations.getZ());
        armorStand.setHeadRotation(rotations1);
        rotations = armorStand.getBodyRotation();
        f = rand.nextFloat() * 10.0F - 5.0F;
        rotations1 = new Rotations(rotations.getX(), rotations.getY() + f, rotations.getZ());
        armorStand.setBodyRotation(rotations1);
    }
}
