package net.optifine;

import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.block.BlockBush;
import com.murengezi.minecraft.block.BlockDoublePlant;
import com.murengezi.minecraft.block.BlockFence;
import com.murengezi.minecraft.block.BlockFenceGate;
import com.murengezi.minecraft.block.BlockFlower;
import com.murengezi.minecraft.block.BlockFlowerPot;
import com.murengezi.minecraft.block.BlockLever;
import com.murengezi.minecraft.block.BlockMushroom;
import com.murengezi.minecraft.block.BlockPane;
import com.murengezi.minecraft.block.BlockRedstoneTorch;
import com.murengezi.minecraft.block.BlockReed;
import com.murengezi.minecraft.block.BlockSapling;
import com.murengezi.minecraft.block.BlockSnow;
import com.murengezi.minecraft.block.BlockTallGrass;
import com.murengezi.minecraft.block.BlockTorch;
import com.murengezi.minecraft.block.BlockWall;
import com.murengezi.minecraft.block.state.IBlockState;
import com.murengezi.minecraft.client.resources.model.IBakedModel;
import com.murengezi.minecraft.init.Blocks;
import net.optifine.config.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BetterSnow {
   private static IBakedModel modelSnowLayer = null;

   public static void update() {
      modelSnowLayer = Config.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(Blocks.snow_layer.getDefaultState());
   }

   public static IBakedModel getModelSnowLayer() {
      return modelSnowLayer;
   }

   public static IBlockState getStateSnowLayer() {
      return Blocks.snow_layer.getDefaultState();
   }

   public static boolean shouldRender(IBlockAccess blockAccess, IBlockState blockState, BlockPos pos) {
      Block block = blockState.getBlock();
      return checkBlock(block, blockState) && hasSnowNeighbours(blockAccess, pos);
   }

   private static boolean hasSnowNeighbours(IBlockAccess blockAccess, BlockPos pos) {
      Block block = Blocks.snow_layer;
      return (blockAccess.getBlockState(pos.north()).getBlock() == block || blockAccess.getBlockState(pos.south()).getBlock() == block || blockAccess.getBlockState(pos.west()).getBlock() == block || blockAccess.getBlockState(pos.east()).getBlock() == block) && blockAccess.getBlockState(pos.down()).getBlock().isOpaqueCube();
   }

   private static boolean checkBlock(Block block, IBlockState blockState) {
      if(block.isFullBlock()) {
         return false;
      } else if(block.isOpaqueCube()) {
         return false;
      } else if(block instanceof BlockSnow) {
         return false;
      } else if(!(block instanceof BlockBush) || !(block instanceof BlockDoublePlant) && !(block instanceof BlockFlower) && !(block instanceof BlockMushroom) && !(block instanceof BlockSapling) && !(block instanceof BlockTallGrass)) {
         if(!(block instanceof BlockFence) && !(block instanceof BlockFenceGate) && !(block instanceof BlockFlowerPot) && !(block instanceof BlockPane) && !(block instanceof BlockReed) && !(block instanceof BlockWall)) {
            if(block instanceof BlockRedstoneTorch && blockState.getValue(BlockTorch.FACING) == EnumFacing.UP) {
               return true;
            } else {
               if(block instanceof BlockLever) {
                  Object object = blockState.getValue(BlockLever.FACING);
	               return object == BlockLever.EnumOrientation.UP_X || object == BlockLever.EnumOrientation.UP_Z;
               }

               return false;
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }
}
