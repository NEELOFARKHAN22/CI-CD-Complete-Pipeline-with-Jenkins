package java_crud.main.Repo;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java_crud.main.Model.Employee;
import java_crud.main.dto.ResponseDAO;

public interface EmployeeRepository extends JpaRepository<Employee, Integer>
{
 public Employee findByName(String name);
 public Optional<Employee> findById(Integer id);
public void save(ResponseDAO emp);
@Transactional
@Modifying 
@Query(value = "select * from employee where name = (?1);",nativeQuery=true) 
public List<Employee> findAllByName(String name);
@Query(value = "select * from employee where id = (?1) and name = (?2);",nativeQuery=true) 
public Employee findByIdAndName(Integer id ,String name);

}
