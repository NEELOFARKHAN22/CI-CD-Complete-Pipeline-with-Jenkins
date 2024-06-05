package java_crud.main.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java_crud.main.Repo.DetailRepository;
import java_crud.main.Repo.EmployeeRepository;
import java_crud.main.Service.EmployeeService;
import java_crud.main.dto.Response;
import java_crud.main.dto.ResponseDAO;
import java_crud.main.message.CustomMessage;

@RestController
@RequestMapping("employee")
public class EmployeeController {

	@Autowired
	EmployeeService employeeService;
	@Autowired
	EmployeeRepository employeeRepository;
	@Autowired
	DetailRepository detailRepository;
//
//	@GetMapping("/list")
	@RequestMapping(value = "/fetch", method = RequestMethod.GET)
	public  String checkApi() {
		return "Hello";
	}
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
	public ResponseEntity<?> getEmployees(@RequestParam(required = false) Integer id,
			@RequestParam(required = false) String name) {
		List<ResponseDAO> response = new ArrayList<>();
		Response res = new Response();
		if (id != null && name == null) {
			ResponseDAO responsee = employeeService.getById(id);
			if (responsee == null) {
				res.setStatus(false);
				res.setMessage("User Not Found");
				return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
			} else {
				response.add(responsee);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} else if (name != null && id == null) {
			List<ResponseDAO> responsee = employeeService.getByName(name);

			if (responsee == null) {
				res.setStatus(false);
				res.setMessage("User Not Found");
				return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
			} else {
				return new ResponseEntity<>(responsee, HttpStatus.OK);
			}
		} else if (name != null && id != null) {
			ResponseDAO responsee = employeeService.getEmpByIdAndName(id, name);
			if (responsee == null) {
				res.setStatus(false);
				res.setMessage("User Not Found");
				return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
			} else {
				response.add(responsee);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}

		} else {
			List<ResponseDAO> responsee = employeeService.getall();
			return new ResponseEntity<>(responsee, HttpStatus.OK);
		}

	}

	@PostMapping("/add")

	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addDetails(@RequestBody @Valid ResponseDAO response) {

		Response res = new Response();
		employeeService.add(response);
		res.setStatus(true);
		res.setMessage("User Added Successfully!!!!");
		res.setData(null);
		System.out.println("++++++" + res.getMessage());

		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@PostMapping("/update/{id}")
	public ResponseEntity<?> updateById(@RequestBody ResponseDAO response, @PathVariable int id) {
		Response res = new Response();
		if (response != null) {
			ResponseDAO responsee = employeeService.getById(id);
			if (responsee != null) {
				employeeService.update(response, id);
				res.setStatus(true);
				res.setMessage("User Updated Successfully!!!!");
				return new ResponseEntity<>(res, HttpStatus.OK);
			} else {
				res.setStatus(false);
				res.setMessage("User Not Updated");
				return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
			}
		} else {
			res.setStatus(false);
			res.setMessage("User Not Found!!!!");

			return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteById(@PathVariable Integer id) {

		Response res = new Response();
		if (id != null) {
			ResponseDAO responsee = employeeService.getById(id);
			System.out.println("+++++" + responsee);
			if (responsee != null) {
				employeeService.delete(id);
				res.setStatus(true);
				res.setMessage("User Deleted Successfully!!!!!");
				return new ResponseEntity<>(res, HttpStatus.OK);
			} else {
				res.setStatus(false);
				res.setMessage("User Not Found");
				System.out.println("++inside+++" + res.getMessage());
				return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
			}
		} else {
			res.setStatus(false);
			res.setMessage("User Not Found");
			System.out.println("++inside+++" + id);
			return new ResponseEntity<>(res, HttpStatus.NOT_ACCEPTABLE);
		}

	}
}
