package com.oocl.springbootemployee.integration;

import com.oocl.springbootemployee.model.Employee;
import com.oocl.springbootemployee.model.Gender;
import com.oocl.springbootemployee.repository.EmployeeRepository;
import com.oocl.springbootemployee.repository.IEmployeeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmployeeTest {

    @Autowired
    private MockMvc client;

    @Autowired
    private IEmployeeRepository employeeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE employee AUTO_INCREMENT = 1");
        employeeRepository.save(new Employee(1, "John Smith", 32, Gender.MALE, 5000.0, Boolean.TRUE));
        employeeRepository.save(new Employee(2, "Jane Johnson", 28, Gender.FEMALE, 6000.0, Boolean.TRUE));
        employeeRepository.save(new Employee(3, "David Williams", 35, Gender.MALE, 5500.0, Boolean.TRUE));
        employeeRepository.save(new Employee(4, "Emily Brown", 23, Gender.FEMALE, 4500.0, Boolean.TRUE));
        employeeRepository.save(new Employee(5, "Michael Jones", 40, Gender.MALE, 7000.0, Boolean.TRUE));
    }

    @Test
    void should_return_employees_when_get_all_given_employee_exist() throws Exception {

        client.perform(MockMvcRequestBuilders.get("/employees"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("John Smith"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1));
    }

    @Test
    void should_return_employee_when_get_by_id() throws Exception {
        Employee employee = employeeRepository.save(new Employee(1, "John Smith", 32, Gender.MALE, 5000.0, Boolean.TRUE));
        client.perform(MockMvcRequestBuilders.get("/employees/" + employee.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(employee.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John Smith"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(32))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender").value(Gender.MALE.name()));
    }


    @Test
    void should_return_employees_when_get_by_gender() throws Exception {
        client.perform(MockMvcRequestBuilders.get("/employees")
                        .param("gender", "FEMALE"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].age")
                        .value(containsInAnyOrder(28, 23)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].name")
                        .value(containsInAnyOrder("Jane Johnson", "Emily Brown")));
    }

    @Test
    void should_create_employee_success() throws Exception {
        // Given
        String givenName = "New Employee";
        Integer givenAge = 18;
        Gender givenGender = Gender.FEMALE;
        Double givenSalary = 5000.0;
        String givenEmployee = String.format(
                "{\"name\": \"%s\", \"age\": \"%s\", \"gender\": \"%s\", \"salary\": \"%s\"}",
                givenName,
                givenAge,
                givenGender,
                givenSalary
        );

        // When
        // Then
        client.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(givenEmployee)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(givenName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(givenAge))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender").value(givenGender.name()));
        List<Employee> employees = employeeRepository.findAll();
        assertThat(employees).hasSize(6);
    }

    @Test
    void should_update_employee_success() throws Exception {
        Employee employee = employeeRepository.save(new Employee(1, "John Smith", 32, Gender.MALE, 5000.0, Boolean.TRUE));
        // Given
        Integer givenId = employee.getId();
        String givenName = "New Employee";
        Integer givenAge = 30;
        Gender givenGender = Gender.FEMALE;
        Double givenSalary = 5432.0;
        String givenEmployee = String.format(
                "{\"id\": %s, \"name\": \"%s\", \"age\": \"%s\", \"gender\": \"%s\", \"salary\": \"%s\"}",
                givenId,
                givenName,
                givenAge,
                givenGender,
                givenSalary
        );

        // When
        // Then
        client.perform(MockMvcRequestBuilders.put("/employees/" + employee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(givenEmployee)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(givenName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(givenAge));
    }

    @Test
    void should_remove_employee_success() throws Exception {
        Employee employee = employeeRepository.save(new Employee(1, "John Smith", 32, Gender.MALE, 5000.0, Boolean.TRUE));

        // When
        // Then
        client.perform(MockMvcRequestBuilders.delete("/employees/" + employee.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Employee employeeResult = employeeRepository.findEmployeeById(employee.getId()).get();
        assertThat(employeeResult.getId()).isEqualTo(employee.getId());
        assertThat(employeeResult.getActive()).isEqualTo(Boolean.FALSE);
    }

    @Test
    void should_return_employees_when_get_by_pageable() throws Exception {
        //then
        client.perform(MockMvcRequestBuilders.get("/employees")
                        .param("pageIndex", "1")
                        .param("pageSize", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("David Williams"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(35))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].gender").value(Gender.MALE.name()));
    }

    @Test
    void should_return_employee_not_found_exception_with_404_status_when_update_a_not_existed_employee() throws Exception {
        // given
        int givenId = 1000;
        String givenName = "A Not Existed Employee";
        Integer givenAge = 30;
        Gender givenGender = Gender.FEMALE;
        Double givenSalary = 5432.0;
        String givenEmployee = String.format(
                "{\"id\": %s, \"name\": \"%s\", \"age\": \"%s\", \"gender\": \"%s\", \"salary\": \"%s\"}",
                givenId,
                givenName,
                givenAge,
                givenGender,
                givenSalary
        );
        // when
        // then
        client.perform(MockMvcRequestBuilders.put("/employees/" + givenId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(givenEmployee)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("EmployeeNotFoundException"));
    }

    @Test
    void should_throw_exception_when_delete_resigned_employee() throws Exception {
        Integer deleteId = 1;
        client.perform(MockMvcRequestBuilders.delete("/employees/" + deleteId)
                .param("id", String.valueOf(deleteId)));

        String givenName = "John Smith";
        Integer givenAge = 28;
        Gender givenGender = Gender.MALE;
        Double givenSalary = 100.0;
        String givenEmployee = String.format(
                "{\"name\": \"%s\", \"age\": \"%s\", \"gender\": \"%s\", \"salary\": \"%s\"}",
                givenName,
                givenAge,
                givenGender,
                givenSalary
        );

        client.perform(MockMvcRequestBuilders.put("/employees/" + deleteId)
                        .param("id", String.valueOf(deleteId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(givenEmployee))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("this employee has resigned!"));

    }
}