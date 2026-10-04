package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<User> getUsers() {
		return userRepository.findAll();
	}

	public User addUser(User user) {
		try {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			return userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			throw new RuntimeException("Add user error: username already exists");
		}
	}

	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public boolean checkUserExists(String username) {
		return userRepository.existsByUsername(username);
	}
}