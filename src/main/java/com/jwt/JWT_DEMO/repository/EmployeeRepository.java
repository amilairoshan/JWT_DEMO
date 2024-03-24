package com.jwt.JWT_DEMO.repository;

import com.jwt.JWT_DEMO.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);

}
