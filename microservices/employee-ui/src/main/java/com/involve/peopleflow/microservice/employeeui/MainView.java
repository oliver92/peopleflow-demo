package com.involve.peopleflow.microservice.employeeui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and
 * use @Route annotation to announce it in a URL as a Spring managed
 * bean.
 * Use the @PWA annotation make the application installable on phones,
 * tablets and some desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */
@Route
@PWA(name = "Spring Boot Demo App",
        shortName = "Spring Boot Demo App",
        description = "Spring Boot Demo App using microservices.",
        enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {

    @Value("${EMPLOYEE_HOST:localhost}")
    private String employeeHost;

    @Value("${EMPLOYEE_PORT:8081}")
    private String employeePort;

    public MainView() {
        VerticalLayout addEmployeeLayout = buildAddEmployeeLayout();
        VerticalLayout setEmployeeState = buildUpdateEmployeeStateLayout();

        addEmployeeLayout.setSpacing(false);
        setEmployeeState.setSpacing(false);

        HorizontalLayout layout = new HorizontalLayout(addEmployeeLayout, setEmployeeState);
        layout.setClassName("centered-content");
        add(layout);
    }

    private VerticalLayout buildAddEmployeeLayout() {
        Span title = new Span();
        title.setText("Add Employee");
        title.addClassName("title");

        TextField nameField = new TextField("Name");
        DatePicker dateOfBirth = new DatePicker("Date of Birth");
        TextField phoneNumber = new TextField("Phone Number");
        EmailField email = new EmailField("Email");
        TextField department = new TextField("Department");
        TextField position = new TextField("Position");
        Span responseHolder = new Span();

        Button button = new Button("Add", e -> {

            Map<String, Object> request = new HashMap();
            request.put("name", nameField.getValue());
            request.put("dateOfBirth", Date.from(dateOfBirth.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            request.put("phoneNumber", phoneNumber.getValue());
            request.put("email", email.getValue());
            request.put("department", department.getValue());
            request.put("position", position.getValue());

            String response = sendRequest("http://" + employeeHost + ":" + employeePort + "/addEmployee", request, true);

            responseHolder.setText("Added Employee ID: " + response);
        });
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickShortcut(Key.ENTER);
        button.setClassName("submit-btn");

        return new VerticalLayout(title, nameField, dateOfBirth, phoneNumber, email, department, position, button, responseHolder);
    }

    private VerticalLayout buildUpdateEmployeeStateLayout() {
        Span title = new Span();
        title.setText("Change Employee State");
        title.addClassName("title");

        TextField textField = new TextField("Employee ID");
        Select<String> select = new Select<>();
        select.setLabel("State");
        select.setItems("ADDED", "IN_CHECK", "APPROVED", "ACTIVE");
        select.setValue("APPROVED");
        Span responseHolder = new Span();

        Button button = new Button("Update", e -> {

            Map<String, Object> request = new HashMap();
            request.put("state", select.getValue());

            String response = sendRequest("http://" + employeeHost + ":" + employeePort + "/setEmployeeState/" + textField.getValue(), request, false);

            responseHolder.setText("Changed State: " + response);
        });
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickShortcut(Key.ENTER);
        button.setClassName("submit-btn");

        return new VerticalLayout(title, textField, select, button, responseHolder);
    }

    private String sendRequest(String url, Map<String, Object> request, boolean isJSON) {
        String response;
        try {
            response = doPost(url, isJSON
                    ? new ObjectMapper().writeValueAsString(request)
                    : ("\"" + request.values().stream().map(Object::toString).collect(Collectors.joining(""))) + "\"");
        } catch(Exception e) {
            e.printStackTrace();
            response = e.getMessage();
        }

        Notification notification = new Notification(response.matches("[0-9]+") || response.equals("true") ? "Success" : "Error: " + response);
        notification.addThemeVariants(response.matches("[0-9]+") || response.equals("true") ? NotificationVariant.LUMO_SUCCESS : NotificationVariant.LUMO_ERROR);
        notification.open();

        return response;
    }

    private String doPost(String url, String body) throws Exception {
        System.out.println("Sending request to: " + url + ", with body: " + body);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        OutputStream os = connection.getOutputStream();
        os.write(body.getBytes(StandardCharsets.UTF_8));
        os.close();

        StringBuilder response = new StringBuilder();
        InputStreamReader in = new InputStreamReader(connection.getInputStream());
        BufferedReader br = new BufferedReader(in);
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        in.close();
        return response.toString();
    }
}
