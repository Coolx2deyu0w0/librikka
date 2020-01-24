package rikka.librikka.model.loader;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.block.BlockBase;
import rikka.librikka.item.IVariableTexture;

import java.util.Objects;

/**
 * 使用此类来注册物品（和方块）在Inventory界面中显示的图标
 * <p>
 * 逻辑原本存在于{@link AdvancedModelLoader}，
 * 为了更清晰单独移动到这里。
 *
 * @author Rikka0_0
 */
@SideOnly(Side.CLIENT)
public class ItemIconRegistry
{
    /**
     * 注册方块在Inventory界面中的图标
     * <p>
     * 在方法中被注册的实际上是方块对应的“方块物品”的图标。在注册时会优先获取方块本身的可变纹理，
     * 若方块不具备则会从方块对应的物品中获取可变纹理。
     *
     * @param block 方块
     * @throws IllegalArgumentException 当一个方块和它对应的方块物品都没有实现IVariableTexture接口时
     */
    public static void registerInventoryIcon(String namespace, String path, BlockBase block) {

        if (block instanceof IVariableTexture) {
            IVariableTexture iVariableTexture = (IVariableTexture) block;
            RegisterInventoryIcon0(namespace, path, iVariableTexture, block.itemBlock);

        } else if (block.itemBlock instanceof IVariableTexture) {
            IVariableTexture iVariableTexture = (IVariableTexture) block.itemBlock;
            RegisterInventoryIcon0(namespace, path, iVariableTexture, block.itemBlock);
        } else
            throw new IllegalArgumentException("Either the Block class or its coresponding ItemBlock class should implement the IVariableTexture interface");
    }

    /**
     * 注册物品（Item）在Inventory界面中的图标，若要注册方块物品的话直接传入方块就好了
     *
     * @param item 需要特别注意的是这里注册的物品不能是一个方块对应的物品
     * @throws IllegalArgumentException 当物品是一个ItemBlock或没有实现IVariableTexture接口时
     */
    public static void registerInventoryIcon(String namespace, String path, Item item) {
        if (item instanceof ItemBlock) {
            throw new IllegalArgumentException("Item不能是ItemBlock的子类");
        }
        if (item instanceof IVariableTexture) {
            IVariableTexture iVariableTexture = (IVariableTexture) item;
            RegisterInventoryIcon0(namespace, path, iVariableTexture, item);
        } else throw new IllegalArgumentException("Item没有实现IVariableTexture接口");
    }

    /**
     * 注册显示在Inventory界面的物品，其逻辑就是使参数item的损耗值从可变纹理处获取
     * 其纹理名称后注册。
     *
     * @param iVariableTexture 可变纹理
     * @param item             item
     */
    private static void RegisterInventoryIcon0(String namespace, String path, IVariableTexture iVariableTexture, Item item) {
        // subItems中储存着此item的所有子物品
        NonNullList<ItemStack> subItems = NonNullList.create();
        item.getSubItems(Objects.requireNonNull(item.getCreativeTab()), subItems);
        for (ItemStack subItem : subItems) {
            int    damage      = subItem.getItemDamage();
            String textureName = iVariableTexture.getIconName(damage);
            ModelResourceLocation res = new ModelResourceLocation(
                    namespace + ":" + path + textureName, "inventory"
            );
            ModelLoader.setCustomModelResourceLocation(item, damage, res);
        }
    }
}
