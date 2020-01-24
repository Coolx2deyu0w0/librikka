package rikka.librikka.model;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 方块变体指的是方块不同的贴图模式：有的方块的四个侧面与上下两面不同（草方块、竖纹石英），
 * 有的方块六个面完全相同（石头，矿石）...这些都属于方块变体。MC内置了几种基础的方块变体
 * （在minecraft:models/block下，以cube开头的几个JSON文件），当然也可以按照格式来添加新
 * 的变体。在变体JSON文件中使用“#”+名字就意味着以此变体为parent的模型JSON中必须含有
 * "名字": "纹理路径"这样的键值对来设定该面的纹理坐标。
 *
 * 此类支持的方块变体指的是六个面完全相同的变体，即“cube_all”。
 *
 * @author Rikka0_0
 */
@SideOnly(Side.CLIENT)
public class SimpleTextureVariant extends Variant
{
    private static final ResourceLocation GENERATED = new ResourceLocation("minecraft:item/generated");
    private static final ResourceLocation CUBE_ALL  = new ResourceLocation("minecraft:block/cube_all");

    private final ImmutableMap<String, String> textures;
    /**
     * 自定义的数据
     */
    private final ImmutableMap<String, String> customData = ImmutableMap.of();
    private final IModelState                  state;
    private final boolean                      isGui3d;

    /**
     * @param realTexturePath 此路径指向一个png文件
     * @param isBlock         这个文件被应用的模型是否为一个方块
     */
    public SimpleTextureVariant(String realTexturePath, boolean isBlock) {
        this(TRSRTransformation.identity(), realTexturePath, isBlock);
    }

    private SimpleTextureVariant(IModelState state, String texture, boolean isBlock) {
        super(
                isBlock ? SimpleTextureVariant.CUBE_ALL : SimpleTextureVariant.GENERATED,
                state instanceof ModelRotation ? (ModelRotation) state : ModelRotation.X0_Y0,
                false, // 什么是UV Lock？大概就是当模型发生旋转的时候纹理不跟着一起旋转
                1
        ); // uvLock = false, weight always 1

        textures = ImmutableMap.of(isBlock ? "all" : "layer0", texture);

        this.state = state;
        isGui3d = isBlock;
    }

    @Override
    public IModelState getState() {
        return this.state;
    }

    @Override
    public IModel process(IModel base) {
        //base must be cube_all
        //texture string,string {all=minecraft:blocks/diamond_block}
        //ImmutableMap<String, String> customData

        //						smooth=gui3d=true
        return this.runModelHooks(base, true, this.isGui3d, isUvLock(), this.textures, this.customData);
    }

    private IModel runModelHooks(IModel base, boolean smooth, boolean gui3d, boolean uvlock, ImmutableMap<String, String> textureMap, ImmutableMap<String, String> customData) {
        base = base.process(customData);
        base = base.retexture(textureMap);
        base = base.smoothLighting(smooth);
        base = base.gui3d(gui3d);
        base = base.uvlock(uvlock);
        return base;
    }

}
