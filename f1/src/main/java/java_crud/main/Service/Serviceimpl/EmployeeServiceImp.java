package java_crud.main.Service.Serviceimpl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java_crud.main.Model.Details;
import java_crud.main.Model.Employee;
import java_crud.main.Repo.DetailRepository;
import java_crud.main.Repo.EmployeeRepository;
import java_crud.main.Service.EmployeeService;
import java_crud.main.dto.ResponseDAO;

@Component
public class EmployeeServiceImp implements EmployeeService {

	@Autowired
	EmployeeRepository employeeRepository;
	@Autowired
	DetailRepository detailRepository;

	public List<Employee> listAll() {
		return employeeRepository.findAll();
	}

	public List<Details> listAll1() {
		return detailRepository.findAll();
	}

	public ResponseDAO add(ResponseDAO response) {
		Employee employee = new Employee();
		employee.setName(response.getName());
		try {
			String password = Base64.getEncoder().encodeToString(response.getPassword().getBytes("UTF-8"));
			employee.setPassword(password);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		employee.setPrice(response.getPrice());
		employee.setSalary(response.getSalary());
		employeeRepository.save(employee);

		Details details = new Details();

		details.setEmployeee(employee);
		details.setAddress(response.getAddress());
		details.setDesignation(response.getDesignation());
		details.setGender(response.getGender());
		detailRepository.save(details);

		return response;
	}

	public ResponseDAO getById(Integer id) {

		Optional<Employee> employee = employeeRepository.findById(id);

		if (employee.isPresent()) {
			Employee emp = employee.get();
			Details details = detailRepository.findByEmployeee(emp);
			ResponseDAO responseDAO = new ResponseDAO(emp, details);

			return responseDAO;
		} else {
			return null;
		}

	}

	public List<ResponseDAO> getByName(String name) {

		List<Employee> employee = employeeRepository.findAllByName(name);
		List<ResponseDAO> response = new ArrayList<ResponseDAO>();
		if (employee != null) {
			employee.forEach(employeee -> {
				Details details = detailRepository.findByEmployeee(employeee);
				ResponseDAO responseDAO = new ResponseDAO(employeee, details);
				response.add(responseDAO);
			});
			return response;
		} else {
			return null;
		}
	}

	public ResponseDAO getEmpByIdAndName(Integer id, String name) {

		Employee employee = employeeRepository.findByIdAndName(id, name);

		if (employee != null) {
			Details details = detailRepository.findByEmployeee(employee);
			ResponseDAO responseee = new ResponseDAO(employee, details);

			return responseee;
		} else {
			return null;
		}

	}

	public List<ResponseDAO> getall() {
		List<Details> detailss = listAll1();
		List<ResponseDAO> response = new ArrayList<ResponseDAO>();
		detailss.forEach(details -> {
			ResponseDAO responsee = new ResponseDAO(details);

			response.add(responsee);
		});
		return response;
	}

	public ResponseDAO update(ResponseDAO responseDAO, int id) {

		Employee employee = employeeRepository.findById(id).get();

		employee.setName(responseDAO.getName());
		employee.setPrice(responseDAO.getPrice());
		try {
			String password = Base64.getEncoder().encodeToString(responseDAO.getPassword().getBytes("UTF-8"));
			employee.setPassword(password);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		employee.setSalary(responseDAO.getSalary());
		employeeRepository.save(employee);

		Details det = detailRepository.findById(id).get();
		det.setEmployeee(employee);
		det.setAddress(responseDAO.getAddress());
		det.setDesignation(responseDAO.getDesignation());
		det.setGender(responseDAO.getGender());
		detailRepository.save(det);

		return responseDAO;
	}

	public void delete(Integer id) {
		Employee employee = employeeRepository.findById(id).get();
		Details details = detailRepository.findByEmployeee(employee);
		detailRepository.deleteByEmpId(id);
		employeeRepository.deleteById(id);
	}

}
