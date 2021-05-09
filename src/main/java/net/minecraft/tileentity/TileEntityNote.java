package net.minecraft.tileentity;

import com.murengezi.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityNote extends TileEntity
{
    /** Note to play */
    public byte note;

    /** stores the latest redstone state */
    public boolean previousRedstoneState;

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setByte("note", this.note);
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.note = compound.getByte("note");
        this.note = (byte)MathHelper.clamp_int(this.note, 0, 24);
    }

    /**
     * change pitch by -> (currentPitch + 1) % 25
     */
    public void changePitch()
    {
        this.note = (byte)((this.note + 1) % 25);
        this.markDirty();
    }

    public void triggerNote(World world, BlockPos p_175108_2_)
    {
        if (world.getBlockState(p_175108_2_.up()).getBlock().getMaterial() == Material.air)
        {
            Material material = world.getBlockState(p_175108_2_.down()).getBlock().getMaterial();
            int i = 0;

            if (material == Material.rock)
            {
                i = 1;
            }

            if (material == Material.sand)
            {
                i = 2;
            }

            if (material == Material.glass)
            {
                i = 3;
            }

            if (material == Material.wood)
            {
                i = 4;
            }

            world.addBlockEvent(p_175108_2_, Blocks.noteblock, i, this.note);
        }
    }
}
