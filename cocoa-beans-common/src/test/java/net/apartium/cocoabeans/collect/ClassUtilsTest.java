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
        assertEquals(Set.of(E.class, A.class, B.class, C.class, D.class), ClassUtils.getSuperClassAndInterfaces(E.class));
    }

    private interface A {

    }

    private interface B extends A {

    }

    private interface C {

    }

    private interface D extends B, C {

    }

    private class E implements D {

        private E() {

        }

    }

}
