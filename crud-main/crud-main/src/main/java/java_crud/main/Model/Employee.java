package java_crud.main.Model;


import javax.persistence.Entity;
import javax.persistence.*;


@Entity
public class Employee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private float price;
	@Column(nullable = false)
	private Integer salary;
	@Column(nullable = false)
	private String password;

	public Employee(Integer id, String name, float price, Integer salary, String password) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.salary = salary;
		this.password = password;
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

	public Employee() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

}
