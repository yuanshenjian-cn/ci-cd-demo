package com.oocl.springbootemployee.controller;


import com.oocl.springbootemployee.dto.EmployeeRequest;
import com.oocl.springbootemployee.dto.EmployeeResponse;
import com.oocl.springbootemployee.dto.mapper.EmployeeMapper;
import com.oocl.springbootemployee.model.Gender;
import com.oocl.springbootemployee.service.IEmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private EmployeeMapper employeeMapper;
    private final IEmployeeService employeeService;

    public EmployeeController(EmployeeMapper employeeMapper, IEmployeeService employeeService) {
        this.employeeMapper = employeeMapper;
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeResponse> getEmployeeList() {
        return employeeMapper.toResponse(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public EmployeeResponse getEmployeeById(@PathVariable Integer id) {
        return employeeMapper.toResponse(employeeService.getEmployeeById(id));
    }

    @GetMapping(params = {"gender"})
    public List<EmployeeResponse> getEmployeesByGender(@RequestParam Gender gender) {
        return employeeMapper.toResponse(employeeService.getEmployeesByGender(gender));
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public EmployeeResponse addEmployee(@RequestBody @Validated EmployeeRequest employee) {
        return employeeMapper.toResponse(employeeService.creat(employeeMapper.toEntity(employee)));
    }

    @PutMapping("/{id}")
    public EmployeeResponse updateEmployee(@PathVariable Integer id, @RequestBody EmployeeRequest employee) {
        return employeeMapper.toResponse(employeeService.updateEmployee(id, employeeMapper.toEntity(employee)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeEmployee(@PathVariable Integer id) {
        employeeService.removeEmployee(id);
    }

    @GetMapping(params = {"pageIndex", "pageSize"})
    public List<EmployeeResponse> getAllByPageSize(@RequestParam Integer pageIndex, @RequestParam Integer pageSize){
        return employeeMapper.toResponse(employeeService.getAllByPageSize(pageIndex, pageSize).toList());
    }
}
