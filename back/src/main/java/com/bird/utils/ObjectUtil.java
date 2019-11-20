package com.bird.utils;

import com.bird.common.BirdException;
import com.bird.common.BirdOutException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * @author zhyyy
 **/
public class ObjectUtil {
    /**
     * 由父类实例构建子类实例，且完全复制父类的属性
     *
     * @param source   父类实例
     * @param <T>      父类泛型
     * @param <R>      子类泛型，必须实现空参数构造方法
     * @param subClass 子类
     * @return 子类实例
     */
    public static <T, R extends T> R generateSubclass(T source, Class<R> subClass) throws BirdOutException {
        Field[] parentFields = source.getClass().getDeclaredFields();
        R r;
        try {
            r = subClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new BirdException("构造子类实例失败:" + subClass.getName());
        }
        Arrays.stream(parentFields)
                // 跳过serialVersionId
                .filter(f -> !"serialVersionId".equals(f.getName()))
                .forEach(f -> {
                    f.setAccessible(true);
                    try {
                        Object val = f.get(source);
                        f.set(r, val);
                    } catch (IllegalAccessException e) {
                        System.out.println(f.getName() + " is not accessible in class " + subClass.getName());
                    }
                });
        return r;
    }
}
