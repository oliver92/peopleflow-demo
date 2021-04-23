package com.involve.peopleflow.microservice.employee.repositories;

import com.arangodb.springframework.repository.ArangoRepository;
import com.involve.peopleflow.microservice.employee.model.Employee;

/**
 * Created by olivertasevski on April 22, 2021
 */
public interface EmployeeRepository extends ArangoRepository<Employee, String> {}
