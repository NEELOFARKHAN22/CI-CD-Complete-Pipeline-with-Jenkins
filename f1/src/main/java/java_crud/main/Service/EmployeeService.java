package java_crud.main.Service;

import java.util.List;
import java_crud.main.Model.Employee;
import java_crud.main.dto.ResponseDAO;

public interface EmployeeService {

	public List<Employee> listAll();
	
	ResponseDAO update(ResponseDAO response, int id);

	void delete(Integer id);

	public List<ResponseDAO> getall();

	public ResponseDAO getEmpByIdAndName(Integer id, String name);

	public ResponseDAO add(ResponseDAO response);

	public ResponseDAO getById(Integer id);

	public List<ResponseDAO> getByName(String name);

}
