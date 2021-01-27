package com.murengezi.feather.Module;

import org.lwjgl.input.Keyboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Tobias Sjöblom
 * Created on 2021-01-12 at 01:17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {

    String name();
    String description();
    String version();
    int keyBind() default Keyboard.CHAR_NONE;
    boolean enabled() default false;

}
