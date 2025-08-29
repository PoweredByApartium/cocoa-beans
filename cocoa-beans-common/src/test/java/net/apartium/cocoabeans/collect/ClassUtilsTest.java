package net.apartium.cocoabeans.collect;

import net.apartium.cocoabeans.reflect.ClassUtils;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassUtilsTest {

    @Test
    void getSuperClassAndInterfacesNull() {
        assertEquals(Collections.emptyList(), ClassUtils.getSuperClassAndInterfaces(null));
    }

    @Test
    void getSuperClassAndInterfacesObject() {
        assertEquals(Collections.emptyList(), ClassUtils.getSuperClassAndInterfaces(Object.class));
    }

    @Test
    void getSuperClassAndInterfacesList() {
        assertEquals(Set.of(List.class, Collection.class, Iterable.class), ClassUtils.getSuperClassAndInterfaces(List.class));
    }

    @Test
    void getSuperClassAndInterfacesArrayList() {
        assertEquals(Set.of(Serializable.class, Cloneable.class, RandomAccess.class, ArrayList.class, AbstractList.class, AbstractCollection.class, List.class, Collection.class, Iterable.class), ClassUtils.getSuperClassAndInterfaces(ArrayList.class));
    }

}
