/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package coolx2deyu0w0.book.net.minecraftforge.client.model;

import java.util.Collection;

import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;

import java.util.function.Function;

import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.model.animation.IClip;

/**
 * IModel代表了一种原始状态的模型，相当于JSON或是OBJ文件反序列化的结果。在IModel的实例只表示
 * 一个形状，需要经过“烘焙（bake）”后才能在游戏中渲染。
 * <p>
 * 这个接口的实例应该是不可变的，当对它（封装的模型）进行修改时必须重新new一个对象而不是直接
 * 在实例上修改。
 *
 * @see net.minecraftforge.client.model.IModel
 */
public interface CIModel
{
    /**
     * 获得这个模型依赖的全部模型。
     * <p>
     * 在MC的众多模型中依赖是普遍存在的——比如绝大多数的方块都依赖同一个“方体”模型。
     * 模型烘焙机制将确保这里返回的模型先于本实例模型烘焙。需要注意的是模型的“加载”
     * 只是将模型从文件中反序列化为Java对象，而真正意义上的加载则指的是执行IModel
     * 实例中bake方法。
     * <p>
     * 如果发生循环依赖的现象，会抛异常
     *
     * @return 返回一个不可变集合
     * @see ModelLoaderRegistry.LoaderException
     */
    default Collection<ResourceLocation> getDependencies() {
        return ImmutableList.of();
    }

    /*
     * Returns all texture locations that this model depends on.
     * Assume that returned collection is immutable.
     */

    /**
     * 获得这个模型依赖的全部纹理
     * <p>
     * 原文中使用了“depends on”这个词，我猜这里的依赖可能说的是这个模型使用了这个纹理，
     * 模型烘培机制将确保在此对象被烘焙时它所使用的所有纹理都已经被加载。
     *
     * @return 返回一个不可变集合
     */
    default Collection<ResourceLocation> getTextures() {
        return ImmutableList.of();
    }

    /*
     * All model texture coordinates should be resolved at this method.
     * Returned model should be in the simplest form possible, for performance
     * reasons (if it's not ISmartBlock/ItemModel - then it should be
     * represented by List<BakedQuad> internally).
     * Returned model's getFormat() can me less specific than the passed
     * format argument (some attributes can be replaced with padding),
     * if there's no such info in this model.
     */

    /**
     * 烘焙
     * <p>
     * 这个方法时此劫口中最重要的一个方法，使用三个参数将抽象的IModel转换为能够代表Item或是Block的IBakedModel。
     *
     * @param state
     * @param format             顶点格式
     * @param bakedTextureGetter 一个用于获取模型纹理的函数。传入一个RL对象，函数内将在此路径下的纹理
     *                           转换包含它的TextureAtlasSprite对象返回
     * @return 一个比IModel更加不抽象的IBakedModel
     */
    IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter);

    /*
     * Default state this model will be baked with.
     * See IModelState.
     */
    default IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }

    default Optional<? extends IClip> getClip(String name) {
        return Optional.empty();
    }

    /**
     * 修改模型，使用参数中传入的数据对模型进行修改
     *
     * @return 在Forge的说明文档中提到此方法应当返回一个新new的IModel实例而不是直接在
     * 此对象的基础上进行修改
     */
    default CIModel process(ImmutableMap<String, String> customData) {
        return this;
    }

    /**
     * 平滑光照
     */
    default CIModel smoothLighting(boolean value) {
        return this;
    }

    /**
     * 可能是和堆叠状态下的物品是否显示为多层次结构有关？
     */
    default CIModel gui3d(boolean value) {
        return this;
    }

    /**
     * “UV Lock”意味着当模型发生旋转时，它使用的纹理不跟着一起旋转。
     */
    default CIModel uvlock(boolean value) {
        return this;
    }

    /**
     * 下面为翻译forge自带的javadoc：
     * 将一个新的纹理应用于此模型。
     * 返回的模型不能是this而应该新new一个IModel实例，因为一个模型需要能够被多次重设纹理
     * 来生成多个单独的模型（就像钻石块和铁块明显使用的是换了皮的同一个模型）。
     * <p>
     * The input map MAY map to an empty string "" which should be used
     * to indicate the texture was removed. Handling of that is up to
     * the model itself. Such as using default, missing texture, or
     * removing vertices.
     * <p>
     * The input should be considered a DIFF of the old textures, not a
     * replacement as it may not contain everything.
     * 翻译结束。
     * <p>
     * 总之此方法就是根据传入的Map创建新的IModel实例（使用了新的纹理）。在模型的json文件中
     * 可能此方法就是修改了“textures”键下的映射表吧。
     *
     * @param textures 新的纹理
     * @return Model with textures applied.
     */
    default CIModel retexture(ImmutableMap<String, String> textures) {
        return this;
    }
}