package com.product.api.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.product.api.dto.ApiResponse;
import com.product.api.dto.DtoProductList;
import com.product.api.entity.Category;
import com.product.api.entity.Product;
import com.product.api.service.SvcProduct;
import com.product.exception.ApiException;

@RestController

public class CtrlProduct {

	private static final String LIST_PRODUCTS_ENDPOINT = "/product/category/{category_id}";
	private static final String READ_PRODUCT_ENDPOINT = "/product/{gtin}";
	private static final String CREATE_PRODUCT_ENDPOINT = "/product";
	private static final String UPDATE_PRODUCT_ENDPOINT = "/product/{id}";
	private static final String UPDATE_PRODUCT_STOCK_ENDPOINT = "/product/{gtin}/stock/{stock}";
	private static final String DELETE_PRODUCT_ENDPOINT = "/product/{id}";
	private static final String UPDATE_PRODUCT_CATEGORY_ENDPOINT = "/product/{gtin}/category";
	
	@Autowired
	private SvcProduct svc;
	
	@GetMapping(value = LIST_PRODUCTS_ENDPOINT)
	public ResponseEntity<List<DtoProductList>> listProducts(@NotNull @PathVariable("category_id") Integer categoryId){
		return new ResponseEntity<>(svc.listProducts(categoryId), HttpStatus.OK);
	}
	
	@GetMapping(value = READ_PRODUCT_ENDPOINT)
	public ResponseEntity<Product> readProduct(@NotNull @PathVariable("gtin") String gtin){
		return new ResponseEntity<>(svc.readProduct(gtin), HttpStatus.OK);
	}
	
	@PostMapping(value = CREATE_PRODUCT_ENDPOINT)
	public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody Product product, BindingResult bindingResult){
		if(bindingResult.hasErrors())
			throw new ApiException(HttpStatus.BAD_REQUEST, bindingResult.getAllErrors().get(0).getDefaultMessage());
		return new ResponseEntity<>(svc.createProduct(product), HttpStatus.OK);
	}
	
	@PutMapping(value = UPDATE_PRODUCT_ENDPOINT)
	public ResponseEntity<ApiResponse> updateProduct(@NotNull @PathVariable("id") Integer id, @Valid @RequestBody Product product, BindingResult bindingResult){
		if(bindingResult.hasErrors())
			throw new ApiException(HttpStatus.BAD_REQUEST, bindingResult.getAllErrors().get(0).getDefaultMessage());
		return new ResponseEntity<>(svc.updateProduct(product, id), HttpStatus.OK);
	}
	
	@DeleteMapping(value = DELETE_PRODUCT_ENDPOINT)
	public ResponseEntity<ApiResponse> deleteProduct(@PathVariable("id") Integer id){
		return new ResponseEntity<>(svc.deleteProduct(id), HttpStatus.OK);
	}
	
	@PutMapping(value = UPDATE_PRODUCT_STOCK_ENDPOINT)
	public ResponseEntity<ApiResponse> updateProductStock(@PathVariable("gtin") String gtin, @PathVariable("stock") Integer stock){
		return new ResponseEntity<>(svc.updateProductStock(gtin, stock), HttpStatus.OK);
	}
		
	@PutMapping(value = UPDATE_PRODUCT_CATEGORY_ENDPOINT)
	public ResponseEntity<ApiResponse> updateProductCategory(@PathVariable("gtin") String gtin, @RequestBody Category category){
		return new ResponseEntity<>(svc.updateProductCategory(category, gtin), HttpStatus.OK);
	}
}
