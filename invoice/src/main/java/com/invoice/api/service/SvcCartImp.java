package com.invoice.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.invoice.api.dto.ApiResponse;
import com.invoice.api.dto.DtoCustomer;
import com.invoice.api.dto.DtoProduct;

import com.invoice.api.entity.Cart;
import com.invoice.api.repository.RepoCart;

import com.invoice.configuration.client.CustomerClient;
import com.invoice.configuration.client.ProductClient;

import com.invoice.exception.ApiException;

@Service
public class SvcCartImp implements SvcCart {

	@Autowired
	RepoCart repo;
	
	@Autowired
	CustomerClient customerCl;

	@Autowired
	ProductClient productCl;
	
	@Override
	public List<Cart> getCart(String rfc) {
		return repo.findByRfcAndStatus(rfc,1);
	}

	@Override
	public ApiResponse addToCart(Cart cart) {
		if(!validateCustomer(cart.getRfc()))
			throw new ApiException(HttpStatus.BAD_REQUEST, "customer does not exist");
		
		//Validamos que el producto exista
		if(!validateProduct(cart.getGtin()))
			throw new ApiException(HttpStatus.BAD_REQUEST, "product does not exist");
		//Obtenemos el stock del producto 		
		ResponseEntity<DtoProduct> response = productCl.getProduct(cart.getGtin());
		Integer product_stock = response.getBody().getStock();


		//Validamos que la cantidad no sea mayor al stock
		if(cart.getQuantity() > product_stock) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "invalid quantity");
		}
		
		//Validamos que el producto no exista en el carrito
		List<Cart> cartList = repo.findByRfcAndStatus(cart.getRfc(), 1);
		for(Cart c : cartList) {
			//Si el producto ya existe en el carrito, sumamos la cantidad
			if(c.getGtin().equals(cart.getGtin())) {
				//Validamos que la cantidad no sea mayor al stock
				if(c.getQuantity() + cart.getQuantity() > product_stock) {
					throw new ApiException(HttpStatus.BAD_REQUEST, "invalid quantity");
				}
				//Actualizamos la cantidad
				c.setQuantity(c.getQuantity() + cart.getQuantity());
				cart.setStatus(1);
				repo.save(c);
				return new ApiResponse("item updated");
			}
		}
		
		cart.setStatus(1);
		repo.save(cart);
		return new ApiResponse("item added");
	}

	@Override
	public ApiResponse removeFromCart(Integer cart_id) {
		if (repo.removeFromCart(cart_id) > 0)
			return new ApiResponse("item removed");
		else
			throw new ApiException(HttpStatus.BAD_REQUEST, "item cannot be removed");
	}

	@Override
	public ApiResponse clearCart(String rfc) {
		if (repo.clearCart(rfc) > 0)
			return new ApiResponse("cart removed");
		else
			throw new ApiException(HttpStatus.BAD_REQUEST, "cart cannot be removed");
	}
	
	private boolean validateCustomer(String rfc) {
		try {
			ResponseEntity<DtoCustomer> response = customerCl.getCustomer(rfc);
			if(response.getStatusCode() == HttpStatus.OK)
				return true;
			else
				return false;
		}catch(Exception e) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "unable to retrieve customer information");
		}
	}

	private boolean validateProduct(String gtin) {
		try{
			ResponseEntity<DtoProduct> response = productCl.getProduct(gtin);
			if(response.getStatusCode() == HttpStatus.OK)
				return true;
			else
				return false;
		}catch(Exception e) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "unable to retrieve product information");
		}
	}

}
