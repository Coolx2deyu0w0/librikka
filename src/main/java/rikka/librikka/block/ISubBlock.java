package rikka.librikka.block;

/**
 * 子类型方块
 *
 * 要是一个方块是某个方块的子类型，就要实现此接口
 */
public interface ISubBlock {
    /**
     * Only use for subBlocks
     *
     * @return an array of unlocalized names
     */
    String[] getSubBlockUnlocalizedNames();
}
