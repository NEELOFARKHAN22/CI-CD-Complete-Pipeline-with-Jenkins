package java_crud.main.Repo;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java_crud.main.Model.Details;
import java_crud.main.Model.Employee;


public interface DetailRepository extends JpaRepository<Details,Integer>{
@Transactional
@Modifying
//@Query(value = "delete * from employee as e inner join details as d ON e.id = (?1) where employee_id in (?1);",nativeQuery=true) 
@Query(value = "delete from details where employee_id = (?1);",nativeQuery=true) 
public void deleteByEmpId(Integer id);
public Details findByEmployeee(Employee employeee);
}