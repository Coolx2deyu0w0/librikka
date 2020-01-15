package rikka.librikka.multiblock;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.max;

/**
 * 多方块结构
 * Inspired by Immersive Engineering
 * [灵感来源于沉浸工艺]
 *
 * @author Rikka0_0
 */
@Getter
public class MultiBlockStructure
{
    /**
     * NSWE YZX
     * 此处是两个多维数组，分别代表了此结构翻转和不翻转的两种情况。
     * 多维数组的第一层数组代表了结构的朝向：北南西东，剩下的三个维度自然也就是XYZ了
     */
    private final IsotopeWithRPosition[][][][] unmirrored     = new IsotopeWithRPosition[4][][][];
    private final IsotopeWithRPosition[][][][] mirroredAboutZ = new IsotopeWithRPosition[4][][][];
    /*
     * 这里只记录沿Z轴翻转的情况，因为只要有一个翻转就可以配合旋转达到所有的组合可能。但是
     * 目前只支持正放，不支持倒挂。
     */
    private final int                          height;
    /**
     * 结构方块的搜索范围，可能是在这个范围内搜索组成这个结构的方块
     */
    private final int                          searchAreaSize;

    /**
     * @param configuration 包含这个结构中所有的方块，顺序为YZX
     */
    public MultiBlockStructure(Isotope[][][] configuration)
    {
        height = configuration.length;

        //Find the bounding box[搜索边界盒]
        // 搜索的算法大概就是遍历整个三维数组，获取其最大的值。从这里可以看出三维数组并不是齐次的
        int zSize = 0, xSize = 0;
        for (int y = 0; y < this.height; y++) {
            Isotope[][] zxc = configuration[y];
            for (Isotope[] xc : zxc) {
                if (xc.length > xSize)
                    xSize = xc.length;
            }

            if (zxc.length > zSize)
                zSize = zxc.length;
        }
        searchAreaSize = max(xSize, zSize);

        // 根据不同的角度创建数组存储方块信息
        this.unmirrored[0] = new IsotopeWithRPosition[height][zSize][xSize];    //North, Unmirrored
        this.unmirrored[3] = new IsotopeWithRPosition[height][xSize][zSize];    //East, Unmirrored
        this.unmirrored[1] = new IsotopeWithRPosition[height][zSize][xSize];    //South, Unmirrored
        this.unmirrored[2] = new IsotopeWithRPosition[height][xSize][zSize];    //West, Unmirrored
        this.mirroredAboutZ[0] = new IsotopeWithRPosition[height][zSize][xSize];    //North
        this.mirroredAboutZ[3] = new IsotopeWithRPosition[height][xSize][zSize];    //East
        this.mirroredAboutZ[1] = new IsotopeWithRPosition[height][zSize][xSize];    //South
        this.mirroredAboutZ[2] = new IsotopeWithRPosition[height][xSize][zSize];    //West
        // 遍历整个configuration空间来填充上面那些数组
        for (int y = 0; y < this.height; y++) {
            for (int z = 0; z < configuration[y].length; z++) {
                for (int x = 0; x < configuration[y][z].length; x++) {
                    Isotope              isotope              = configuration[y][z][x];
                    IsotopeWithRPosition isotopeWithRPosition = null;

                    if (isotope != null) {
                        isotopeWithRPosition = new IsotopeWithRPosition(x, y, z, isotope);
                    }

                    unmirrored[0][y][z][x] = isotopeWithRPosition;                    //North
                    unmirrored[3][y][x][zSize - 1 - z] = isotopeWithRPosition;            //East, newX = zSize-1 - oldZ, newZ = oldX
                    unmirrored[1][y][zSize - 1 - z][xSize - 1 - x] = isotopeWithRPosition;    //South, newX = xSize-1 -newX, newZ = zSize-1 - newZ
                    unmirrored[2][y][xSize - 1 - x][z] = isotopeWithRPosition;            //West, newX = oldZ, newZ = xSize-1 - oldX;

                    mirroredAboutZ[0][y][z][xSize - 1 - x] = isotopeWithRPosition;
                }
            }
        }

        for (int y = 0; y < this.height; y++) {
            for (int z = 0; z < zSize; z++) {
                for (int x = 0; x < xSize; x++) {
                    IsotopeWithRPosition isotopeWithRPosition = this.mirroredAboutZ[0][y][z][x];

                    mirroredAboutZ[3][y][x][zSize - 1 - z] = isotopeWithRPosition;            //East, newX = zSize-1 - oldZ, newZ = oldX
                    mirroredAboutZ[1][y][zSize - 1 - z][xSize - 1 - x] = isotopeWithRPosition;    //South, newX = xSize-1 -newX, newZ = zSize-1 - newZ
                    mirroredAboutZ[2][y][xSize - 1 - x][z] = isotopeWithRPosition;            //West, newX = oldZ, newZ = xSize-1 - oldX;
                }
            }
        }
    }

