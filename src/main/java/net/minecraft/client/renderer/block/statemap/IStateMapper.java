package net.minecraft.client.renderer.block.statemap;

import java.util.Map;
import com.murengezi.minecraft.block.Block;
import com.murengezi.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public interface IStateMapper {

    Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block);

}
