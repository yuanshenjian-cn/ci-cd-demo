package com.oocl.springbootemployee.service;

import com.oocl.springbootemployee.model.Company;
import com.oocl.springbootemployee.model.Employee;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ICompanyService {
    Company getCompanyById(Integer id);

    Company creat(Company company);

    Page<Company> getAllByPageSize(Integer pageIndex, Integer pageSize);

    Company updateCompany(Integer id, Company company) throws Exception;

    List<Employee> getEmployeesByCompanyId(Integer id);
}
