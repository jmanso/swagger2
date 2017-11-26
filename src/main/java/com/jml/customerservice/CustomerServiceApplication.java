package com.jml.customerservice;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@SpringBootApplication
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner runExecute(CustomerRepository customerRepository) {
		return customers -> {
			customerRepository.save(new CustomerBuilder()
					 .name("Juan").lastName("Perez").cusNum("XX-12765-Z").build());
			
			customerRepository.save(new CustomerBuilder()
					 .name("Lucia").lastName("Alvarez").cusNum("XX-12345-R").build());
			
			customerRepository.save(new CustomerBuilder()
					 .name("Scott").lastName("Smith").cusNum("XX-12345-T").build());
		};
	}
}

@RestController
@RequestMapping(value="/customer")
@Api(value="Customers")
@ApiResponses(value={
		@ApiResponse(code=400, message="Invalid Request"),
		@ApiResponse(code=404, message="Not found resource"),
		@ApiResponse(code=401, message="Not Allowed"),
		@ApiResponse(code=500, message="Generic error")})
class CustomerServiceController {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@ApiResponses(value={
			@ApiResponse(code=200, message="Well Done", response=Customer.class, responseContainer="List")})
	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Customer> findCustomers() {
		return customerRepository.findAll();
	}
	
	@ApiResponses(value={
			@ApiResponse(code=200, message="Well Done", response=Customer.class)})
	@RequestMapping(value="/{customerId}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Customer findOneCustomer(@PathVariable final Long customerId) {
		return customerRepository.findOne(customerId);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public void saveCustomer(@ApiParam(value="JSON with Person to create", required=true) @RequestBody Customer customer) {
		customerRepository.save(customer);
	}
	
	@RequestMapping(method=RequestMethod.PUT)
	public void modifyCustomer(@ApiParam(value="JSON with Person to modify", required=true) @RequestBody Customer customer) {
		customerRepository.save(customer);
	}
	
	@RequestMapping(value="/{customerId}", method=RequestMethod.DELETE)
	public void deleteCustomer(@PathVariable final Long customerId) {
		customerRepository.delete(customerId);
	}
	
}

interface CustomerRepository extends JpaRepository<Customer, Long> {
	
}

@Entity
class Customer {
		
	@Id @GeneratedValue
	@ApiModelProperty(value="Customer identifier", required=true, example="1", position=1)
	private Long id;

	@ApiModelProperty(value="Customer Name", example="Juan", position=2)
	private String name;

	@ApiModelProperty(value="Customer LastName", example="Perez", position=3)
	private String lastName;

	@ApiModelProperty(value="Customer number", example="XX-12345-T", position=4)
	private String cusNum;

	public Customer() {

	}
	
	public Customer(String name, String lastName, String cusNum) {
		this.name = name;
		this.lastName = lastName;
		this.cusNum = cusNum;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getLastName() {
		return lastName;
	}

	public String getCusNum() {
		return cusNum;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", name=" + name + ", lastName=" + lastName + ", cusNum=" + cusNum + "]";
	}
	
}

class CustomerBuilder {
	
	String name;
	String lastName;
	String cusNum;
	
	public CustomerBuilder name(String name) {
		this.name = name;
		return this;
	}
	
	public CustomerBuilder lastName(String lastName) {
		this.lastName = lastName;
		return this;
	}
	
	public CustomerBuilder cusNum(String cusNum) {
		this.cusNum = cusNum;
		return this;
	}
	
	public Customer build() {
		return new Customer(name, lastName, cusNum);
	}
	
}