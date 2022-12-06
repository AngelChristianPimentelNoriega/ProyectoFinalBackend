package com.invoice.configuration.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.invoice.api.dto.DtoProduct;


@FeignClient(name = "product-service")
public interface ProductClient {

	/*
	 * Requerimiento 3
	 * Actualizar método getProduct para obtener la información necesaria de un producto
	 */
	@GetMapping("product/{gtin}")
	public ResponseEntity<DtoProduct> getProduct(@PathVariable("gtin") String gtin);
	
	@GetMapping("product/{gtin}/stock")
	public ResponseEntity<DtoProduct> getProductStock(@PathVariable("gtin") String gtin);

	@GetMapping("product/{gtin}/price")
	public ResponseEntity<DtoProduct> getProductPrice(@PathVariable("gtin") String gtin);

	@PutMapping("product/{gtin}/stock/{stock}")
	public ResponseEntity<DtoProduct> updateProductStock(@PathVariable("gtin") String gtin, @PathVariable("stock") Integer stock);

}
