package com.customer.api.service;

import java.util.List;

import com.customer.api.dto.ApiResponse;
import com.customer.api.dto.DtoCustomerList;
import com.customer.api.entity.Customer;

public interface SvcCustomer {

	public List<DtoCustomerList> getCustomers();
	public Customer getCustomer(String rfc);
	public ApiResponse createCustomer(Customer customer);
	public ApiResponse updateCustomer(Customer customer, Integer id_customer);
	public ApiResponse updateCustomerImage(String image, Integer id_customer);
	public ApiResponse updateCustomerRegion(Integer id_region, Integer id_customer);
	public ApiResponse deleteCustomer(Integer id_customer);
}