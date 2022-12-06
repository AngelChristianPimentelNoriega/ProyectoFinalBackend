package com.invoice.api.service;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.invoice.api.entity.Cart;
import com.invoice.api.repository.RepoCart;

import com.invoice.api.dto.ApiResponse;
import com.invoice.api.entity.Invoice;
import com.invoice.api.entity.Item;
import com.invoice.api.repository.RepoInvoice;
import com.invoice.api.repository.RepoItem;

import com.invoice.configuration.client.ProductClient;

import com.invoice.exception.ApiException;

import feign.Response;

import com.invoice.api.dto.DtoProduct;

@Service
public class SvcInvoiceImp implements SvcInvoice {

	@Autowired
	RepoInvoice invoiceRepo;
	
	@Autowired
	RepoCart cartRepo;

	@Autowired
	RepoItem repoItem;

	@Autowired
	RepoCart repoCart;

	@Autowired
	ProductClient productCl;

	@Override
	public List<Invoice> getInvoices(String rfc) {
		return invoiceRepo.findByRfcAndStatus(rfc, 1);
	}

	@Override
	public List<Item> getInvoiceItems(Integer invoice_id) {
		return repoItem.getInvoiceItems(invoice_id);
	}

	@Override
	public ApiResponse generateInvoice(String rfc) {
		/*
		 * Requerimiento 5
		 * Implementar el método para generar una factura 
		 */
		
		List<Cart> cart = cartRepo.findByRfcAndStatus(rfc, 1);
		if(cart.isEmpty())
			throw new ApiException(HttpStatus.NOT_FOUND, "cart has no items");
		
		Invoice invoice = new Invoice();
		invoice.setRfc(rfc);
		invoice.setStatus(0);
		invoice.setTotal(0.0);
		invoice.setSubtotal(0.0);
		invoice.setTaxes(0.0);
		invoice.setCreated_at(LocalDateTime.now());
		invoice = invoiceRepo.save(invoice);

		invoice.setRfc(rfc);

		for (Cart c : cart) {
			ResponseEntity<DtoProduct> response = productCl.getProduct(c.getGtin());
			Double unitPrice = response.getBody().getPrice();
			Double total = unitPrice * c.getQuantity();
			Double taxes = total * 0.16;
			Double subtotal = total - taxes;
			Item item = new Item();
			item.setId_invoice(invoice.getInvoice_id());
			item.setGtin(c.getGtin());
			item.setQuantity(c.getQuantity());
			item.setUnit_price(unitPrice);
			item.setSubtotal(subtotal);
			item.setTaxes(taxes);
			item.setTotal(total);
			item.setStatus(1);

			Integer newStock = response.getBody().getStock() - c.getQuantity();

			ResponseEntity<DtoProduct> response2 = productCl.updateProductStock(c.getGtin(), newStock);
			if (response2.getStatusCode() != HttpStatus.OK)
				throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "error updating product stock");

			repoItem.save(item);
			invoice.setTotal(invoice.getTotal() + total);
			invoice.setSubtotal(invoice.getSubtotal() + subtotal);
			invoice.setTaxes(invoice.getTaxes() + taxes);
		}
		invoice.setStatus(1);
		
		invoiceRepo.save(invoice);

		repoCart.clearCart(rfc);

		return new ApiResponse("invoice generated");
	}
}
