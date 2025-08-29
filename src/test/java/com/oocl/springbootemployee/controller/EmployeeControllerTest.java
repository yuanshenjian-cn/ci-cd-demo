package com.oocl.springbootemployee.controller;

import com.oocl.springbootemployee.dto.EmployeeResponse;
import com.oocl.springbootemployee.dto.mapper.EmployeeMapper;
import com.oocl.springbootemployee.model.Employee;
import com.oocl.springbootemployee.model.Gender;
import com.oocl.springbootemployee.service.IEmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest({EmployeeController.class, EmployeeMapper.class})
@AutoConfigureJsonTesters
public class EmployeeControllerTest {
    @Autowired
    private MockMvc client;

    @MockBean
    private IEmployeeService employeeService;

    @Autowired
    private JacksonTester<List<EmployeeResponse>> employeeListJsonTest;

    @Autowired
    private JacksonTester<EmployeeResponse> employeeJsonTest;

    private List<Employee> initEmployees = new ArrayList<>();

    @BeforeEach
    void setUp() {
        initEmployees.add(new Employee(1, "John Smith", 32, Gender.MALE, 5000.0, Boolean.TRUE));
        initEmployees.add(new Employee(2, "Jane Johnson", 28, Gender.FEMALE, 6000.0, Boolean.TRUE));
    }

    @Test
    void should_return_employees_list_when_get_all_employee() throws Exception {
        // Given
        when(employeeService.getAllEmployees()).thenReturn(initEmployees);

        // when
        MvcResult result = client.perform(MockMvcRequestBuilders.get("/employees")).andReturn();

        // then
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        List<EmployeeResponse> resultListEmployees = employeeListJsonTest.parseObject(result.getResponse().getContentAsString());
        assertThat(resultListEmployees.get(0).getName()).isEqualTo("John Smith");
        assertThat(resultListEmployees.size()).isEqualTo(2);
    }

    @Test
    void should_return_employees_when_create_a_employee() throws Exception {
        Employee newEmployee = new Employee(3, "Michael", 28, Gender.FEMALE, 6000.0, Boolean.TRUE);
        // Given
        when(employeeService.creat(any(Employee.class))).thenReturn(newEmployee);

        Integer id = 3;
        String name = "Michael";
        Integer age = 28;
        Gender gender = Gender.FEMALE;
        Double salary = 6000.0;
        Boolean isActive = Boolean.TRUE;
        String content = String.format("{\"id\": %d, " +
                        "\"name\": \"%s\", " +
                        "\"age\": %d, " +
                        "\"gender\": \"%s\", " +
                        "\"salary\": %f," +
                        "\"active\": %b}",
                id, name, age, gender.toString(), salary, isActive.toString());
        MvcResult mvcResult = client.perform(MockMvcRequestBuilders.post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)).andReturn();
        EmployeeResponse responseEmployee = employeeJsonTest.parseObject(mvcResult.getResponse().getContentAsString());
        assertEquals(responseEmployee.getGender(),newEmployee.getGender().name());
        assertEquals(responseEmployee.getName(),newEmployee.getName());
    }

}
