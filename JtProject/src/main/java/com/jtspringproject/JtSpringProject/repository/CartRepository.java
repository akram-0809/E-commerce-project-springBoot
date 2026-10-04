package com.jtspringproject.JtSpringProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jtspringproject.JtSpringProject.models.Cart;
import com.jtspringproject.JtSpringProject.models.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
	List<Cart> findByCustomer(User customer);
}