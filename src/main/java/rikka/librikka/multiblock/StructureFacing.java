package rikka.librikka.multiblock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.EnumFacing;

/**
 * 多方块结构只定义四种朝向，主要是横着放的结构太复杂而且没必要
 */
@AllArgsConstructor
public enum StructureFacing
{
    NORTH(0),
    SOUTH(1),
    WEST(2),
    EAST(3);

    @Getter
    private int index;

    public static StructureFacing getByIndex(int index)
    {
        switch (index) {
            case 0:
                return NORTH;
            case 1:
                return SOUTH;
            case 2:
                return WEST;
            case 3:
                return EAST;
        }
        return NORTH;
    }

    public static StructureFacing translate(EnumFacing facing)
    {
        int index = facing.getIndex();
        switch (index) {
            case 2:
                return NORTH;
            case 3:
                return SOUTH;
            case 4:
                return WEST;
            case 5:
                return EAST;
        }
        return NORTH;
    }

    public EnumFacing translate()
    {
        switch (this.index) {
            default:
            case 0:
                return EnumFacing.NORTH;
            case 1:
                return EnumFacing.SOUTH;
            case 2:
                return EnumFacing.WEST;
            case 3:
                return EnumFacing.EAST;
        }
    }

    /**
     * 对方块的相对坐标做一下变换
     */
    public static int[] offsetFromOrigin(StructureFacing facing, boolean mirrored, int x, int y, int z)
    {
        if (mirrored)
            x = -x;

        switch (facing) {
            default:
            case NORTH: { //North
                return new int[]{x, y, z};
            }
            case EAST: { //East, newX = zSize-1 - oldZ, newZ = oldX
                return new int[]{-z, y, x};
            }
            case SOUTH: { //South, newX = xSize-1 -newX, newZ = zSize-1 - newZ
                return new int[]{-x, y, -z};
            }
            case WEST: { //West, newX = oldZ, newZ = xSize-1 - oldX;
                return new int[]{z, y, -x};
            }
        }
    }

    public static int[] offsetFromOrigin(StructureFacing facing, boolean mirrored, IsotopeWithRPosition isotope)
    {
        return StructureFacing.offsetFromOrigin(facing, mirrored, isotope.getX(), isotope.getY(), isotope.getZ());
    }
}
