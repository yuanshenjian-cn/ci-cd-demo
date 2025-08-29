package com.oocl.springbootemployee.service;

import com.oocl.springbootemployee.exception.EmployeeAgeInvalidException;
import com.oocl.springbootemployee.exception.EmployeeLeaveNotUpdateException;
import com.oocl.springbootemployee.exception.EmployeeNotFoundException;
import com.oocl.springbootemployee.exception.EmployeeSalaryInvalidException;
import com.oocl.springbootemployee.model.Employee;
import com.oocl.springbootemployee.model.Gender;
import com.oocl.springbootemployee.repository.IEmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService implements IEmployeeService {
    private final IEmployeeRepository employeeRepository;

    public EmployeeService(IEmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee getEmployeeById(Integer id) {
        return employeeRepository.findEmployeeById(id).orElseThrow(
                () -> new EmployeeNotFoundException()
        );
    }

    @Override
    public List<Employee> getEmployeesByGender(Gender gender) {
        return employeeRepository.findEmployeeByGender(gender);
    }

    @Override
    public Employee updateEmployee(Integer id, Employee employee) {
        Employee respEmployee = employeeRepository.findEmployeeById(id).orElseThrow(
                () -> new EmployeeNotFoundException()
        );
        if (respEmployee == null)
            throw new EmployeeNotFoundException();
        if (!respEmployee.getActive()) {
            throw new EmployeeLeaveNotUpdateException("this employee has resigned!");
        }
        employee.setId(id);
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee creat(Employee employee) {
        Employee validEmployee = Optional.ofNullable(employee)
                .map(this::validateEmployeeAge)
                .map(this::validateEmployeeSalary)
                .orElseThrow(() -> new EmployeeAgeInvalidException("employee is null"));
        validEmployee.setActive(Boolean.TRUE);
        return employeeRepository.save(validEmployee);
    }

    private Employee validateEmployeeAge(Employee employee) {
        Optional.ofNullable(employee.getAge())
//                .orElseThrow(() -> new EmployeeAgeInvalidException("employee age is empty!"))
                .ifPresentOrElse(age -> {
                            if (age >= 60 || age <= 15) {
                                throw new EmployeeAgeInvalidException("employee age must be between 15 and 65;");
                            }
                        },
                        () -> {
                            throw new EmployeeAgeInvalidException("employee age is empty!");
                        });

        return employee;
    }

    private Employee validateEmployeeSalary(Employee employee) {
        Optional.ofNullable(employee.getSalary())
                .orElseThrow(() -> new EmployeeSalaryInvalidException("employee salary is empty!"));

        if (employee.getSalary() < 20000 && employee.getAge() >= 30) {
            throw new EmployeeSalaryInvalidException("Employees over 30 years of age (inclusive) with a salary below 20000 cannot be created.");
        }
        return employee;
    }

    @Override
    public void removeEmployee(Integer id) {
        Employee employee = employeeRepository.findEmployeeById(id).get();
        employee.setActive(Boolean.FALSE);
        employeeRepository.save(employee);
    }

    @Override
    public Page<Employee> getAllByPageSize(Integer pageIndex, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return employeeRepository.findAll(pageable);
    }
}
