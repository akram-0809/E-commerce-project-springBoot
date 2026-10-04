package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	public List<Product> getProducts() {
		return productRepository.findAll();
	}

	public Product addProduct(Product product) {
		return productRepository.save(product);
	}

	public Product getProduct(Integer id) {
		return productRepository.findById(id).orElse(null);
	}

	public Product updateProduct(Integer id, Product product) {
		product.setId(id);
		return productRepository.save(product);
	}

	public boolean deleteProduct(Integer id) {
		if (productRepository.existsById(id)) {
			productRepository.deleteById(id);
			return true;
		}
		return false;
	}
}