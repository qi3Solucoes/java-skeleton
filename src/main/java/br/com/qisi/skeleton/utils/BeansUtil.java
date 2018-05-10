package br.com.qisi.skeleton.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.Collection;

public class BeansUtil<T> {
    public T copyNonNullProperties(T from, T to) {
        if (from == null || to == null || to.getClass() != from.getClass()) return null;

        final BeanWrapper src = new BeanWrapperImpl(from);
        final BeanWrapper trg = new BeanWrapperImpl(to);

        for (final Field property : to.getClass().getDeclaredFields()) {
            Object providedObject = src.getPropertyValue(property.getName());
            if (providedObject != null && !(providedObject instanceof Collection<?>)) {
                trg.setPropertyValue(
                        property.getName(),
                        providedObject);
            }
        }
        return to;
    }
}