package com.product.api.service;

import java.util.List;

import com.product.api.dto.ApiResponse;
import com.product.api.dto.DtoProductList;
import com.product.api.entity.Category;
import com.product.api.entity.Product;

public interface SvcProduct {
	
	public abstract List<DtoProductList> listProducts(Integer categoryId);
	public abstract Product readProduct(String gtin);
	public abstract ApiResponse createProduct(Product product);
	public abstract ApiResponse updateProduct(Product product, Integer id);
	public abstract ApiResponse updateProductStock(String gtin, Integer stock);
	public abstract ApiResponse deleteProduct(Integer id);
	public abstract ApiResponse updateProductCategory(Category category, String gtin);

}
