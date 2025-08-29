package com.oocl.springbootemployee.controller;


import com.oocl.springbootemployee.dto.CompanyRequest;
import com.oocl.springbootemployee.dto.CompanyResponse;
import com.oocl.springbootemployee.dto.EmployeeResponse;
import com.oocl.springbootemployee.dto.mapper.CompanyMapper;
import com.oocl.springbootemployee.dto.mapper.EmployeeMapper;
import com.oocl.springbootemployee.model.Company;
import com.oocl.springbootemployee.model.Employee;
import com.oocl.springbootemployee.model.Gender;
import com.oocl.springbootemployee.service.ICompanyService;
import com.oocl.springbootemployee.service.IEmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final ICompanyService companyService;
    private final CompanyMapper companyMapper;
    private final EmployeeMapper employeeMapper;

    public CompanyController(ICompanyService companyService, CompanyMapper companyMapper, EmployeeMapper employeeMapper) {
        this.companyService = companyService;
        this.companyMapper = companyMapper;
        this.employeeMapper = employeeMapper;
    }

    @GetMapping("/{id}")
    public CompanyResponse getCompanyById(@PathVariable Integer id) {
        return companyMapper.toResponse(companyService.getCompanyById(id));
    }


    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompanyResponse addCompany(@RequestBody CompanyRequest companyRequest) {
        return companyMapper.toResponse(companyService.creat(companyMapper.toEntity(companyRequest)));
    }

    @PutMapping("/{id}")
    public CompanyResponse updateCompany(@PathVariable Integer id, @RequestBody CompanyRequest companyRequest) throws Exception {
        return companyMapper.toResponse(companyService.updateCompany(id, companyMapper.toEntity(companyRequest)));
    }


    @GetMapping(params = {"pageIndex", "pageSize"})
    public List<CompanyResponse> getAllByPageSize(@RequestParam Integer pageIndex, @RequestParam Integer pageSize){
        return companyMapper.toResponse(companyService.getAllByPageSize(pageIndex, pageSize).toList());
    }

    @GetMapping("/{id}/employees")
    public List<EmployeeResponse> getEmployeesByCompanyId(@PathVariable Integer id) {
        return employeeMapper.toResponse(companyService.getEmployeesByCompanyId(id));
    }
}
