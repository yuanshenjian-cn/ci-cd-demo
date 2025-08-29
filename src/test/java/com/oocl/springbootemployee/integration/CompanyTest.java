package com.oocl.springbootemployee.integration;

import com.oocl.springbootemployee.model.Company;
import com.oocl.springbootemployee.model.Employee;
import com.oocl.springbootemployee.model.Gender;
import com.oocl.springbootemployee.repository.ICompanyRepository;
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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CompanyTest {
    @Autowired
    private MockMvc client;

    @Autowired
    private ICompanyRepository companyRepository;

    @Autowired
    private IEmployeeRepository employeeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        companyRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE employee AUTO_INCREMENT=1;");
        jdbcTemplate.execute("ALTER TABLE company AUTO_INCREMENT=1;");
        Company company = new Company();
        company.setName("afs");
        Employee employee1 = new Employee(null, "Jacky", 24, Gender.FEMALE, 2000.0, Boolean.TRUE);
        Employee employee2 = new Employee(null, "Michael", 24, Gender.FEMALE, 2000.0, Boolean.TRUE);
        company.setEmployees(List.of(employee1, employee2));
        companyRepository.save(company);
    }


    @Test
    void should_return_paged_companies_when_get_by_page_params() throws Exception {
        // When
        client.perform(MockMvcRequestBuilders.get("/companies")
                        .param("pageIndex", "0")
                        .param("pageSize", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("afs"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].employeeResponses[0].name")
                        .value("Jacky"));
    }

    @Test
    void should_return_employees_when_get_employees_under_the_company() throws Exception {
        client.perform(MockMvcRequestBuilders.get("/companies/1/employees"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Jacky"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Michael"));
    }

    @Test
    void should_return_company_when_get_by_id() throws Exception {

        client.perform(MockMvcRequestBuilders.get("/companies/1"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("afs"));
    }

    @Test
    void should_return_created_company() throws Exception {
        //Given
        var givenName = "New Company";
        String requestBody = String.format("{\"name\": \"%s\" }", givenName);

        // When
        // Then
        client.perform(
                MockMvcRequestBuilders.post("/companies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(givenName));
    }

//    @Test
//    void should_return_updated_company_when_update_with_id_and_data() throws Exception {
//        String nameToUpdate = "NewName";
//        String requestBody = String.format("{\"name\": \"%s\" }", nameToUpdate);
//
//        // When
//        // Then
//        client.perform(MockMvcRequestBuilders.put("/companies/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestBody))
//            .andExpect(MockMvcResultMatchers.status().isOk())
//            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(nameToUpdate));
//    }
//
}
