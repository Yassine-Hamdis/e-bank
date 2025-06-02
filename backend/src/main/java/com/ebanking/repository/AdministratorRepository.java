package com.ebanking.repository;

import com.ebanking.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Administrator entity
 * Provides CRUD operations and custom queries for Administrator management
 */
@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {

    /**
     * Find administrators by admin level
     * @param adminLevel the admin level to search for
     * @return list of administrators with the specified admin level
     */
    List<Administrator> findByAdminLevel(String adminLevel);

    /**
     * Find administrators by department
     * @param department the department to search for
     * @return list of administrators in the specified department
     */
    List<Administrator> findByDepartment(String department);

    /**
     * Find administrators by admin level and department
     * @param adminLevel the admin level
     * @param department the department
     * @return list of administrators with the specified admin level and department
     */
    List<Administrator> findByAdminLevelAndDepartment(String adminLevel, String department);

    /**
     * Count administrators by admin level
     * @param adminLevel the admin level
     * @return count of administrators with the specified admin level
     */
    long countByAdminLevel(String adminLevel);

    /**
     * Count administrators by department
     * @param department the department
     * @return count of administrators in the specified department
     */
    long countByDepartment(String department);
}
