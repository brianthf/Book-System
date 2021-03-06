package com.organicautonomy.userservice.dao;

import com.organicautonomy.userservice.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByUsername(String username);
    User findUserByEmail(String email);
}
