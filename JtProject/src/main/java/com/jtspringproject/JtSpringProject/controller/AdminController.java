package com.jtspringproject.JtSpringProject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.jtspringproject.JtSpringProject.models.Category;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.services.CategoryService;
import com.jtspringproject.JtSpringProject.services.ProductService;
import com.jtspringproject.JtSpringProject.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final UserService userService;
	private final CategoryService categoryService;
	private final ProductService productService;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public AdminController(UserService userService, CategoryService categoryService, ProductService productService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.categoryService = categoryService;
		this.productService = productService;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping("/index")
	public String index(Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		model.addAttribute("username", username);
		return "index";
	}

	@GetMapping("login")
	public ModelAndView adminLogin(@RequestParam(required = false) String error) {
		ModelAndView mv = new ModelAndView("adminlogin");
		if ("true".equals(error)) {
			mv.addObject("msg", "Invalid username or password. Please try again.");
		}
		return mv;
	}

	@GetMapping(value = { "/", "/Dashboard" })
	public ModelAndView adminHome(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		ModelAndView mv = new ModelAndView("adminHome");
		mv.addObject("admin", authentication.getName());
		return mv;
	}

	@GetMapping("categories")
	public ModelAndView getCategories() {
		ModelAndView mView = new ModelAndView("categories");
		List<Category> categories = this.categoryService.getCategories();
		mView.addObject("categories", categories);
		return mView;
	}

	@PostMapping("/categories")
	public String addCategory(@RequestParam("categoryname") String categoryName) {
		Category category = this.categoryService.addCategory(categoryName);
		if (category != null && category.getName().equals(categoryName)) {
			return "redirect:/admin/categories";
		}
		return "redirect:/admin/categories";
	}

	@GetMapping("categories/delete")
	public String removeCategory(@RequestParam("id") Integer id) {
		this.categoryService.deleteCategory(id);
		return "redirect:/admin/categories";
	}

	@GetMapping("categories/update")
	public String updateCategory(@RequestParam("categoryid") Integer id, @RequestParam("categoryname") String categoryName) {
		this.categoryService.updateCategory(id, categoryName);
		return "redirect:/admin/categories";
	}

	@GetMapping("products")
	public ModelAndView getProducts() {
		ModelAndView mView = new ModelAndView("products");
		List<Product> products = this.productService.getProducts();

		if (products.isEmpty()) {
			mView.addObject("msg", "No products are available");
		} else {
			mView.addObject("products", products);
		}
		return mView;
	}

	@GetMapping("products/add")
	public ModelAndView addProductPage() {
		ModelAndView mView = new ModelAndView("productsAdd");
		List<Category> categories = this.categoryService.getCategories();
		mView.addObject("categories", categories);
		return mView;
	}

	@PostMapping("products/add")
	public String addProduct(@RequestParam("name") String name, @RequestParam("categoryid") Integer categoryId,
			@RequestParam("price") Integer price, @RequestParam("weight") Integer weight,
			@RequestParam("quantity") Integer quantity, @RequestParam("description") String description,
			@RequestParam("productImage") String productImage) {

		Category category = this.categoryService.getCategory(categoryId);
		Product product = new Product();
		product.setName(name);
		product.setCategory(category);
		product.setDescription(description);
		product.setPrice(price);
		product.setImage(productImage);
		product.setWeight(weight);
		product.setQuantity(quantity);
		this.productService.addProduct(product);
		return "redirect:/admin/products";
	}

	@GetMapping("products/update/{id}")
	public ModelAndView updateProductPage(@PathVariable("id") Integer id) {
		ModelAndView mView = new ModelAndView("productsUpdate");
		Product product = this.productService.getProduct(id);
		List<Category> categories = this.categoryService.getCategories();

		mView.addObject("categories", categories);
		mView.addObject("product", product);
		return mView;
	}

	@PostMapping("products/update/{id}")
	public String updateProduct(@PathVariable("id") Integer id, @RequestParam("name") String name,
			@RequestParam("categoryid") Integer categoryId, @RequestParam("price") Integer price,
			@RequestParam("weight") Integer weight, @RequestParam("quantity") Integer quantity,
			@RequestParam("description") String description, @RequestParam("productImage") String productImage) {

		Category category = this.categoryService.getCategory(categoryId);
		Product product = new Product();
		product.setId(id);
		product.setName(name);
		product.setCategory(category);
		product.setDescription(description);
		product.setPrice(price);
		product.setImage(productImage);
		product.setWeight(weight);
		product.setQuantity(quantity);
		this.productService.updateProduct(id, product);
		return "redirect:/admin/products";
	}

	@GetMapping("products/delete")
	public String removeProduct(@RequestParam("id") Integer id) {
		this.productService.deleteProduct(id);
		return "redirect:/admin/products";
	}

	@PostMapping("products")
	public String postProduct() {
		return "redirect:/admin/categories";
	}

	@GetMapping("customers")
	public ModelAndView getCustomerDetail() {
		ModelAndView mView = new ModelAndView("displayCustomers");
		List<User> users = this.userService.getUsers();
		mView.addObject("customers", users);
		return mView;
	}

	@GetMapping("profileDisplay")
	public String profileDisplay(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		User user = userService.getUserByUsername(username);

		if (user != null) {
			model.addAttribute("userid", user.getId());
			model.addAttribute("username", user.getUsername());
			model.addAttribute("email", user.getEmail());
			model.addAttribute("address", user.getAddress());
		} else {
			model.addAttribute("msg", "User not found");
		}

		return "updateProfile";
	}

	@PostMapping("updateuser")
	public String updateUserProfile(@RequestParam("userid") Integer userId, @RequestParam("username") String username,
			@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam("address") String address) {

		User user = userService.getUserByUsername(username);
		if (user == null) {
			user = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		}

		if (user != null) {
			user.setUsername(username);
			user.setEmail(email);
			user.setAddress(address);
			if (password != null && !password.isEmpty()) {
				user.setPassword(passwordEncoder.encode(password));
			}
			userService.addUser(user);

			Authentication newAuthentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
				username,
				password != null && !password.isEmpty() ? password : user.getPassword(),
				SecurityContextHolder.getContext().getAuthentication().getAuthorities()
			);
			SecurityContextHolder.getContext().setAuthentication(newAuthentication);
		}

		return "redirect:/admin/index";
	}
}