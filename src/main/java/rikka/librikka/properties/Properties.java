package rikka.librikka.properties;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;

public class Properties
{
    public static final IProperty<Integer> FACING_3_BIT = PropertyInteger.create("facing", 0, 7);
    public static final IProperty<Integer> FACING_2_BIT = PropertyInteger.create("facing", 0, 3);
    public static final IProperty<Integer> TYPE_1_BIT   = PropertyInteger.create("type", 0, 1);
    public static final IProperty<Integer> TYPE_2_BIT   = PropertyInteger.create("type", 0, 3);

    public static final IProperty<Boolean> PROPERTY_MIRRORED = PropertyBool.create("mirrored");
}
