package com.oocl.springbootemployee.service;

import com.oocl.springbootemployee.exception.CompanyNotFoundException;
import com.oocl.springbootemployee.model.Company;
import com.oocl.springbootemployee.model.Employee;
import com.oocl.springbootemployee.repository.ICompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService implements ICompanyService {
    private ICompanyRepository companyRepository;

    public CompanyService(ICompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Company getCompanyById(Integer id) {
        return companyRepository.findCompanyById(id).get();
    }

    @Override
    public Company creat(Company company) {
        return companyRepository.save(company);
    }

    @Override
    public Page<Company> getAllByPageSize(Integer pageIndex, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return companyRepository.findAll(pageable);
    }

    @Override
    public Company updateCompany(Integer id, Company company) throws Exception {
        Company repositoryCompany = companyRepository.findCompanyById(id).orElseThrow(
                () -> new Exception("this company is not exist!")
        );
        if (company.getName() != null) {
            repositoryCompany.setName(company.getName());
        }
        return companyRepository.save(repositoryCompany);
    }

    @Override
    public List<Employee> getEmployeesByCompanyId(Integer id) {
        Company company = companyRepository.findCompanyById(id).orElseThrow(
                () -> new CompanyNotFoundException()
        );
        return company.getEmployees();
    }
}
