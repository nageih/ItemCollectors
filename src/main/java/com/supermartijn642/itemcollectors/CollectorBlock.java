package com.supermartijn642.itemcollectors;

import com.google.common.collect.Lists;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.ToolType;
import com.supermartijn642.core.block.BaseBlock;
import com.supermartijn642.itemcollectors.screen.BasicCollectorScreen;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created 7/15/2020 by SuperMartijn642
 */
public class CollectorBlock extends BaseBlock {

    private static final List<AxisAlignedBB> SHAPES = Lists.newArrayList(
        new AxisAlignedBB(3 / 16d, 0 / 16d, 3 / 16d, 13 / 16d, 1 / 16d, 13 / 16d),
        new AxisAlignedBB(5 / 16d, 1 / 16d, 5 / 16d, 11 / 16d, 2 / 16d, 11 / 16d),
        new AxisAlignedBB(6 / 16d, 2 / 16d, 6 / 16d, 10 / 16d, 5 / 16d, 10 / 16d),
        new AxisAlignedBB(5 / 16d, 5 / 16d, 5 / 16d, 11 / 16d, 6 / 16d, 11 / 16d),
        new AxisAlignedBB(5 / 16d, 6 / 16d, 5 / 16d, 6 / 16d, 11 / 16d, 6 / 16d),
        new AxisAlignedBB(5 / 16d, 6 / 16d, 10 / 16d, 6 / 16d, 11 / 16d, 11 / 16d),
        new AxisAlignedBB(10 / 16d, 6 / 16d, 5 / 16d, 11 / 16d, 11 / 16d, 6 / 16d),
        new AxisAlignedBB(10 / 16d, 6 / 16d, 10 / 16d, 11 / 16d, 11 / 16d, 11 / 16d),
        new AxisAlignedBB(6 / 16d, 10 / 16d, 5 / 16d, 11 / 16d, 11 / 16d, 6 / 16d),
        new AxisAlignedBB(6 / 16d, 10 / 16d, 10 / 16d, 10 / 16d, 11 / 16d, 11 / 16d),
        new AxisAlignedBB(5 / 16d, 10 / 16d, 6 / 16d, 6 / 16d, 11 / 16d, 10 / 16d),
        new AxisAlignedBB(10 / 16d, 10 / 16d, 6 / 16d, 11 / 16d, 11 / 16d, 10 / 16d),
        new AxisAlignedBB(6 / 16d, 6 / 16d, 6 / 16d, 10 / 16d, 10 / 16d, 10 / 16d)
    );
    private static final AxisAlignedBB SHAPE;

    static{
        AxisAlignedBB shape = SHAPES.get(0);
        for(int i = 1; i < SHAPES.size(); i++)
            shape = shape.union(SHAPES.get(i));
        SHAPE = shape;
    }

    private final Supplier<CollectorTile> tileSupplier;
    private final Supplier<Integer> maxRange;
    private final Supplier<Boolean> hasFilter;

    public CollectorBlock(String registryName, Supplier<CollectorTile> tileSupplier, Supplier<Integer> maxRange, Supplier<Boolean> hasFilter){
        super(registryName, false, Properties.create(Material.ROCK, MapColor.BLACK).harvestTool(ToolType.PICKAXE).harvestLevel(1).hardnessAndResistance(5, 1200));
        this.tileSupplier = tileSupplier;
        this.maxRange = maxRange;
        this.hasFilter = hasFilter;
        this.setCreativeTab(CreativeTabs.SEARCH);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand handIn, EnumFacing facing, float hitX, float hitY, float hitZ){
        if(worldIn.isRemote && !this.hasFilter.get())
            ClientUtils.displayScreen(new BasicCollectorScreen(pos));
        else if(!worldIn.isRemote && this.hasFilter.get())
            player.openGui(ItemCollectors.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState){
        for(AxisAlignedBB box : SHAPES)
            addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
    }

    @Override
    public boolean isFullCube(IBlockState state){
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face){
        return face == EnumFacing.DOWN ? BlockFaceShape.CENTER : BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean hasTileEntity(IBlockState state){
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state){
        return this.tileSupplier.get();
    }

    @Override
    public BlockRenderLayer getBlockLayer(){
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state){
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced){
        tooltip.add(new TextComponentTranslation("itemcollectors." + (this.hasFilter.get() ? "advanced" : "basic") + "_collector.info", this.maxRange.get()).setStyle(new Style().setColor(TextFormatting.AQUA)).getFormattedText());
        super.addInformation(stack, player, tooltip, advanced);
    }
}
