package com.prediction.repository;

//public interface UserRepository {
//
//}

//package com.project.studentml.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prediction.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);
}
