package com.tech.brain.service;

import com.tech.brain.model.Product;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ObjectUtils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public interface CommandService {

    Product createProduct(Product product);

    Product updateProduct(Long id, Product product);

    String deleteProduct(Long id);

    default void copyNonNullProperties(Object dto, Object entity) {
        BeanUtils.copyProperties(dto, entity, getNullPropertyName(dto));
    }

    private String[] getNullPropertyName(Object dto) {
        final BeanWrapper beanWrapper = new BeanWrapperImpl(dto);
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object dtoValue = beanWrapper.getPropertyValue(pd.getName());
            if (ObjectUtils.isEmpty(dtoValue)) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
