package net.minecraft.item;

import java.util.List;
import com.murengezi.minecraft.block.BlockStandingSign;
import com.murengezi.minecraft.block.BlockWallSign;
import com.murengezi.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBanner extends ItemBlock
{
    public ItemBanner()
    {
        super(Blocks.standing_banner);
        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
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
        else if (!world.getBlockState(pos).getBlock().getMaterial().isSolid())
        {
            return false;
        }
        else
        {
            pos = pos.offset(side);

            if (!player.canPlayerEdit(pos, side, stack))
            {
                return false;
            }
            else if (!Blocks.standing_banner.canPlaceBlockAt(world, pos))
            {
                return false;
            }
            else if (world.isRemote)
            {
                return true;
            }
            else
            {
                if (side == EnumFacing.UP)
                {
                    int i = MathHelper.floor_double((double)((player.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                    world.setBlockState(pos, Blocks.standing_banner.getDefaultState().withProperty(BlockStandingSign.ROTATION, Integer.valueOf(i)), 3);
                }
                else
                {
                    world.setBlockState(pos, Blocks.wall_banner.getDefaultState().withProperty(BlockWallSign.FACING, side), 3);
                }

                --stack.stackSize;
                TileEntity tileentity = world.getTileEntity(pos);

                if (tileentity instanceof TileEntityBanner)
                {
                    ((TileEntityBanner)tileentity).setItemValues(stack);
                }

                return true;
            }
        }
    }

    public String getItemStackDisplayName(ItemStack stack)
    {
        String s = "item.banner.";
        EnumDyeColor enumdyecolor = this.getBaseColor(stack);
        s = s + enumdyecolor.getUnlocalizedName() + ".name";
        return StatCollector.translateToLocal(s);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     *  
     * @param tooltip All lines to display in the Item's tooltip. This is a List of Strings.
     * @param advanced Whether the setting "Advanced tooltips" is enabled
     */
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        NBTTagCompound nbttagcompound = stack.getSubCompound("BlockEntityTag", false);

        if (nbttagcompound != null && nbttagcompound.hasKey("Patterns"))
        {
            NBTTagList nbttaglist = nbttagcompound.getTagList("Patterns", 10);

            for (int i = 0; i < nbttaglist.tagCount() && i < 6; ++i)
            {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                EnumDyeColor enumdyecolor = EnumDyeColor.byDyeDamage(nbttagcompound1.getInteger("Color"));
                TileEntityBanner.EnumBannerPattern tileentitybanner$enumbannerpattern = TileEntityBanner.EnumBannerPattern.getPatternByID(nbttagcompound1.getString("Pattern"));

                if (tileentitybanner$enumbannerpattern != null)
                {
                    tooltip.add(StatCollector.translateToLocal("item.banner." + tileentitybanner$enumbannerpattern.getPatternName() + "." + enumdyecolor.getUnlocalizedName()));
                }
            }
        }
    }

    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        if (renderPass == 0)
        {
            return 16777215;
        }
        else
        {
            EnumDyeColor enumdyecolor = this.getBaseColor(stack);
            return enumdyecolor.getMapColor().colorValue;
        }
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     *  
     * @param subItems The List of sub-items. This is a List of ItemStacks.
     */
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (EnumDyeColor enumdyecolor : EnumDyeColor.values())
        {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            TileEntityBanner.func_181020_a(nbttagcompound, enumdyecolor.getDyeDamage(), null);
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setTag("BlockEntityTag", nbttagcompound);
            ItemStack itemstack = new ItemStack(itemIn, 1, enumdyecolor.getDyeDamage());
            itemstack.setTagCompound(nbttagcompound1);
            subItems.add(itemstack);
        }
    }

    /**
     * gets the CreativeTab this item is displayed on
     */
    public CreativeTabs getCreativeTab()
    {
        return CreativeTabs.tabDecorations;
    }

    private EnumDyeColor getBaseColor(ItemStack stack)
    {
        NBTTagCompound nbttagcompound = stack.getSubCompound("BlockEntityTag", false);
        EnumDyeColor enumdyecolor = null;

        if (nbttagcompound != null && nbttagcompound.hasKey("Base"))
        {
            enumdyecolor = EnumDyeColor.byDyeDamage(nbttagcompound.getInteger("Base"));
        }
        else
        {
            enumdyecolor = EnumDyeColor.byDyeDamage(stack.getMetadata());
        }

        return enumdyecolor;
    }
}
