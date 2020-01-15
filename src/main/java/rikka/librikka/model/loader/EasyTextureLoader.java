package rikka.librikka.model.loader;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.anno.TextureRLMark;

@SideOnly(Side.CLIENT)
public class EasyTextureLoader
{
    /**
     * 下面那个方法的简化版本，将一直搜索到Object类。但是应该不太常用。
     *
     * @param begin 起始类的实例
     * @param list  RL列表
     */
    @Deprecated
    public static void registerTextures(@Nonnull Object begin, @Nonnull Set<ResourceLocation> list)
    {
        registerTextures(begin, Object.class, list);
    }

    /**
     * 注册纹理
     * <p>
     * 从begin类开始逐层搜索其父类（父类的父类），直到endClass。检查每一个类中的每一个字段，
     * 将带有TextureRLMark注解的字段的注解中储存的路径初始化为ResourceLocation实例。
     *
     * @param begin    起始类的实例
     * @param endClass 终止类
     * @param list     RL列表
     */
    public static void registerTextures(@Nonnull Object begin, @Nonnull Class endClass, @Nonnull Set<ResourceLocation> list)
    {
        for (Class cls = begin.getClass(); cls != endClass; cls = cls.getSuperclass()) {
            // 遍历全部的字段
            for (Field field : cls.getDeclaredFields()) {
                // TextureAtlasSprite是一个包含了Texture信息的类
                if (field.getType().isAssignableFrom(TextureAtlasSprite.class) && field.isAnnotationPresent(TextureRLMark.class)) {
                    TextureRLMark texture    = field.getAnnotation(TextureRLMark.class);
                    String        textureLoc = texture.resourceLocation();
                    list.add(new ResourceLocation(textureLoc));
                }
            }
        }
    }

    @Deprecated
    public static void applyTextures(@Nonnull Object target, @Nonnull Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        applyTextures(target, Object.class, bakedTextureGetter);
    }

    /**
     * 应用纹理
     * <p>
     * 和registerTextures方法一样的搜索逻辑，但是将TextureRLMark中的路径初始化为TextureAtlasSprite实例赋值给带有注解的字段
     *
     * @param begin              起始类的实例
     * @param endClass           终止类
     * @param bakedTextureGetter λ
     */
    public static void applyTextures(@Nonnull Object begin, @Nonnull Class endClass, @Nonnull Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        for (Class cls = begin.getClass(); cls != endClass; cls = cls.getSuperclass()) {
            for (Field field : cls.getDeclaredFields()) {
                if (field.getType().isAssignableFrom(TextureAtlasSprite.class) && field.isAnnotationPresent(TextureRLMark.class)) {

                    TextureRLMark      texture    = field.getAnnotation(TextureRLMark.class);
                    String             textureLoc = texture.resourceLocation();
                    TextureAtlasSprite sprite     = bakedTextureGetter.apply(new ResourceLocation(textureLoc));

                    boolean accessibilityChanged = false;
                    if (!field.isAccessible()) {
                        accessibilityChanged = true;
                        field.setAccessible(true);
                    }

                    try {
                        field.set(begin, sprite);
                        if (accessibilityChanged) {
                            field.setAccessible(true);
                        }
                    } catch (Exception e) {
                        System.err.println("An error occured while populating field " + field.getName() + "in class " + cls.toString());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * @return a registered texture
     */
    public static TextureAtlasSprite getTexture(String textureName)
    {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(textureName);
    }
}
