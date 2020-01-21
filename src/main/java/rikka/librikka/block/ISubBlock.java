package rikka.librikka.block;

/**
 * 实现这个接口的方块都是有子方块的
 */
public interface ISubBlock
{
    /**
     * 获取所有子方块的名字
     *
     * @return an array of unlocalized names
     */
    String[] getSubBlockUnlocalizedNames();
}
