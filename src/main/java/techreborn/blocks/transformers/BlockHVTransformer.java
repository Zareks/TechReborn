package techreborn.blocks.transformers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import techreborn.init.ModBlocks;
import techreborn.tiles.energy.transformer.TileHVTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by modmuss50 on 16/03/2016.
 */
public class BlockHVTransformer extends BlockTransformer {

	public BlockHVTransformer() {
		super("hvtransformer");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		return new TileHVTransformer();
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> list = new ArrayList<>();
		list.add(new ItemStack(ModBlocks.machineframe, 1, 1));
		return list;
	}

}
