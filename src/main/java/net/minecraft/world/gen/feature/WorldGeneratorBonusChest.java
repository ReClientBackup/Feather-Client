package net.minecraft.world.gen.feature;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class WorldGeneratorBonusChest extends WorldGenerator
{
    private final List<WeightedRandomChestContent> chestItems;

    /**
     * Value of this int will determine how much items gonna generate in Bonus Chest.
     */
    private final int itemsToGenerateInBonusChest;

    public WorldGeneratorBonusChest(List<WeightedRandomChestContent> p_i45634_1_, int p_i45634_2_)
    {
        this.chestItems = p_i45634_1_;
        this.itemsToGenerateInBonusChest = p_i45634_2_;
    }

    public boolean generate(World world, Random rand, BlockPos position)
    {
        Block block;

        while (((block = world.getBlockState(position).getBlock()).getMaterial() == Material.air || block.getMaterial() == Material.leaves) && position.getY() > 1)
        {
            position = position.down();
        }

        if (position.getY() < 1)
        {
            return false;
        }
        else
        {
            position = position.up();

            for (int i = 0; i < 4; ++i)
            {
                BlockPos blockpos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(3) - rand.nextInt(3), rand.nextInt(4) - rand.nextInt(4));

                if (world.isAirBlock(blockpos) && World.doesBlockHaveSolidTopSurface(world, blockpos.down()))
                {
                    world.setBlockState(blockpos, Blocks.chest.getDefaultState(), 2);
                    TileEntity tileentity = world.getTileEntity(blockpos);

                    if (tileentity instanceof TileEntityChest)
                    {
                        WeightedRandomChestContent.generateChestContents(rand, this.chestItems, (TileEntityChest)tileentity, this.itemsToGenerateInBonusChest);
                    }

                    BlockPos blockpos1 = blockpos.east();
                    BlockPos blockpos2 = blockpos.west();
                    BlockPos blockpos3 = blockpos.north();
                    BlockPos blockpos4 = blockpos.south();

                    if (world.isAirBlock(blockpos2) && World.doesBlockHaveSolidTopSurface(world, blockpos2.down()))
                    {
                        world.setBlockState(blockpos2, Blocks.torch.getDefaultState(), 2);
                    }

                    if (world.isAirBlock(blockpos1) && World.doesBlockHaveSolidTopSurface(world, blockpos1.down()))
                    {
                        world.setBlockState(blockpos1, Blocks.torch.getDefaultState(), 2);
                    }

                    if (world.isAirBlock(blockpos3) && World.doesBlockHaveSolidTopSurface(world, blockpos3.down()))
                    {
                        world.setBlockState(blockpos3, Blocks.torch.getDefaultState(), 2);
                    }

                    if (world.isAirBlock(blockpos4) && World.doesBlockHaveSolidTopSurface(world, blockpos4.down()))
                    {
                        world.setBlockState(blockpos4, Blocks.torch.getDefaultState(), 2);
                    }

                    return true;
                }
            }

            return false;
        }
    }
}
