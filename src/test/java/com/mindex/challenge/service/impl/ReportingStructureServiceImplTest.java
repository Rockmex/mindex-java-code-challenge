package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {
    private String reportStructureEmployeeIdUrl;

    @Autowired
    private EmployeeRepository employeeRepository;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        reportStructureEmployeeIdUrl = "http://localhost:" + port + "/reportingStructure/{id}";
    }

    /*  To create multiple test cases:
        I tried ParameterizedTest with MethodSource, but was not successful.
        Therefore, I approached it in a more naive way -- using list and loop.
     */
    @Test
    public void testRead(){
        for (Pair<String, Integer> idReportPair : expectedIdReportPairs()) {
            String testEmployeeId = idReportPair.getKey();
            int expectedNumOfReports = idReportPair.getValue();
            ReportingStructure readReportingStructure = restTemplate.getForEntity(reportStructureEmployeeIdUrl, ReportingStructure.class, testEmployeeId).getBody();
            assert readReportingStructure != null;
            assertEmployeeEquivalence(employeeRepository.findByEmployeeId(testEmployeeId), readReportingStructure.getEmployee());
            assertEquals(expectedNumOfReports, readReportingStructure.getNumberOfReports());
        }
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    // Creating multiple test cases for testRead().
    private List<Pair<String, Integer>> expectedIdReportPairs() {
        return Arrays.asList(
                new Pair<>("16a596ae-edd3-4847-99fe-c4518e82c86f", 4),
                new Pair<>("03aa1462-ffa9-4978-901b-7c001562cf6f", 2),
                new Pair<>("c0c2293d-16bd-4603-8e08-638a9d18b22c", 0)
        );
    }

}
