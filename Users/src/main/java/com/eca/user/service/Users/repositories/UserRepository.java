package com.eca.user.service.Users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eca.user.service.Users.entities.User;

public interface UserRepository extends JpaRepository<User, String>{
}