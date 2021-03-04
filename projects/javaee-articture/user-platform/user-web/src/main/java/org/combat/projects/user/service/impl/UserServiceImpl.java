package org.combat.projects.user.service.impl;

import org.combat.projects.user.domain.User;
import org.combat.projects.user.repository.DatabaseUserRepository;
import org.combat.projects.user.repository.UserRepository;
import org.combat.projects.user.service.UserService;

/**
 * 用户接口实现类
 */
public class UserServiceImpl implements UserService {

    UserRepository userRepository = DatabaseUserRepository.init();

    @Override
    public boolean register(User user) {
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
