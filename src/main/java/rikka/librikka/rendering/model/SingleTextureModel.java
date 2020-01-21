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
 */
@SideOnly(Side.CLIENT)
public class SingleTextureModel implements IModel
{
    private final List<ResourceLocation> locations = new ArrayList<>();
    private final Set<ResourceLocation>  textures  = new HashSet<>();
    private final IModel                 model;
    private final IModelState            defaultState;

    /**
     * 既然是Texture（纹理）就应该是在assets/modid/textures路径下，具体的路径由三个参数共同决定。
     * 注：路径中间的“../textures/..”应该是mc自己加上的，并不属于此方法逻辑
     *
     * @param domain         命名空间，决定了modid
     * @param textureImgPath 贴图名字，带扩展名的图片文件名。当然了，这个名字也可以用“xxx1/xxx2.png”
     *                       的形式来指定位于items/blocks文件夹下的子文件夹
     * @param isBlock        是否是一个方块 此参数决定了../textures/路径后是“blocks”还是“items”
     * @throws Exception
     */
    public SingleTextureModel(String domain, String textureImgPath, boolean isBlock) throws Exception {
        /*
         * 拼装路径
         * 这里拼装成的路径为“[modid]:[blocks or items]/[图片路径.png]”
         */
        String resourcePath = domain + ":" + (isBlock ? "blocks/" : "items/") + textureImgPath;

        Variant          variant = new SimpleTextureVariant(resourcePath, isBlock);
        ResourceLocation loc     = variant.getModelLocation();
        this.locations.add(loc);

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
        return ImmutableList.copyOf(this.locations);
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
     * @return
     */
    @Override
    public IModelState getDefaultState() {
        return this.defaultState;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        IBakedModel bakedModel = this.model.bake(MultiModelState.getPartState(state, this.model, 0), format, bakedTextureGetter);
        return bakedModel;
    }
}
