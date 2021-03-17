package org.combat.projects.user.service.impl;

import org.combat.projects.user.domain.User;
import org.combat.projects.user.repository.UserRepository;
import org.combat.projects.user.service.UserService;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 用户接口实现类
 */
public class UserServiceImpl implements UserService {

    @Resource(name = "bean/EntityManager")
    private EntityManager entityManager;

    @Resource(name = "bean/UserRepository")
    private UserRepository userRepository;

    @Resource(name = "bean/Validator")
    private Validator validator;

    @Override
    public boolean register(User user) {
        Set<ConstraintViolation<User>> violationSet = validator.validate(user);

        for (ConstraintViolation<User> violation : violationSet) {
            System.out.println(violation.getMessage());
        }

        if (violationSet.size() != 0) {
            return false;
        }

        return userRepository.save(user);
    }

    @Override
    public boolean deregister(User user) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User queryUserById(Long id) {
        return null;
    }

    @Override
    public User queryUserByNameAndPassword(String name, String password) {
        return null;
    }
}
