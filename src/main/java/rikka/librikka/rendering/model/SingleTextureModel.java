package rikka.librikka.rendering.model;

import java.util.*;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.MultiModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;
import rikka.librikka.model.SimpleTextureVariant;

/**
 * 单一贴图模型。这种模型直的是六个面都是用同一贴图的模型。比如各种矿石，石头原石之属。
 *
 * @see net.minecraftforge.client.model.IModel IModle
 */
@SideOnly(Side.CLIENT)
public class SingleTextureModel implements IModel
{
    private final List<ResourceLocation> resourceLocations = new ArrayList<>();
    private final List<ResourceLocation> textures          = new ArrayList<>();
    /**
     * 由此可见这个模型只是一个模型的封装
     */
    private final IModel                 model;
    /**
     * 默认的模型状态
     * <p>
     * 当一个模型具备“动画效果”时，一个方块状态代表了动画的“一帧”。当然，模型状态是有限的，
     * 但是随着游戏的渲染刷新率提高时，系统会在两个状态之间做一些“平滑过渡”的操作。这也就意
     * 味着在实际的屏幕展示中可能会呈现无数种模型状态。<b color=red>另外这一段是我猜的</b>。
     */
    private final IModelState            defaultState;

    /**
     * 这里使用的纹理路径可能并非在一般为方块或是物品绑定贴图时使用的带有“../textures/..”
     * 的路径，而是自定义的文件路径结构。比如这里就是“modid:blocks/.../xxx.png”。
     *
     * @param domain         命名空间，决定了modid
     * @param textureImgPath 贴图名字，带扩展名的图片文件名。当然了，这个名字也可以用“xxx1/xxx2.png”
     *                       的形式来指定位于items/blocks文件夹下的子文件夹
     * @param isBlock        是否是一个方块 此参数决定了../textures/路径后是“blocks”还是“items”
     * @throws Exception
     */
    public SingleTextureModel(String domain, String textureImgPath, boolean isBlock) throws Exception {
        /*
         * 真实的材质图路径
         * 这里拼装成的路径为“[modid]:[blocks or items]/[图片路径.png]”
         */
        String realTexturePath = domain + ":" + (isBlock ? "blocks/" : "items/") + textureImgPath;

        Variant          variant = new SimpleTextureVariant(realTexturePath, isBlock);
        ResourceLocation loc     = variant.getModelLocation();
        this.resourceLocations.add(loc);

        IModel preModel = ModelLoaderRegistry.getModel(loc);
        this.model = variant.process(preModel);
        for (ResourceLocation location : this.model.getDependencies()) {
            ModelLoaderRegistry.getModelOrMissing(location);
        }

        this.textures.addAll(this.model.getTextures()); // Kick this, just in case.

        Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
        builder.add(Pair.of(this.model, variant.getState()));
        this.defaultState = new MultiModelState(builder.build());
    }

    /**
     * 获取此模型依赖的所有模型的位置
     *
     * @return 根据接口的要求返回的集合是不可变的
     */
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.copyOf(this.resourceLocations);
    }

    /**
     * 获取此模型使用的纹理的位置
     *
     * @return 根据接口的要求返回的集合是不可变的
     */
    @Override
    public Collection<ResourceLocation> getTextures() {
        return ImmutableSet.copyOf(this.textures);
    }

    /**
     * 获取此模型的默认状态
     *
     * @return
     */
    @Override
    public IModelState getDefaultState() {
        return this.defaultState;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return this.model.bake(MultiModelState.getPartState(state, this.model, 0), format, bakedTextureGetter);
    }
}
