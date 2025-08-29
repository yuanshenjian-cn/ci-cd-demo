package com.oocl.springbootemployee.repository;

import com.oocl.springbootemployee.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICompanyRepository extends JpaRepository<Company, Integer> {
    Optional<Company> findCompanyById(Integer id);

    Company save(Company company);

    Page<Company> findAll(Pageable pageable);
}
