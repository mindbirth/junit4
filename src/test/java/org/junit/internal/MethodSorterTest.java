package org.junit.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

public class MethodSorterTest {
    private static final String ALPHA = "java.lang.Object alpha(int,double,java.lang.Thread)";
    private static final String BETA = "void beta(int[][])";
    private static final String GAMMA_VOID = "int gamma()";
    private static final String GAMMA_BOOLEAN = "void gamma(boolean)";
    private static final String DELTA = "void delta()";
    private static final String EPSILON = "void epsilon()";
    private static final String SUPER_METHOD = "void superMario()";
    private static final String SUB_METHOD = "void subBowser()";

    static class DummySortWithoutAnnotation {
        Object alpha(int i, double d, Thread t) {
            return null;
        }

        void beta(int[][] x) {
        }

        int gamma() {
            return 0;
        }

        void gamma(boolean b) {
        }

        void delta() {
        }

        void epsilon() {
        }
    }

    static class Super {
        void superMario() {
        }
    }

    static class Sub extends Super {
        void subBowser() {
        }
    }

    private List<String> getDeclaredMethodNames(Class<?> clazz) {
        Method[] actualMethods = MethodSorter.getDeclaredMethods(clazz);

        // Obtain just the names instead of the full methods.
        List<String> names = new ArrayList<String>();
        for (Method m : actualMethods) {
            // Filter out synthetic methods from, e.g., coverage tools.
            if (!m.isSynthetic()) {
                names.add(m.toString().replace(clazz.getName() + '.', ""));
        	}
        }
        
        return names;
    }

    @Test
    public void testMethodsNullSorterSelf() {
        List<String> expected = Arrays.asList(EPSILON, BETA, ALPHA, DELTA, GAMMA_VOID, GAMMA_BOOLEAN);
        List<String> actual = getDeclaredMethodNames(DummySortWithoutAnnotation.class);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testMethodsNullSorterSuper() {
        List<String> expected = Arrays.asList(SUPER_METHOD);
        List<String> actual = getDeclaredMethodNames(Super.class);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testMethodsNullSorterSub() {
        List<String> expected = Arrays.asList(SUB_METHOD);
        List<String> actual = getDeclaredMethodNames(Sub.class);
        assertEquals(expected, actual);
    }

    @FixMethodOrder(MethodSorters.DefaultMethodSorter.class)
    static class DummySortWithDefault {
        Object alpha(int i, double d, Thread t) {
            return null;
        }

        void beta(int[][] x) {
        }

        int gamma() {
            return 0;
        }

        void gamma(boolean b) {
        }

        void delta() {
        }

        void epsilon() {
        }
    }

    @Test
    public void testDefaultMethodSorter() {
        List<String> expected = Arrays.asList(EPSILON, BETA, ALPHA, DELTA, GAMMA_VOID, GAMMA_BOOLEAN);
        List<String> actual = getDeclaredMethodNames(DummySortWithDefault.class);
        assertEquals(expected, actual);
    }

    @FixMethodOrder(MethodSorters.JvmMethodSorter.class)
    static class DummySortJvm {
        Object alpha(int i, double d, Thread t) {
            return null;
        }

        void beta(int[][] x) {
        }

        int gamma() {
            return 0;
        }

        void gamma(boolean b) {
        }

        void delta() {
        }

        void epsilon() {
        }
    }

    @Test
    public void testJvmMethodSorter() {
        Method[] fromJvmWithSynthetics = DummySortJvm.class.getDeclaredMethods();
        Method[] sorted = MethodSorter.getDeclaredMethods(DummySortJvm.class);
        assertArrayEquals(fromJvmWithSynthetics, sorted);
    }

    @FixMethodOrder(MethodSorters.NameAscendingMethodSorter.class)
    static class DummySortWithNameAsc {
        Object alpha(int i, double d, Thread t) {
            return null;
        }

        void beta(int[][] x) {
        }

        int gamma() {
            return 0;
        }

        void gamma(boolean b) {
        }

        void delta() {
        }

        void epsilon() {
        }
    }

    @Test
    public void testAscendingMethodSorter() {
        List<String> expected = Arrays.asList(ALPHA, BETA, DELTA, EPSILON, GAMMA_VOID, GAMMA_BOOLEAN);
        List<String> actual = getDeclaredMethodNames(DummySortWithNameAsc.class);
        assertEquals(expected, actual);
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public @interface NumberOrdering {
        int order();
    }

    static class OrderedComparator implements IMethodSorter {

        public Comparator<Method> getComparator() {
            return new Comparator<Method>() {
                public int compare(Method m1, Method m2) {
                    NumberOrdering orderAnnotation1 = m1.getAnnotation(NumberOrdering.class);
                    NumberOrdering orderAnnotation2 = m2.getAnnotation(NumberOrdering.class);
                    if (orderAnnotation2 == null) {
                        return 1;
                    }

                    if (orderAnnotation1 == null) {
                        return 1;
                    }

                    if (orderAnnotation1.order() < orderAnnotation2.order()) {
                        return -1;
                    } else if (orderAnnotation1.order() == orderAnnotation2.order()) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            };
        }
    }

    @FixMethodOrder(OrderedComparator.class)
    static abstract class ParentOrderedSortWithAsc {
        @NumberOrdering(order = 0)
        void alpha() {
        }
    }

    @FixMethodOrder(OrderedComparator.class)
    static class OrderedSortWithAsc extends ParentOrderedSortWithAsc {

        @NumberOrdering(order = 10)
        Object alpha(int i, double d, Thread t) {
            return null;
        }

        @NumberOrdering(order = 5)
        void beta(int[][] x) {
        }

        @NumberOrdering(order = 4)
        int gamma() {
            return 0;
        }

        @NumberOrdering(order = 3)
        void gamma(boolean b) {
        }

        @NumberOrdering(order = 2)
        void delta() {
        }

        @NumberOrdering(order = 1)
        void epsilon() {
        }
    }

    @Test
    public void testCustomSorter() {
        List<String> expected = Arrays.asList("void org.junit.internal.MethodSorterTest$ParentOrderedSortWithAsc.alpha()",
                EPSILON, DELTA, GAMMA_BOOLEAN, GAMMA_VOID, BETA, ALPHA);
        List<String> actual = getDeclaredMethodNames(OrderedSortWithAsc.class);
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }
}