    /**
     * 在一定的空间内搜索
     *
     * @param area            空间
     * @param structureState1 指定结构
     * @return 返回结构坐标，null即为未找到
     */
    private int[] check(IBlockState[][][] area, IsotopeWithRPosition[][][] structureState1)
    {
        for (int zOrigin = 0; zOrigin < this.searchAreaSize; zOrigin++) {
            for (int xOrigin = 0; xOrigin < this.searchAreaSize; xOrigin++) {
                for (int yOrigin = 0; yOrigin < this.height; yOrigin++) {
                    if (this.check(area, structureState1, xOrigin, yOrigin, zOrigin)) {
                        return new int[]{xOrigin, yOrigin, zOrigin};
                    }
                }
            }
        }

        return null;
    }

    private boolean check(IBlockState[][][] states, IsotopeWithRPosition[][][] configuration, int xOrigin, int yOrigin, int zOrigin)
    {
        for (int y = 0; y < this.height; y++) {
            for (int z = 0; z < configuration[y].length; z++) {
                for (int x = 0; x < configuration[y][z].length; x++) {
                    IsotopeWithRPosition config = configuration[y][z][x];
                    if (config != null && config.getComparator().isDifferent1(states[xOrigin + x][yOrigin + y][zOrigin + z]))
                        return false;
                }
            }
        }

        return true;
    }

    private IBlockState[][][] searchState1Structure(World world, BlockPos startPos, int originPosX, int originPosY, int originPosZ)
    {
        IBlockState[][][] states = new IBlockState[searchAreaSize * 2 - 1][height * 2 - 1][searchAreaSize * 2 - 1];

        for (int x = originPosX, i = 0; x < startPos.getX() + searchAreaSize; x++, i++) {
            for (int y = originPosY, j = 0; y < startPos.getY() + height; y++, j++) {
                for (int z = originPosZ, k = 0; z < startPos.getZ() + searchAreaSize; z++, k++) {
                    states[i][j][k] = world.getBlockState(new BlockPos(x, y, z));

                    if (states[i][j][k] == Blocks.AIR)
                        states[i][j][k] = null;
                }
            }
        }

        return states;
    }

    /**
     * 尝试转换
     * <p>
     * 尝试将玩家建造的state1堆转换为state2堆的结构。方法不可以指定建造的坐标信息是因
     * 为必须在已经有state1堆的情况下才能转换，也就是说，在转换前坐标就已经被确定了。
     * 不可以指定旋转角度也是这个原因。如果state1堆是一个中心对称（或是左右对称）结构，
     * 那具体的朝向就没有关系了。
     *
     * @param world    世界
     * @param startPos 起始坐标
     * @return 如果可以建造则返回StructureConverter实例，不可以返回null
     */
    public StructureConverter attempToConverter(World world, BlockPos startPos)
    {
        // 搜索空间的起点（向start的西北方扩展）
        int xStart  = startPos.getX(), yStart = startPos.getY(), zStart = startPos.getZ();
        int originX = xStart - searchAreaSize + 1, originY = yStart - height + 1, originZ = zStart - searchAreaSize + 1;

        IBlockState[][][] states = this.searchState1Structure(world, startPos, originX, originY, originZ);

        //Check unmirrored
        for (int dir = 0; dir < 4; dir++) {
            int[] offset = this.check(states, this.unmirrored[dir]);
            if (offset != null)
                return new StructureConverter(this, StructureFacing.getByIndex(dir), false, world, originX + offset[0], originY + offset[1], originZ + offset[2]);
        }

        //Check mirrored
        for (int dir = 0; dir < 4; dir++) {
            int[] offset = this.check(states, this.mirroredAboutZ[dir]);
            if (offset != null)
                return new StructureConverter(this, StructureFacing.getByIndex(dir), true, world, originX + offset[0], originY + offset[1], originZ + offset[2]);
        }

        return null;
    }

