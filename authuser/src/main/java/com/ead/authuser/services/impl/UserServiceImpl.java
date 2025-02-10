package com.ead.authuser.services.impl;

import com.ead.authuser.enums.ActionType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.publishers.UserEventPublisher;
import com.ead.authuser.repositories.UserRepository;
import com.ead.authuser.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher;
    public UserServiceImpl(UserRepository userRepository, UserEventPublisher userEventPublisher) {
        this.userRepository = userRepository;
        this.userEventPublisher = userEventPublisher;
    }
    @Override
    public List<UserModel> findAll() {
        return this.userRepository.findAll();
    }
    @Override
    public Optional<UserModel> findById(UUID userId) {
        return this.userRepository.findById(userId);
    }
    @Transactional
    @Override
    public void delete(UserModel userModel) {
        this.userRepository.delete(userModel);
    }
    @Transactional
    @Override
    public UserModel save(UserModel userModel) {
        return this.userRepository.save(userModel);
    }
    @Override
    public boolean existsByUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }
    @Override
    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }
    @Override
    public Page<UserModel> findAll(Specification<UserModel> spec, Pageable pageable) {
        return this.userRepository.findAll(spec,pageable);
    }

    public UserModel saveUser(UserModel userModel) {
        userModel = save(userModel);
        userEventPublisher.publicherUserEvent(userModel.convertToUserEventDto(), ActionType.CREATE);
        return userModel;

    }

    @Override
    public void deleteUser(UserModel userModel) {
        delete(userModel);
        userEventPublisher.publicherUserEvent(userModel.convertToUserEventDto(), ActionType.DELETE);
    }
    @Transactional
    @Override
    public UserModel updateUser(UserModel userModel) {
        userModel = save(userModel);
        userEventPublisher.publicherUserEvent(userModel.convertToUserEventDto(), ActionType.UPDATE);
        return userModel;
    }

    @Override
    public UserModel updatePassword(UserModel userModel) {
        return save(userModel);
    }
}
