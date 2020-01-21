package rikka.librikka.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 一个Item的简单实现
 */
public abstract class ItemBase extends Item
{
    /**
     * @param name        命名规则：只能用小写英文字母+数字，单词之间使用“_”相连。例如：“cooked_beef”
     * @param hasSubItems 是否有子类型的物品。所谓的子类型并非不同颜色的羊毛，而是不同损坏值的同一件物品。
     *                    比如钻石剑就有一千多个子类型物品。
     */
    public ItemBase(String name, boolean hasSubItems) {
        super();
        super.setUnlocalizedName(name);    //UnlocalizedName = "item." + name
        super.setRegistryName(name);
        super.setHasSubtypes(hasSubItems); // 设置是否有子类型

        if (hasSubItems) {
            this.setMaxDamage(0);    // 意思就是没有子类型的物品是没有损坏值的
        }
    }

    /**
     * 获取非本地化名称
     * <p>
     * 接受一个ItemStack的原因是要根据物品堆的不同情况来返回不同的名称。如满血钻石剑被翻译为“钻石剑”，损坏的钻石剑被翻译为“破损的钻石剑”
     *
     * @param itemstack
     * @return
     */
    @Override
    public final String getUnlocalizedName(ItemStack itemstack) {
        if (super.getHasSubtypes()) {
            return super.getUnlocalizedName() + "." + this.getSubItemUnlocalizedNames()[itemstack.getItemDamage()];
        } else {
            return super.getUnlocalizedName();
        }
    }

    /**
     * 获取此物品的子物品列表
     */
    @Override
    @SideOnly(Side.CLIENT)
    public final void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        // 如果不是在指定的创造模式物品栏则不向subItems中添加任何的元素
        if (!this.isInCreativeTab(tab)) {
            return;
        }

        if (getHasSubtypes()) {
            for (int ix = 0; ix < this.getSubItemUnlocalizedNames().length; ix++) {
                subItems.add(new ItemStack(this, 1, ix));
            }
        } else {
            subItems.add(new ItemStack(this));
        }
    }

    @Override
    public final String getUnlocalizedNameInefficiently(ItemStack stack) {
        String prevName = super.getUnlocalizedNameInefficiently(stack);
        String domain   = this.getRegistryName().getResourceDomain();
        return "item." + domain + ":" + prevName.substring(5);
    }

    /**
     * 如果一个物品具备子类型，则必须事先这个方法来返回这个物品的所有子类型的非本地化名称
     *
     * @return 一个包含所有子类型非本地话名称的数组，数组的长度就代表了拥有子类型的数量
     */
    public String[] getSubItemUnlocalizedNames() {
        return null;
    }
}
