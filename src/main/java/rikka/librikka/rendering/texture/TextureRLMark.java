package rikka.librikka.rendering.texture;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 将这个注解标记在某个类的字段上，在EasyTextureLoader.registerTextures方法
 * 执行时其储存的ResourceLocation路径将被初始化为ResourceLocation实例。
 * <p>
 * 需要注意的是次注解只有写在TextureAtlasSprite子类型的字段上才会被识别。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TextureRLMark
{
    String resourceLocation();
}
