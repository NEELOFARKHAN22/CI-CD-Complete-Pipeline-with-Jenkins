package java_crud.main.dto;

import javax.persistence.Column;

import java_crud.main.Model.Details;
import java_crud.main.Model.Employee;

public class ResponseDAO {
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private float price;
	
	@Column(nullable = false)
	private Integer salary;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
	private String gender;
	
	@Column(nullable = false)
	private String address;
	
	@Column(nullable = false)
	private String designation;
	

	public ResponseDAO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ResponseDAO(Details details) {
		this.name = details.getEmployeee().getName();
		this.price = details.getEmployeee().getPrice();
		this.salary = details.getEmployeee().getSalary();
		this.gender = details.getGender();
		this.address = details.getAddress();
		this.designation = details.getDesignation();
	}
	
	public ResponseDAO(Employee employee, Details details) {
		this.name = employee.getName();
		this.price = employee.getPrice();
		this.salary = employee.getSalary();
		this.password = employee.getPassword();
		this.gender = details.getGender();
		this.address = details.getAddress();
		this.designation = details.getDesignation();
	}
	public ResponseDAO(Employee employeee) {
		// TODO Auto-generated constructor stub
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public Integer getSalary() {
		return salary;
	}
	public void setSalary(Integer salary) {
		this.salary = salary;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}


}
