package rikka.librikka.model.loader;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.SingleTextureModel;
import rikka.librikka.model.loader.IModelLoader;

/**
 * 先进的模型加载器。指的是加载器先进而不是加载的模型先进
 * <p>
 * 这个加载器不仅自己包含加载模型的功能，还可以储存多个其他的模型加载器实例，当
 * 遇到自己无法加载的模型文件时将调用这些加载器加载，这大概就是它先进的地方吧...
 * <p>
 * 若材质路径以“virtual/sti/”开头，则将其加载为SingleTextureModel对象，
 * 否则尝试使用registeredLoaders中储存的其他加载器加载这个纹理。
 *
 * @author Rikka0_0
 */
@SideOnly(Side.CLIENT)
public class AdvancedModelLoader implements ICustomModelLoader
{
    /**
     * “STI”的意思是“Simple Texture Item”，简单的item纹理
     */
    private static final String             PATH_STI          = "virtual/sti/";
    public final         String             namespace;
    private final        List<IModelLoader> registeredLoaders = new ArrayList<>();

    /**
     * @param namespace 命名空间
     */
    public AdvancedModelLoader(String namespace) {
        this.namespace = namespace;
        ModelLoaderRegistry.registerLoader(this);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        for (IModelLoader loader : this.registeredLoaders) {
            if (loader instanceof IResourceManagerReloadListener) {
                ((IResourceManagerReloadListener) loader).onResourceManagerReload(resourceManager);
            }
        }
    }

    /**
     * 检查这个RL对应的模型文件能不能被这个加载器加载
     * <p>
     * 以下两种情况会返回false：
     * 1. 命名空间（就是“:”前的部分）和此模型加载器初始化时传入的命名空间不相符
     * 2. 文件路径不以“STI”开头且registeredLoaders列表中没有能够加载此模型的加载器
     */
    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        // 命名空间
        String namespace = modelLocation.getResourceDomain();
        if (!this.namespace.equals(namespace)) {
            return false;
        }

        // 路径
        String resourcePath = modelLocation.getResourcePath();
        if (resourcePath.startsWith(PATH_STI)) {
            return true; // SimpleTextureItem
        } else {
            for (IModelLoader loader : this.registeredLoaders) {
                if (loader.accepts(resourcePath)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 加载模型
     * <p>
     * 在以下三种情况会返回null:
     * 1. 命名空间和domain字段不同
     * 2. 路径不以STI开头且无法被registeredLoaders字段中任何一个加载器加载
     * 3. registeredLoaders字段中的模型加载器返回null
     *
     * @throws Exception registeredLoaders字段中的模型加载器抛出异常
     */
    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        String domain = modelLocation.getResourceDomain();
        if (!this.namespace.equals(domain)) {
            return null;
        }

        String resPath = modelLocation.getResourcePath();
        if (resPath.startsWith(PATH_STI)) {
            resPath = resPath.substring(PATH_STI.length());
            return new SingleTextureModel(this.namespace, resPath, false);    //SimpleTextureItem
        } else {
            for (IModelLoader loader : this.registeredLoaders)
                if (loader.accepts(resPath)) {
                    String variantStr = ((ModelResourceLocation) modelLocation).getVariant();
                    return loader.loadModel(domain, resPath, variantStr);
                }
        }

        return null;
    }

    /**
     * 添加模型加载器
     * <p>
     * 被添加的模型加载器会被储存到registeredLoaders字段中
     */
    public void registerModelLoader(IModelLoader loader) {
        this.registeredLoaders.add(loader);
    }
}
