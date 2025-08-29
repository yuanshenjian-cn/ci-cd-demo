package com.oocl.springbootemployee.service;

import com.oocl.springbootemployee.exception.EmployeeAgeInvalidException;
import com.oocl.springbootemployee.exception.EmployeeSalaryInvalidException;
import com.oocl.springbootemployee.model.Employee;
import com.oocl.springbootemployee.model.Gender;
import com.oocl.springbootemployee.repository.IEmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock
    private IEmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private List<Employee> initEmployees = new ArrayList<>();

    @BeforeEach
    void setUp(){
        initEmployees.add(new Employee(1, "John Smith", 32, Gender.MALE, 5000.0, Boolean.TRUE));
        initEmployees.add(new Employee(2, "Jane Johnson", 28, Gender.FEMALE, 6000.0, Boolean.TRUE));
    }

    @Test
    void should_return_the_given_employees_when_getAllEmployees() {
        //given
        when(employeeRepository.findAll()).thenReturn(initEmployees);

        //when
        List<Employee> allEmployees = employeeService.getAllEmployees();

        //then
        assertEquals(2, allEmployees.size());
        assertEquals("John Smith", allEmployees.get(0).getName());
    }

    @Test
    void should_return_the_created_employee_when_create_given_a_employee() {
        //given
        Employee lucy = new Employee(1, "Lucy", 18, Gender.FEMALE, 8000.0, Boolean.TRUE);
        when(employeeRepository.save(any(Employee.class))).thenReturn(lucy);

        //when
        Employee createdEmployee = employeeService.creat(lucy);

        //then
        assertEquals("Lucy", createdEmployee.getName());
    }

    @Test
    void should_return_exception_when_create_a_employee_age_greater_than_65(){
        //given
        Employee lucy = new Employee(1, "Lucy", 90, Gender.FEMALE, 8000.0, Boolean.TRUE);
        assertThrows(EmployeeAgeInvalidException.class, () -> employeeService.creat(lucy));
    }

    @Test
    void should_return_exception_when_create_a_employee_age_greater_than_30_and_salary_less_than_20000(){
        //given
        Employee jacky = new Employee(10, "Jacky", 40, Gender.FEMALE, 8000.0, Boolean.TRUE);

        assertThrows(EmployeeSalaryInvalidException.class, () -> employeeService.creat(jacky));
    }

    @Test
    void should_return_active_employee_when_create_a_employee(){
        //given
        Employee herry = new Employee(10, "Herry", 20, Gender.MALE, 8000.0, null);
        Employee herry2 = new Employee(10, "Herry", 20, Gender.MALE, 8000.0, Boolean.TRUE);
        when(employeeRepository.save(any())).thenReturn(herry2);

        //when
        Employee createdEmployee = employeeService.creat(herry);

        //then
        assertTrue(createdEmployee.getActive());
    }
}
