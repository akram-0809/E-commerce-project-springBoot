package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.models.Cart;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.repository.CartRepository;

@Service
public class CartService {

	@Autowired
	private CartRepository cartRepository;

	public Cart addCart(Cart cart) {
		return cartRepository.save(cart);
	}

	public List<Cart> getCarts() {
		return cartRepository.findAll();
	}

	public List<Cart> getCartsByCustomer(User customer) {
		return cartRepository.findByCustomer(customer);
	}

	public void updateCart(Cart cart) {
		cartRepository.save(cart);
	}

	public void deleteCart(Cart cart) {
		cartRepository.delete(cart);
	}
}