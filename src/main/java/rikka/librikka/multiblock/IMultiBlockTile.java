package rikka.librikka.multiblock;

/**
 * 是一个方块的TileEntity
 */
public interface IMultiBlockTile {

    MultiBlockTileInfo getMultiBlockTileInfo();

    /**
     * 当结构被正在创建时调用此方法，此结构包含的所有方块的TileEntity都将收到这个信息
     *
     * @param mbInfo
     */
    void onStructureCreating(MultiBlockTileInfo mbInfo);

    /**
     * 当结构被正在创建时调用此方法，此结构包含的所有方块的TileEntity都将收到这个信息
     */
    void onStructureCreated();

    /**
     * 当结构被破坏时调用（玩家破坏/爆炸/其他）
     */
    void onStructureRemoved();
}
