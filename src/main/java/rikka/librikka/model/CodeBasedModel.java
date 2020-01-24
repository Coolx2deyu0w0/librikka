package rikka.librikka.model;

import java.util.HashSet;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.loader.TextureRLMark;

import java.util.Collection;
import java.util.Set;

/**
 * An alternative to ISBRH in 1.7.10 and previous
 *
 * 其实IModel和IBakedModel可以看做是同一个概念的两种不同状态，分开的原因是
 * 为了将一个IModel实例“烘焙”成几个不同的IBakedModel。当一个类同时实现这两
 * 个接口（就像这个类一样）时并不会失去这种特性——即便是成了IBakedModel也仍
 * 然具备IModel的数据结构，仍然可以调用process进行修改并再次baked产生一个
 * 新的IBakedModel实例。当然，这种设计唯一的缺陷就是会比分开占用更多的内存
 * 空间。
 *
 * @author Rikka0_0
 */
@SideOnly(Side.CLIENT)
public abstract class CodeBasedModel implements IModel, IBakedModel
{
    private final Set<ResourceLocation> textures = new HashSet<>();

    /**
     * 初始化。在初始化的过程中将从子类开始所有带有{@link TextureRLMark}注解的字段全部加入
     * texture列表中。
     */
    protected CodeBasedModel()
    {
        EasyTextureLoader.registerTextures(this, CodeBasedModel.class, textures);
    }

    protected ResourceLocation registerTexture(String textureResourceLocation)
    {
        ResourceLocation resLoc = new ResourceLocation(textureResourceLocation);
        this.textures.add(resLoc);
        return resLoc;
    }

    protected ResourceLocation registerTexture(ResourceLocation resLoc)
    {
        this.textures.add(resLoc);
        return resLoc;
    }

    /**
     * “烘焙”
     */
    protected abstract void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry);

    ////////////////
    /// IModel
    ////////////////

    /**
     * 获取此模型依赖的所有模型
     * 这个实现和{@link IModel}是一样的
     */
    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return ImmutableList.of();
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        return ImmutableSet.copyOf(this.textures);
    }

    /**
     * 返回此模型的默认状态（初始帧）
     *
     * @see IModelState
     */
    @Override
    public IModelState getDefaultState()
    {
        return TRSRTransformation.identity();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        // 将子类中所有的TextureAtlasSprite类型字段赋值
        EasyTextureLoader.applyTextures(this, CodeBasedModel.class, bakedTextureGetter);
        this.bake(bakedTextureGetter);
        return this;
    }

    /////////////////
    /// IBakedModel
    /////////////////

    /**
     * 是否计算环境光遮挡
     */
    @Override
    public boolean isAmbientOcclusion()
    {
        return false;
    }

    @Override
    public boolean isGui3d()
    {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return ItemOverrideList.NONE;
    }
}