    public StructureConverter attempToConverter(World world, BlockPos startPos, boolean mirrored, StructureFacing facing)
    {
        int xStart  = startPos.getX(), yStart = startPos.getY(), zStart = startPos.getZ();
        int originX = xStart - searchAreaSize + 1, originY = yStart - height + 1, originZ = zStart - searchAreaSize + 1;

        IBlockState[][][] states = this.searchState1Structure(world, startPos, originX, originY, originZ);

        int[] offset;
        if (mirrored) {
            offset = this.check(states, this.mirroredAboutZ[facing.getIndex()]);
        } else {
            offset = this.check(states, this.unmirrored[facing.getIndex()]);
        }
        if (offset != null) {
            return new StructureConverter(this, facing, mirrored, world, originX + offset[0], originY + offset[1], originZ + offset[2]);
        } else return null;
    }

    /**
     * 重建这个结构
     * <p>
     * 在秦始皇mod中是用在秦始皇被破坏一个方块后用来将整个结构恢复成没被雕刻的样子
     *
     * @param te                          被破坏的方块的TileEntity
     * @param stateJustRemoved            被破坏的方块
     * @param dropConstructionBlockAsItem 是否在世界中扔一个指定位置的物品
     */
    public void restoreStructure(TileEntity te, IBlockState stateJustRemoved, boolean dropConstructionBlockAsItem)
    {
        if (!(te instanceof IMultiBlockTile)) {
            return;
        }

        MultiBlockTileInfo mbInfo = ((IMultiBlockTile) te).getMultiBlockTileInfo();
        if (!mbInfo.formed)
            return;    //Avoid circulation, better performance!

        if (dropConstructionBlockAsItem) {
            IBlockState stateToDrop = this.getConstructionBlock(mbInfo);
            //System.out.println("drop!!!!!!!!!!!!!!!");
            stateToDrop.getBlock().dropBlockAsItem(te.getWorld(), te.getPos(), stateToDrop, 0);
        }

        Set<IMultiBlockTile> removedTile = new HashSet<>();

        World world = te.getWorld();

        int     facing   = mbInfo.structureFacing.getIndex();
        boolean mirrored = mbInfo.mirrored;
        //YZX
        IsotopeWithRPosition[][][] configuration = mirrored ? mirroredAboutZ[facing] : unmirrored[facing];
        BlockPos                   originActual  = mbInfo.origin;

        int zSize = configuration[0].length;
        int xSize = configuration[0][0].length;

        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < zSize; j++) {
                for (int k = 0; k < xSize; k++) {
                    IsotopeWithRPosition isotopeWithRPosition = configuration[i][j][k];

                    if (isotopeWithRPosition != null) {
                        //Traverse the structure
                        int[] offset = StructureFacing.offsetFromOrigin(StructureFacing.getByIndex(facing), mirrored, isotopeWithRPosition);

                        IBlockState theState;

                        BlockPos pos = originActual.add(offset[0], offset[1], offset[2]);

                        if (pos == te.getPos()) {
                            theState = stateJustRemoved;
                        } else {
                            theState = world.getBlockState(pos);

                            if (theState.getBlock() != Blocks.AIR && !isotopeWithRPosition.getComparator().isDifferent2(theState)) {
                                TileEntity te2 = world.getTileEntity(pos);

                                if (te2 instanceof IMultiBlockTile) {
                                    ((IMultiBlockTile) te2).getMultiBlockTileInfo().formed = false;
                                    removedTile.add((IMultiBlockTile) te2);
                                }

                                //Play Destroy Effect
                                world.playEvent(2001, pos, Block.getStateId(theState));
                                world.setBlockState(pos, isotopeWithRPosition.getComparator().getStateForRestore(te2));
                            }
                        }
                    }
                }
            }
        }

        removedTile.add((IMultiBlockTile) te);

        for (IMultiBlockTile tile : removedTile) {
            tile.onStructureRemoved();
        }
    }

    /**
     * 从未翻转而且面朝北的结构中的指定坐标中获取BlockInfo
     * <p>
     * 可以看到这个方法无法直接从世界中的绝对坐标获取
     *
     * @param xOffset 结构定义中的三个坐标
     * @param yOffset
     * @param zOffset
     * @return
     */
    public IsotopeWithRPosition getBlockInfo(int xOffset, int yOffset, int zOffset)
    {
        return unmirrored[0][yOffset][zOffset][xOffset];
    }

    public IBlockState getConstructionBlock(MultiBlockTileInfo mbInfo)
    {
        IsotopeWithRPosition info = this.getBlockInfo(mbInfo.xOffset, mbInfo.yOffset, mbInfo.zOffset);
        return info == null ? null : info.getBefore();
    }

}
