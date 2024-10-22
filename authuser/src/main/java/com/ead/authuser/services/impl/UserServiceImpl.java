package com.ead.authuser.services.impl;

import com.ead.authuser.models.UserCourseModel;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.repositories.UserCourseRepository;
import com.ead.authuser.repositories.UserRepository;
import com.ead.authuser.services.UserService;
import com.ead.authuser.specifications.SpecificationTemplate;
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
    private final UserCourseRepository userCourseRepository;
    public UserServiceImpl(UserRepository userRepository, UserCourseRepository userCourseRepository) {
        this.userRepository = userRepository;
        this.userCourseRepository = userCourseRepository;
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
        List<UserCourseModel> userCourseModelList = this.userCourseRepository.findAllUSerCourseIntoUser(userModel.getUserId());
        if (!userCourseModelList.isEmpty()) {
            this.userCourseRepository.deleteAll(userCourseModelList);
        }
        this.userRepository.delete(userModel);
    }
    @Transactional
    @Override
    public void save(UserModel userModel) {
        this.userRepository.save(userModel);
    }
    @Override
    public boolean existsByUsername(String username) {
        return this.userRepository.existsByUserName(username);
    }
    @Override
    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }
    @Override
    public Page<UserModel> findAll(Specification<UserModel> spec, Pageable pageable) {
        return this.userRepository.findAll(spec,pageable);
    }


}
