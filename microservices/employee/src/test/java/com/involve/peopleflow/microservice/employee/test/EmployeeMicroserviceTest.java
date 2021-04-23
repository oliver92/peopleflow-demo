package com.involve.peopleflow.microservice.employee.test;

import com.involve.peopleflow.microservice.employee.model.Employee;
import com.involve.peopleflow.microservice.employee.model.EmployeeState;
import com.involve.peopleflow.microservice.employee.repositories.EmployeeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by olivertasevski on April 23, 2021
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmployeeMicroserviceTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EmployeeRepository repository;

    @Test
    public void test_addEmployee_changeState() throws Exception {

        Employee employee = new Employee(
            "Oliver",
            EmployeeState.ADDED,
            new Date(),
            "0123456789",
            "test@test.com",
            "IT",
            "Software Engineer"
        );

        //Add employee
        MvcResult result = mvc.perform(post("/addEmployee").content(employee.toString())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN)).andReturn();

        String employeeId = result.getResponse().getContentAsString();

        //Change the status
        mvc.perform(post("/setEmployeeState/" + employeeId).content("\"IN_CHECK\"")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(content().string("true"));

        //Delete that employee from db
        repository.deleteById(employeeId);
    }
}
