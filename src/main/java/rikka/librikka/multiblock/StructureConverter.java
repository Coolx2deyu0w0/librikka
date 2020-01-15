package rikka.librikka.multiblock;

import lombok.Getter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * 结构转换器
 * <p>
 * 用一个例子说明会很简单：秦始皇mod中需要先将石头堆成长条才能雕刻，这个类进行的转换就是将
 * 石碓变成雕像。
 */
@Getter
public class StructureConverter
{
    private final MultiBlockStructure structure;
    /**
     * facing
     */
    private final StructureFacing     facing;
    private final boolean             mirrored;

    /**
     * 结构的空间布局
     *
     * 可以看做一个三维的空间布局，多维数组顺序为[Y][Z][X]。这个空间是已经经过
     * 旋转和翻转处理的。
     */
    private final IsotopeWithRPosition[][][] configuration;

    /**
     * Origin of blockInfo, Actual location in the world
     */
    private final int originX, originY, originZ;

    /**
     * 空间布局的俯视图尺寸
     */
    private final int zSize, xSize;

    private final World world;
    /**
     * 此结构起始点在世界中的绝对坐标
     */
    private final int   absolutePositionX, absolutePositionY, absolutePositionZ;

    StructureConverter(MultiBlockStructure structure, StructureFacing facing, boolean mirrored, World world, int originPosX, int originPosY, int originPosZ)
    {
        this.structure = structure;
        this.facing = facing;
        this.mirrored = mirrored;
        this.world = world;
        this.originX = originPosX;
        this.originY = originPosY;
        this.originZ = originPosZ;


        // 根据旋转和翻转进行调整
        if (mirrored) {
            configuration = structure.getMirroredAboutZ()[this.facing.getIndex()];
        } else {
            configuration = structure.getUnmirrored()[this.facing.getIndex()];
        }

        zSize = configuration[0].length;
        xSize = configuration[0][0].length;

        int xOriginActual = this.originX;
        int yOriginActual = this.originY;
        int zOriginActual = this.originZ;


        switch (facing.getIndex()) {
            case 0:    //North
                if (mirrored)
                    xOriginActual = xOriginActual + xSize - 1;
                break;
            case 3: //East, newX = zSize-1 - oldZ, newZ = oldX
                xOriginActual += xSize - 1;
                if (mirrored)
                    zOriginActual += zSize - 1;
                break;
            case 1:    //South, newX = xSize-1 -newX, newZ = zSize-1 - newZ
                if (mirrored) {
                    zOriginActual += zSize - 1;
                } else {
                    xOriginActual += xSize - 1;
                    zOriginActual += zSize - 1;
                }
                break;
            case 2: //West, newX = oldZ, newZ = xSize-1 - oldX;
                if (!mirrored)
                    zOriginActual += zSize - 1;
                break;
            default:
                xOriginActual = -1;
                yOriginActual = -1;
                zOriginActual = -1;
                break;
        }

        this.absolutePositionX = xOriginActual;
        this.absolutePositionY = yOriginActual;
        this.absolutePositionZ = zOriginActual;
    }

    /**
     * 使用此转换器将结构中的方块1转换成为方块2来形成新的结构（外观）
     *
     * 基本原理就是遍历整个configuration空间，要是不是null就摆放state2并记录其TileEntity
     */
    public void convertStructure()
    {
//        Set<IMultiBlockTile> createdTile = new HashSet<>();
        List<IMultiBlockTile> createdTile = new ArrayList<>();

        for (int i = 0; i < this.structure.getHeight(); i++) {
            for (int j = 0; j < this.zSize; j++) {
                for (int k = 0; k < this.xSize; k++) {

                    IsotopeWithRPosition isotopeWithRPosition = this.configuration[i][j][k];
                    if (isotopeWithRPosition != null) {

                        // 进行坐标转换并且在世界上的指定位置摆放方块（State2）
                        int[] offset = StructureFacing.offsetFromOrigin(this.facing, this.mirrored, isotopeWithRPosition);
                        BlockPos pos = new BlockPos(this.absolutePositionX + offset[0], this.absolutePositionY + offset[1], this.absolutePositionZ + offset[2]);
                        this.world.setBlockState(pos, isotopeWithRPosition.getAfter());

                        // 移除已经存在的TileEntity
//                        world.removeTileEntity(pos); // 注释掉的原因是设置新的TileEntity隐含着移除旧的

                        TileEntity tileEntity = this.world.getTileEntity(pos);

                        // 记录这些方块的TileEntity
                        if (tileEntity instanceof IMultiBlockTile) {
                            MultiBlockTileInfo mbInfo = new MultiBlockTileInfo(
                                    this.facing, this.mirrored, isotopeWithRPosition.getX(), isotopeWithRPosition.getY(), isotopeWithRPosition.getZ(), this.absolutePositionX, this.absolutePositionY, this.absolutePositionZ
                            );
                            ((IMultiBlockTile) tileEntity).onStructureCreating(mbInfo);
                            createdTile.add((IMultiBlockTile) tileEntity);
                        }
                    }

                }
            }
        }

        for (IMultiBlockTile tile : createdTile)
            tile.onStructureCreated();
    }
}
