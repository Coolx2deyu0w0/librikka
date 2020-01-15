package rikka.librikka.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import rikka.librikka.Utils;

/**
 * 在IMultiBlockTile中储存的附加信息
 *
 * @author Administrator
 */
public class MultiBlockTileInfo
{
    // 将EnumFacing修改为StructureFacing
    public final StructureFacing structureFacing;
    public final boolean         mirrored;

    /**
     * the coordinate in the structure description (before rotation and mirror)
     */
    public final int xOffset, yOffset, zOffset;
    public final BlockPos origin;
    protected    boolean  formed;

    public MultiBlockTileInfo(StructureFacing structureFacing, boolean mirrored, IsotopeWithRPosition isotope, StructureConverter converter)
    {
        this(
                structureFacing,
                mirrored,
                isotope.getX(),
                isotope.getY(),
                isotope.getZ(),
                converter.getOriginX(),
                converter.getOriginY(),
                converter.getOriginZ()
        );
    }

    public MultiBlockTileInfo(StructureFacing structureFacing, boolean mirrored, int xOffset, int yOffset, int zOffset, int xOrigin, int yOrigin, int zOrigin)
    {
        this.structureFacing = structureFacing;
        this.mirrored = mirrored;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.origin = new BlockPos(xOrigin, yOrigin, zOrigin);
        this.formed = true;
    }

    // StructureFacing类只在程序运行中使用，储存还是用Minecraft自带的EnumFacing
    public MultiBlockTileInfo(NBTTagCompound nbt)
    {
        this.structureFacing = Utils.structureFacingFromNbt(nbt, "facing");
        this.mirrored = nbt.getBoolean("mirrored");
        this.xOffset = nbt.getInteger("xOffset");
        this.yOffset = nbt.getInteger("yOffset");
        this.zOffset = nbt.getInteger("zOffset");
        this.origin = Utils.posFromNbt(nbt, "origin");
        this.formed = nbt.getBoolean("formed");
    }

    public void saveToNBT(NBTTagCompound nbt)
    {
        Utils.saveToNbt(nbt, "facing", this.structureFacing.translate());
        nbt.setBoolean("mirrored", this.mirrored);
        nbt.setInteger("xOffset", this.xOffset);
        nbt.setInteger("yOffset", this.yOffset);
        nbt.setInteger("zOffset", this.zOffset);
        Utils.saveToNbt(nbt, "origin", this.origin);
        nbt.setBoolean("formed", this.formed);
    }

    public boolean isPart(Vec3i partPos)
    {
        return xOffset == partPos.getX() && yOffset == partPos.getY() && zOffset == partPos.getZ();
    }

    /**
     * @param offsetPos the coordinate in the structure description (before rotation and mirror)
     * @return the actual BlockPos
     */
    public BlockPos getPartPos(Vec3i offsetPos)
    {
        int[] offset = StructureFacing.offsetFromOrigin(structureFacing, this.mirrored, offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
        return this.origin.add(offset[0], offset[1], offset[2]);
    }

    /**
     * @param array y,z,x facing NORTH(Z-)
     * @return
     */
    public <T> T lookup(T[][][] array)
    {
        if (this.yOffset >= array.length)
            return null;

        T[][] renderInfoZX = array[this.yOffset];
        if (this.zOffset >= renderInfoZX.length)
            return null;

        T[] renderInfoX = renderInfoZX[this.zOffset];
        if (this.xOffset >= renderInfoX.length)
            return null;

        return renderInfoX[this.xOffset];
    }

    public static <T> T lookup(IMultiBlockTile mbTile, T[][][] array)
    {
        if (mbTile == null)
            return null;

        MultiBlockTileInfo mbInfo = mbTile.getMultiBlockTileInfo();
        if (mbInfo == null)
            return null;

        return mbInfo.lookup(array);
    }
}
