package coolx2deyu0w0.book.net.minecraft.client.renderer.block.model;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 此接口的实例是有IModel的bake方法产生的，代表了一个已经优化并简化为（几乎）准备好进入GPU进行
 * 渲染的几何体。
 * <p>
 * 文档中提示不需要手动实现这个接口，因为已经有了一个现成的实现。
 */
@SideOnly(Side.CLIENT)
public interface CIBakedModel
{
    /**
     * IBakedModel的核心方法，此方法调用频率非常高，所以对性能要求较高
     *
     * @param state 模型是一个Block则此项非空
     * @param side  用于平面剔除，即看不到的面就不参与渲染了
     * @param rand  一个随机数，意义不明
     * @return 将用于渲染的顶点数据
     */
    List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand);

    /**
     * 此模型是否会收到环境光的影响
     */
    boolean isAmbientOcclusion();

    /**
     * 是否以3D形态在GUI界面中显示
     * <p>
     * In GUIs this also disables the lighting.
     */
    boolean isGui3d();

    /**
     * TODO
     */
    boolean isBuiltInRenderer();

    /**
     * 此模型对应的粒子效果
     * <p>
     * 比如：玩家打碎方块产生的粒子和玩家吃掉食物时嘴边产生的粒子等
     *
     * @return TextureAtlasSprite封装了一个纹理
     */
    TextureAtlasSprite getParticleTexture();

    @Deprecated
    default ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    /**
     * 只在此模型是一个Item模型时才被使用
     *
     * @return ItemOverrideList
     */
    ItemOverrideList getOverrides();

    default boolean isAmbientOcclusion(IBlockState state) {
        return isAmbientOcclusion();
    }

    /*
     * Returns the pair of the model for the given perspective, and the matrix
     * that should be applied to the GL state before rendering it (matrix may be null).
     */
    default org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        // 此处由于返回值问题，原本是不存在转型的
        return net.minecraftforge.client.ForgeHooksClient.handlePerspective((IBakedModel) this, cameraTransformType);
    }
}