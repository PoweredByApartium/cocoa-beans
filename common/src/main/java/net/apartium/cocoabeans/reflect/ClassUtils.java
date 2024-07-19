package net.apartium.cocoabeans.reflect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ClassUtils {

    public static Collection<Class<?>> getSuperClassAndInterfaces(Class<?> clazz) {
        if (clazz == null || clazz == Object.class)
            return Collections.emptyList();

        List<Class<?>> list = new ArrayList<>();
        list.add(clazz);

        list.addAll(getSuperClassAndInterfaces(clazz.getSuperclass()));

        for (Class<?> anInterface : clazz.getInterfaces())
            list.addAll(getSuperClassAndInterfaces(anInterface));

        return list;
    }

}
