package com.oocl.springbootemployee.dto.mapper;

import com.oocl.springbootemployee.dto.CompanyRequest;
import com.oocl.springbootemployee.dto.CompanyResponse;
import com.oocl.springbootemployee.model.Company;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompanyMapper {
    private EmployeeMapper employeeMapper;

    public CompanyMapper(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    public Company toEntity(CompanyRequest companyRequest) {
        Company company = new Company();
        company.setName(companyRequest.getName());
        if (companyRequest.getEmployeeRequests() != null){
            company.setEmployees(
                    companyRequest.getEmployeeRequests().stream().map(
                            employeeRequest -> employeeMapper.toEntity(employeeRequest)
                    ).collect(Collectors.toList())
            );
        }
        return company;
    }

    public CompanyResponse toResponse(Company company) {
        CompanyResponse companyResponse = new CompanyResponse();
        companyResponse.setId(company.getId());
        companyResponse.setName(company.getName());
        companyResponse.setEmployeeResponses(
                company.getEmployees().stream().map(
                        employee -> employeeMapper.toResponse(employee)).collect(Collectors.toList()
                )
        );
        return companyResponse;
    }


    public List<CompanyResponse> toResponse(List<Company> companies) {
        return companies.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
