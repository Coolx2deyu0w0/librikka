package rikka.librikka.multiblock;

import lombok.Getter;
import net.minecraft.block.state.IBlockState;

/**
 * 带相对位置位置的同位素
 *
 * 原类名为BlockInfo
 */
@Getter
public class IsotopeWithRPosition
{
    private final IBlockState before;
    private final IBlockState after;
    /**
     * 结构定义中的相对位置
     */
    private final int         x, y, z;

    private final Isotope comparator;

    IsotopeWithRPosition(int x, int y, int z, Isotope comparator)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.before = comparator.state1;
        this.after = comparator.state2;
        this.comparator = comparator;
    }
}
