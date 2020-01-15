package rikka.librikka.multiblock;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;

/**
 * 代表了在多方块结构中位于同一位置的两个方块
 *
 * 原类名为BlockMapping
 */
public abstract class Isotope
{
    public final IBlockState state1;
    public final IBlockState state2;

    /**
     * The MultiBlockStructure controller only checks properties from getStateFromMeta, other properties and UnlistedProperties will be ignored
     *
     * @param state1
     * @param state2
     */
    public Isotope(IBlockState state1, IBlockState state2)
    {
        this.state1 = state1;
        this.state2 = state2;
    }

    /**
     * 看看和第一个BlockState是否不同
     * 建议你重写这个类来创建自己的逻辑
     *
     * @param state
     * @return
     */
    protected boolean isDifferent1(IBlockState state)
    {
        return this.state1 != state;
    }

    protected boolean isDifferent2(IBlockState state)
    {
        return state2 != state;
    }

    protected IBlockState getStateForRestore(@Nullable TileEntity tileEntity)
    {
        return state1;
    }
}