package com.oocl.springbootemployee.dto.mapper;

import com.oocl.springbootemployee.dto.EmployeeRequest;
import com.oocl.springbootemployee.dto.EmployeeResponse;
import com.oocl.springbootemployee.model.Employee;
import com.oocl.springbootemployee.model.Gender;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeeMapper {
    public Employee toEntity(EmployeeRequest employeeRequest) {
        Employee employee = new Employee();
        employee.setName(employeeRequest.getName());
        employee.setAge(employeeRequest.getAge());
        employee.setGender(Gender.valueOf(employeeRequest.getGender()));
        employee.setSalary(employeeRequest.getSalary());
        employee.setCompanyId(employeeRequest.getCompanyId());
        return employee;
    }

    public EmployeeResponse toResponse(Employee employee) {
        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setId(employee.getId());
        employeeResponse.setAge(employee.getAge());
        employeeResponse.setName(employee.getName());
        employeeResponse.setGender(employee.getGender().name());
        return employeeResponse;
    }

    public List<EmployeeResponse> toResponse(List<Employee> employees) {
        return employees.stream().map(this::toResponse).collect(Collectors.toList());
    }

}
