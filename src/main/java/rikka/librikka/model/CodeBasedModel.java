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
import rikka.librikka.rendering.texture.TextureRLMark;

import java.util.Collection;
import java.util.Set;

/**
 * An alternative to ISBRH in 1.7.10 and previous
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

    /**
     * 使用资源路径注册一个纹理
     */
    protected ResourceLocation registerTexture(String textureResourceLocation)
    {
        ResourceLocation resLoc = new ResourceLocation(textureResourceLocation);
        this.textures.add(resLoc);
        return resLoc;
    }

    /**
     * 注册一个资源
     */
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

    /**
     * 获取此模型的包含的纹理
     *
     * IModel中要求返回的是一个不可变集合
     */
    @Override
    public Collection<ResourceLocation> getTextures()
    {
        return ImmutableSet.copyOf(this.textures);
    }

    /**
     * 返回此模型的默认状态
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
