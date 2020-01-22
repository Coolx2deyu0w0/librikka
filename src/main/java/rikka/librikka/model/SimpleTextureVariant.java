package rikka.librikka.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

/**
 * A block variant uses the same texture for all 6 sides / An item with single texture
 * [这种“Variant”是六个面都是用同一种纹理的方块，比如石头，煤块等]
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
