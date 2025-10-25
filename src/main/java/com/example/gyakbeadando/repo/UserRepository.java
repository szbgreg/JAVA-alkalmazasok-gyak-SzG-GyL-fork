package com.example.gyakbeadando.repo;

import com.example.gyakbeadando.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
