package rikka.librikka.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.item.ItemBlockBase;

import java.lang.reflect.Constructor;

/**
 * 一个Block的简单实现
 */
public abstract class BlockBase extends Block
{
    /**
     * 记录这个方块的方块物品
     */
    public final ItemBlockBase itemBlock;

    /**
     * 初始化这个方块。在初始化的过程中设置非本地化名称，质地和ItemBlock
     *
     * @param unlocalizedName 非本地化名称
     * @param material        方块的质地。一个表现就是玩家走在这个方块上时发出的声音
     * @param itemBlockClass  此方块对应的方块物品的类
     */
    public BlockBase(String unlocalizedName, Material material, Class<? extends ItemBlockBase> itemBlockClass)
    {
        super(material);
        super.setUnlocalizedName(unlocalizedName);
        super.setRegistryName(unlocalizedName);                //Key!
        super.setDefaultState(this.getBaseState(this.blockState.getBaseState()));

        try {
            Constructor constructor = itemBlockClass.getConstructor(Block.class);
            itemBlock = (ItemBlockBase) constructor.newInstance(this);
        } catch (Exception e) {
            throw new RuntimeException("Invalid ItemBlock constructor!");
        }
    }

    /**
     * 获取这个方块的子方块
     *
     * @param tab      鬼知道这里为什么要传入一个创造模式物品栏
     * @param subItems 将获取的子类型填进这个列表
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (this.itemBlock.getHasSubtypes()) {
            for (int ix = 0; ix < ((ISubBlock) this).getSubBlockUnlocalizedNames().length; ix++)
                subItems.add(new ItemStack(this, 1, ix));
        } else {
            subItems.add(new ItemStack(this));
        }
    }

    /**
     * 此方块是否被叶子替换。我能想到的例子就是树木生长时用树叶方块替换空气方块这样子
     *
     * @param state
     * @param world
     * @param pos
     * @return
     */
    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    //BlockState --------------------------------------------------------------------
    //createBlockState, setDefaultBlockState

    /**
     * 重写此方法来修改在此类初始化过程中设置的默认BlockState
     *
     * @param firstValidState
     * @return the base state
     */
    protected IBlockState getBaseState(IBlockState firstValidState)
    {
        return firstValidState;
    }
}