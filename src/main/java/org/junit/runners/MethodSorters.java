package org.junit.runners;

import org.junit.internal.IMethodSorter;
import org.junit.internal.MethodSorter;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Sort the methods into a specified execution order.
 * Defines common {@link MethodSorter} implementations.
 *
 * @since 4.11
 */
public class MethodSorters {

    private MethodSorters() {}

    /**
     * Sorts the test methods by the method name, in lexicographic order,
     * with {@link Method#toString()} used as a tiebreaker
     */
    public static class NameAscendingMethodSorter implements IMethodSorter {
        public Comparator<Method> getComparator() {
            return MethodSorter.NAME_ASCENDING;
        }
    }

    /**
     * Sorts the test methods in a deterministic, but not predictable, order
     */
    public static class DefaultMethodSorter implements IMethodSorter {
        public Comparator<Method> getComparator() {
            return MethodSorter.DEFAULT;
        }
    }

    /**
     * Leaves the test methods in the order returned by the JVM.
     * Note that the order from the JVM may vary from run to run
     */
    public static class JvmMethodSorter implements IMethodSorter {
        public Comparator<Method> getComparator() {
            return MethodSorter.JVM;
        }
    }
}
