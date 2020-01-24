package coolx2deyu0w0.lib.client.model;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用这个类可以不使用JSON文件定义方块模型
 * <p>
 * 基本原理就是获取MC自带的集中cube模型后添加自定义的textureMap来定义模型纹理
 */
public class EasyTextureVariant extends Variant
{
    private final VariantType         variantType;
    private final Map<String, String> textureMap = new HashMap<>();

    /*
     * TODO 此字段暂不使用
     */
    private final Map<String, String> customData = new HashMap<>();

    public EasyTextureVariant(VariantType variantType) {
        super(
                new ResourceLocation(variantType.getModelPath()),
                ModelRotation.X0_Y0, false, 1
        );

        this.variantType = variantType;
    }

    /**
     * 设置某个面的材质路径
     *
     * @param key         对于不同的变体类型，这里的键也有区别。当输入一个当前变体不支持的key是，这个方法不会做任何事情
     * @param texturePath 由于此方法是直接对JSON信息进行修改，所以是从textures的下级路径开始的
     */
    public void setTexture(String key, String texturePath) {
        if (!this.variantType.test(key)) {
            return;
        }

        this.textureMap.put(key, texturePath);
    }

    @Override
    public IModel process(IModel base) {
        base = base.process(ImmutableMap.copyOf(this.customData));
        base = base.retexture(ImmutableMap.copyOf(this.textureMap));
        base = base.smoothLighting(true);
        base = base.gui3d(this.variantType instanceof VariantType.Block);
        base = base.uvlock(isUvLock());
        return base;
    }
}
