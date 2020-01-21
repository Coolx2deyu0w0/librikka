package rikka.librikka.item;

import jdk.nashorn.internal.ir.Block;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 可变纹理
 * <p>
 * 称之为“可变”的原因是使用它获得的材质是可以随着物品损耗值变化的
 */
@SideOnly(Side.CLIENT)
public interface IVariableTexture
{
    /**
     * 根据损耗值来获取Icon的名字
     *
     * @param damage 物品的损耗值
     */
    String getIconName(int damage);
}
