package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {
    private String compensationUrl;
    private String compensationEmployeeIdUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationEmployeeIdUrl = "http://localhost:" + port + "/compensation/{id}";
    }

    @Test
    public void testCreateRead() {
        // Creating test objects
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");
        Employee createdEmployee = employeeService.create(testEmployee);

        Compensation testCompensation = new Compensation();
        testCompensation.setEmployee(createdEmployee);
        testCompensation.setSalary(150000);
        testCompensation.setEffectiveDate(new Date());

        // Test compensation create
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();
        assertNotNull(createdCompensation.getEmployee());
        assertCompensationEquivalence(testCompensation, createdCompensation);

        // Test compensation read
        Compensation readCompensation = restTemplate.getForEntity(compensationEmployeeIdUrl, Compensation.class, createdCompensation.getEmployee().getEmployeeId()).getBody();
        assertNotNull(readCompensation.getEmployee());
        assertCompensationEquivalence(createdCompensation, readCompensation);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEmployeeEquivalence(expected.getEmployee(), actual.getEmployee());
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    }
}
