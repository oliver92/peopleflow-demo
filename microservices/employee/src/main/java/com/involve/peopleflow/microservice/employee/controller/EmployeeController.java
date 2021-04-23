package com.involve.peopleflow.microservice.employee.controller;

import com.arangodb.springframework.core.ArangoOperations;
import com.involve.peopleflow.microservice.employee.model.Employee;
import com.involve.peopleflow.microservice.employee.model.EmployeeState;
import com.involve.peopleflow.microservice.employee.repositories.EmployeeRepository;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Created by olivertasevski on April 22, 2021
 */
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RestController
public class EmployeeController {

    private final static Logger logger = LogManager.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeRepository repository;

    @ApiOperation(value = "Add Employee", response = boolean.class)
    @PostMapping(value = "/addEmployee")
    public String addEmployee(@NotNull @RequestBody Employee request) {
        logger.info("Add Employee Request on method addEmployee: " + request.toString());

        String employeeId;
        try {
            Employee employee = repository.save(request);
            employeeId = employee.getId();
        } catch (Exception e) {
            String message = "Failed to addEmployee with name: " + request.getName() + " due to: " + e.getMessage();
            logger.warn(message);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }

        logger.info("Add Employee Response for employee with name: " + request.getName());
        return employeeId;
    }

    @ApiOperation(value = "Set Employee State", response = boolean.class)
    @PostMapping(value = "/setEmployeeState/{id}")
    public boolean setEmployeeState(@NotNull @PathVariable String id, @NotNull @RequestBody EmployeeState state) {
        logger.info("Set Employee State Request on method setEmployeeState: " + id + ", state: " + state.toString());

        try {
            final Optional<Employee> findEmployee = repository.findById(id);
            assert findEmployee.isPresent();

            Employee employee = findEmployee.get();
            employee.setState(state);
            repository.save(employee);
        } catch (Exception e) {
            String message = "Failed to setEmployeeState with id: " + id + " due to: " + e.getMessage();
            logger.warn(message);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }

        logger.info("Set Employee State Response for employee with id: " + id);
        return true;
    }
}
