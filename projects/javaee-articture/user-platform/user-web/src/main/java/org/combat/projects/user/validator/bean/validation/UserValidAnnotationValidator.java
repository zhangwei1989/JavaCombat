package org.combat.projects.user.validator.bean.validation;

import org.combat.projects.user.domain.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author zhangwei
 * @Description UserValidAnnotationValidator
 * @Date: 2021/3/17 09:54
 */
public class UserValidAnnotationValidator implements ConstraintValidator<UserValid, User> {

    private int idRange;

    @Override
    public void initialize(UserValid constraintAnnotation) {
        this.idRange = constraintAnnotation.idRange();
    }

    @Override
    public boolean isValid(User value, ConstraintValidatorContext context) {
        // 获取模版信息
        context.getDefaultConstraintMessageTemplate();

        return false;
    }
}
