package techreborn.proxies;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import reborncore.RebornCore;
import reborncore.client.multiblock.MultiblockRenderEvent;
import reborncore.common.blocks.BlockMachineBase;
import techreborn.blocks.BlockRubberLeaves;
import techreborn.client.ClientMultiBlocks;
import techreborn.client.RegisterItemJsons;
import techreborn.client.StackToolTipEvent;
import techreborn.client.hud.HudStackInfo;
import techreborn.client.keybindings.KeyBindings;
import techreborn.client.render.ModelDynamicCell;
import techreborn.client.render.entitys.RenderNukePrimed;
import techreborn.client.render.tiles.GlowHandler;
import techreborn.entitys.EntityNukePrimed;
import techreborn.init.ModBlocks;
import techreborn.lib.ModInfo;
import techreborn.manual.loader.ManualLoader;

import java.io.File;

public class ClientProxy extends CommonProxy {

	public static MultiblockRenderEvent multiblockRenderEvent;

	public static GlowHandler handler;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);

		RenderingRegistry.registerEntityRenderingHandler(EntityNukePrimed.class, new RenderManagerNuke());

		ManualLoader loader = new ManualLoader(new File(event.getModConfigurationDirectory(), "techreborn"));

		//		new Thread(() ->
		//		{
		//			try {
		//				loader.load();
		//			} catch (IOException e) {
		//				e.printStackTrace();
		//			}
		//		}).start();

		for (Object object : RebornCore.jsonDestroyer.objectsToDestroy) {
			if (object instanceof BlockMachineBase) {
				BlockMachineBase base = (BlockMachineBase) object;
				registerItemModel(Item.getItemFromBlock(base));
			}
		}

		ModelDynamicCell.init();
		RegisterItemJsons.registerModels();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		MinecraftForge.EVENT_BUS.register(new HudStackInfo());
		//MinecraftForge.EVENT_BUS.register(new VersionCheckerClient());
		MinecraftForge.EVENT_BUS.register(new StackToolTipEvent());
		multiblockRenderEvent = new MultiblockRenderEvent();
		MinecraftForge.EVENT_BUS.register(multiblockRenderEvent);

		ClientRegistry.registerKeyBinding(KeyBindings.config);
		ClientMultiBlocks.init();
		StateMap rubberLeavesStateMap = new StateMap.Builder().ignore(BlockRubberLeaves.CHECK_DECAY, BlockRubberLeaves.DECAYABLE).build();
		ModelLoader.setCustomStateMapper(ModBlocks.rubberLeaves, rubberLeavesStateMap);
		GlowHandler.createAndLoad();
	}

	protected void registerItemModel(ItemStack item, String name) {
		// tell Minecraft which textures it has to load. This is resource-domain sensitive
		ModelLoader.registerItemVariants(item.getItem(), new ResourceLocation(name));
		// tell the game which model to use for this item-meta combination
		ModelLoader.setCustomModelResourceLocation(item.getItem(), item
			.getMetadata(), new ModelResourceLocation(name, "inventory"));
	}

	public ResourceLocation registerItemModel(Item item) {
		ResourceLocation itemLocation = getItemLocation(item);
		if (itemLocation == null) {
			return null;
		}

		return registerIt(item, itemLocation);
	}

	public static ResourceLocation getItemLocation(Item item) {
		Object o = item.getRegistryName();
		if (o == null) {
			return null;
		}
		return (ResourceLocation) o;
	}

	private static ResourceLocation registerIt(Item item, final ResourceLocation location) {
		ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				return new ModelResourceLocation(location, "inventory");
			}
		});
		ModelLoader.registerItemVariants(item, location);

		return location;
	}

	public class RenderManagerNuke implements IRenderFactory<EntityNukePrimed> {

		@Override
		public Render<? super EntityNukePrimed> createRenderFor(RenderManager manager) {
			return new RenderNukePrimed(manager);
		}
	}

	@Override
	public void registerFluidBlockRendering(Block block, String name) {
		super.registerFluidBlockRendering(block, name);
		final ModelResourceLocation fluidLocation = new ModelResourceLocation(ModInfo.MOD_ID.toLowerCase() + ":fluids", name);

		// use a custom state mapper which will ignore the LEVEL property
		ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return fluidLocation;
			}
		});
	}

	@Override
	public void registerCustomBlockSateLocation(Block block, String resourceLocation) {
		super.registerCustomBlockSateLocation(block, resourceLocation);
		ModelLoader.setCustomStateMapper(block, new DefaultStateMapper() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				String resourceDomain = Block.REGISTRY.getNameForObject(state.getBlock()).getResourceDomain();
				String propertyString = getPropertyString(state.getProperties());
				return new ModelResourceLocation(resourceDomain + ':' + resourceLocation, propertyString);
			}
		});
		String resourceDomain = Block.REGISTRY.getNameForObject(block).getResourceDomain();
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(resourceDomain + ':' + resourceLocation, "inventory"));
	}

	@Override
	public void registerCustomBlockStateLocation(Block block, String resourceLocation, boolean item) {
		super.registerCustomBlockStateLocation(block, resourceLocation, item);
		ModelLoader.setCustomStateMapper(block, new DefaultStateMapper() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				String resourceDomain = Block.REGISTRY.getNameForObject(state.getBlock()).getResourceDomain();
				String propertyString = getPropertyString(state.getProperties());
				return new ModelResourceLocation(resourceDomain + ':' + resourceLocation, propertyString);
			}
		});
		if (item) {
			String resourceDomain = Block.REGISTRY.getNameForObject(block).getResourceDomain();
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(resourceDomain + ':' + resourceLocation, "inventory"));
		}
	}

	@Override
	public boolean isCTMAvailable() {
		return isChiselAround;
	}
}
