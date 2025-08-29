package com.oocl.springbootemployee.service;

import com.oocl.springbootemployee.model.Employee;
import com.oocl.springbootemployee.model.Gender;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IEmployeeService {
    Employee getEmployeeById(Integer id);

    List<Employee> getEmployeesByGender(Gender gender);

    Employee updateEmployee(Integer id, Employee employee);

    List<Employee> getAllEmployees();

    Employee creat(Employee employee);

    void removeEmployee(Integer id);

    Page<Employee> getAllByPageSize(Integer pageIndex, Integer pageSize);
}
