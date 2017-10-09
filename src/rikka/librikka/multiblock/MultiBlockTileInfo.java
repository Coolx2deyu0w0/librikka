package rikka.librikka.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import rikka.librikka.Utils;

/**
 * Additional multiblock information that stored in tileEntities 
 * @author Administrator
 *
 */
public class MultiBlockTileInfo {
    public final EnumFacing facing;
    public final boolean mirrored;
    public final int xOffset, yOffset, zOffset;	/**From config origin, before any transformation*/
    public final BlockPos origin;
    protected boolean formed;

    /**
     * Structure creation
     *
     * @param facing
     * @param mirrored
     * @param xOffset
     * @param yOffset
     * @param zOffset
     * @param xOrigin
     * @param yOrigin
     * @param zOrigin
     */
    public MultiBlockTileInfo(EnumFacing facing, boolean mirrored, int xOffset, int yOffset, int zOffset, int xOrigin, int yOrigin, int zOrigin) {
        this.facing = facing;
        this.mirrored = mirrored;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.origin = new BlockPos(xOrigin, yOrigin, zOrigin);
        this.formed = true;
    }

    public MultiBlockTileInfo(NBTTagCompound nbt) {
    	this.facing = Utils.facingFromNbt(nbt, "facing");
    	this.mirrored = nbt.getBoolean("mirrored");
    	this.xOffset = nbt.getInteger("xOffset");
    	this.yOffset = nbt.getInteger("yOffset");
    	this.zOffset = nbt.getInteger("zOffset");
        this.origin = Utils.posFromNbt(nbt, "origin");
        this.formed = nbt.getBoolean("formed");
    }

    public void saveToNBT(NBTTagCompound nbt) {
        Utils.saveToNbt(nbt, "facing", this.facing);
        nbt.setBoolean("mirrored", this.mirrored);
        nbt.setInteger("xOffset", this.xOffset);
        nbt.setInteger("yOffset", this.yOffset);
        nbt.setInteger("zOffset", this.zOffset);
        Utils.saveToNbt(nbt, "origin", this.origin);
        nbt.setBoolean("formed", this.formed);
    }

    public BlockPos getPartPos(Vec3i offsetPos) {
        int[] offset = MultiBlockStructure.offsetFromOrigin(getFacing(), this.mirrored,
                offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
        return this.origin.add(offset[0], offset[1], offset[2]);
    }
    
    public int getFacing() {
    	return this.facing.ordinal() - 2;
    }
    
    /**
     * @param array y,z,x facing NORTH(Z-)
     * @return
     */
    public <T> T lookup(T[][][] array) {
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
    
    public static <T> T lookup(IMultiBlockTile mbTile, T[][][] array) {
    	if (mbTile == null)
    		return null;
    	
    	MultiBlockTileInfo mbInfo = mbTile.getMultiBlockTileInfo();
    	if (mbInfo == null)
    		return null;
    	
    	return mbInfo.lookup(array);
    }
}
