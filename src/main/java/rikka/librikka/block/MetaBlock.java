package rikka.librikka.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import rikka.librikka.item.ItemBlockBase;
import rikka.librikka.properties.PropertyMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * MetaBlock（拥有元数据的方块）
 * <p>
 * 什么是元数据？大概就是老版本中的“子ID”吧。当一个方块具备子类型时就使用元数据来
 * 区分各个不同的自类型方块（比如不同颜色的羊毛和玻璃）
 * <p>
 * 从窜染的角度来说不同颜色的羊毛并非不同方块，只不过是同样的羊毛来渲染成不同的颜色而已。
 */
public abstract class MetaBlock extends BlockBase implements ISubBlock
{
    /**
     * 记录所有拥有元数据的方块的元数据数量
     * <p>
     * Key：  方块的非本地化名称
     * Value：此方块的元数据数量上限
     */
    private static final Map<String, Integer> META_UPPER_BOUNDS = new HashMap<>();
    /**
     * 元数据数量的上限
     */
    private static       int                  metaUpperBound;

    /**
     * 元数据
     */
    public final  IProperty<Integer> propertyMeta;
    private final String[]           subNames;

    /**
     * @param unlocalizedName 非本地化名称
     * @param subNames        此方块包含的子方块种类
     * @param material        质地
     * @param itemBlockClass  此方块对应的ItemBlock。按理说如果是父方块的话应该不能指定ItemBlock，
     *                        但是这里却有...可能是方块物品只是外观上的区别，这个类中对元数据进行了判
     *                        断来使其有正确的行为
     */
    public MetaBlock(String unlocalizedName, String[] subNames, Material material, Class<? extends ItemBlockBase> itemBlockClass) {
        super(RegisterMetaUpperBound(unlocalizedName, subNames), material, itemBlockClass);

        if (metaUpperBound != META_UPPER_BOUNDS.get(unlocalizedName)) {
            throw new RuntimeException("Parameter Corrupted!");
        }

        this.subNames = new String[subNames.length];
        for (int i = 0; i < subNames.length; i++) {
            this.subNames[i] = subNames[i];
        }

        this.propertyMeta = (IProperty<Integer>) getBlockState().getProperty("meta");
        super.setDefaultState(this.getDefaultState(blockState.getBaseState()));
    }

    private static String RegisterMetaUpperBound(String unlocalizedName, String[] subNames) {
        metaUpperBound = subNames.length - 1;
        META_UPPER_BOUNDS.put(unlocalizedName, metaUpperBound);
        return unlocalizedName;
    }

    @Override
    public final String[] getSubBlockUnlocalizedNames() {
        return this.subNames;
    }

    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
    protected final BlockStateContainer createBlockState() {
        ArrayList<IProperty> properties = new ArrayList<>();
        // 未列出的属性
        ArrayList<IUnlistedProperty> unlisted = new ArrayList<>();
        // 创建属性与未列出的属性列表
        this.createProperties(properties, unlisted);
        IProperty[]         propertyArray = properties.toArray(new IProperty[0]);
        IUnlistedProperty[] unlistedArray = unlisted.toArray(new IUnlistedProperty[0]);

        if (unlisted.isEmpty()) {
            return new BlockStateContainer(this, propertyArray);
        } else {
            // ExtendedBlockState是forge提供的一个工具类，提供了支持额外属性的BlockState
            return new ExtendedBlockState(this, propertyArray, unlistedArray);
        }
    }

    /**
     * 重写这个方法来添加更多的属性
     *
     * @param properties 属性列表
     * @param unlisted   未列出的属性列表
     */
    protected void createProperties(ArrayList<IProperty> properties, ArrayList<IUnlistedProperty> unlisted) {
        properties.add(new PropertyMeta("meta", metaUpperBound + 1));
    }

    /**
     * 在初始化完成前，propertyMeta的值为null
     *
     * @return @NonNullable propertyMeta
     */
    public final IProperty<Integer> getPropertyMeta() {
        if (this.propertyMeta == null) {
            return (IProperty<Integer>) blockState.getProperty("meta");
        }
        return this.propertyMeta;
    }

    private IBlockState getDefaultState(IBlockState baseState) {
        return baseState.withProperty(this.propertyMeta, 0);
    }

    /**
     * 在初始化过程完成前propertyMeta不能用
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(this.getPropertyMeta(), meta & 15);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(this.getPropertyMeta());
        meta = meta & 15;
        return meta;
    }

    /**
     * 在方块被破坏时调用根据BlockState获得元数据
     * <p>
     * 详细说一下：当一个方块被破坏的时候要扔下相对的物品，如果方块具有子方块则需要能扔下
     * 具有正确元数据的物品，就用这个方法进行判断
     *
     * @param state 被破坏的方块
     * @return 元数据值
     */
    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(this.propertyMeta);
    }
}