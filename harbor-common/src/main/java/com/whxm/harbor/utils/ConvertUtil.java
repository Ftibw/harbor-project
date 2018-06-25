package com.whxm.harbor.utils;

import com.google.common.collect.Lists;
import com.whxm.harbor.bean.ParameterInvalidItem;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;

public class ConvertUtil {
    public static List<ParameterInvalidItem> convertCVSetToParameterInvalidItemList(Set<ConstraintViolation<?>> constraintViolations) {

        List<ParameterInvalidItem> parameterInvalidItemList = Lists.newArrayList();

        constraintViolations.forEach(item -> {

            ParameterInvalidItem parameterInvalidItem = new ParameterInvalidItem();

            parameterInvalidItem.setFieldName(item.getInvalidValue());

            parameterInvalidItem.setMessage(item.getMessage());

            parameterInvalidItemList.add(parameterInvalidItem);
        });

        return parameterInvalidItemList;
    }

    public static List<ParameterInvalidItem> convertBindingResultToMapParameterInvalidItemList(BindingResult bindingResult) {

        List<ParameterInvalidItem> parameterInvalidItemList = Lists.newArrayList();

        List<FieldError> fieldErrorList = bindingResult.getFieldErrors();

        for (FieldError fieldError : fieldErrorList) {

            ParameterInvalidItem parameterInvalidItem = new ParameterInvalidItem();

            parameterInvalidItem.setFieldName(fieldError.getField());

            parameterInvalidItem.setMessage(fieldError.getDefaultMessage());

            parameterInvalidItemList.add(parameterInvalidItem);
        }

        return parameterInvalidItemList;
    }
}
