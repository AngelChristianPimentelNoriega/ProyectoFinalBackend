package com.product.api.service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.product.api.dto.ApiResponse;
import com.product.api.dto.DtoProductList;
import com.product.api.entity.Category;
import com.product.api.entity.Product;
import com.product.api.repository.RepoCategory;
import com.product.api.repository.RepoProduct;
import com.product.api.repository.RepoProductList;
import com.product.exception.ApiException;

@Service
public class SvcProductImp implements SvcProduct {

	@Autowired
	private RepoProduct repoProduct;
	
	@Autowired
	private RepoCategory repoCategory;
	
	@Autowired
	private RepoProductList repoProductList;

	@Override
	public List<DtoProductList> listProducts(Integer categoryId) {
		return repoProductList.listProducts(1, categoryId);
	}
	
	@Override
	public Product readProduct(String gtin) {
		Product product = repoProduct.findByGtinAndStatus(gtin, 1);
		if (product != null) {
			return product;
		}else
			throw new ApiException(HttpStatus.NOT_FOUND, "product does not exist");
	}

	@Override
	public ApiResponse createProduct(Product product) {
		Category category = repoCategory.findByCategoryId(product.getCategory().getCategoryId());
		System.out.println(category.getCategory());
		
		if(category == null)
			throw new ApiException(HttpStatus.NOT_FOUND, "category not found");		
		
		Product retrievedProduct = repoProduct.findProduct(product.getGtin(), product.getProduct(), product.getCategory().getCategoryId());
		
		if(retrievedProduct != null) {
			if(retrievedProduct.getStatus() == 0) {
				repoProduct.updateProduct(retrievedProduct.getProduct_id(), product.getGtin(), product.getProduct(), product.getDescription(), product.getPrice(), product.getStock(), product.getCategory().getCategoryId());
				return new ApiResponse("product activated");
			}
			if(product.getGtin().equalsIgnoreCase(retrievedProduct.getGtin()))
				throw new ApiException(HttpStatus.BAD_REQUEST, "product gtin already exist");
			if(product.getProduct().equalsIgnoreCase(retrievedProduct.getProduct()))
				throw new ApiException(HttpStatus.BAD_REQUEST, "product name already exist");
		}
		
		repoProduct.save(product);
		return new ApiResponse("product created");
	}

	@Override
	public ApiResponse updateProduct(Product product, Integer id) {
		Integer updated = 0;
		try {
			updated = repoProduct.updateProduct(id, product.getGtin(), product.getProduct(), product.getDescription(), product.getPrice(), product.getStock(), product.getCategory().getCategoryId());
		}catch (DataIntegrityViolationException e) {
			if (e.getLocalizedMessage().contains("gtin"))
				throw new ApiException(HttpStatus.BAD_REQUEST, "product gtin already exist");
			if (e.getLocalizedMessage().contains("product"))
				throw new ApiException(HttpStatus.BAD_REQUEST, "product name already exist");
			if (e.contains(SQLIntegrityConstraintViolationException.class))
				throw new ApiException(HttpStatus.BAD_REQUEST, "category not found");
		}
		
		if(updated == 0)
			throw new ApiException(HttpStatus.BAD_REQUEST, "product cannot be updated");
		else
			return new ApiResponse("product updated");
	}

	@Override
	public ApiResponse deleteProduct(Integer id) {
		if (repoProduct.deleteProduct(id) > 0)
			return new ApiResponse("product removed");
		else
			throw new ApiException(HttpStatus.BAD_REQUEST, "product cannot be deleted");
	}

	@Override
	public ApiResponse updateProductStock(String gtin, Integer stock) {
		Product product = readProduct(gtin);
		if(stock > product.getStock())
			throw new ApiException(HttpStatus.BAD_REQUEST, "stock to update is invalid");
		
		repoProduct.updateProductStock(gtin, product.getStock() - stock);
		return new ApiResponse("product stock updated");
	}

	@Override
	public ApiResponse updateProductCategory(Category category, String gtin) {
		try {
			if(repoCategory.findByCategoryId(category.getCategoryId()) == null )  
				throw new ApiException(HttpStatus.NOT_FOUND, "category not found");
			if(repoProduct.updateProductCategory(category.getCategoryId(), gtin) > 0)
				return new ApiResponse("product category updated");
			else
				throw new ApiException(HttpStatus.NOT_FOUND, "product does not exist");
		} catch(DataIntegrityViolationException e) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "category not found");
		}
	}
}
