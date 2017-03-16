package org.junit.internal;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Created on 16/03/2017.
 */
public interface IMethodSorter {

    /**
     * Returns the comparator to be used by the Method Sorter.
     * @return The method comparator.
     */
    Comparator<Method> getComparator();
}
