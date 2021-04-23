package com.involve.peopleflow.microservice.employee.model;

import com.arangodb.springframework.annotation.ArangoId;
import com.arangodb.springframework.annotation.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by olivertasevski on April 22, 2021
 */
@Data
@ApiModel(description = "Employee")
@Document("employee")
public class Employee {

    @Id
    @ApiModelProperty(hidden = true)
    private String id;
    @ApiModelProperty("Name of the Employee")
    @NotNull
    private String name;
    @ApiModelProperty(value = "State of the Employee", notes = "Possible values: ADDED,IN_CHECK,APPROVED,ACTIVE")
    private EmployeeState state;
    @ApiModelProperty("Date of Birth of the Employee")
    private Date dateOfBirth;
    @ApiModelProperty("Phone Number of the Employee")
    private String phoneNumber;
    @ApiModelProperty("Business Email of the Employee")
    private String email;
    @ApiModelProperty("Department of the Employee")
    private String department;
    @ApiModelProperty("Position of the Employee")
    private String position;


    //this can be avoided using lomboc`s constructor configuration and have just the state be set as default if its empty
    public Employee(@NotNull String name, EmployeeState state, Date dateOfBirth, String phoneNumber, String email, String department, String position) {
        this.name = name;
        this.state = state != null ? state : EmployeeState.ADDED;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.department = department;
        this.position = position;
    }

    public String toString() {
        try {
            ObjectWriter ow = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(this);
        } catch (Exception e) {
            // e.g. JSON exception or jackson not found
            return super.toString();
        }
    }
}
