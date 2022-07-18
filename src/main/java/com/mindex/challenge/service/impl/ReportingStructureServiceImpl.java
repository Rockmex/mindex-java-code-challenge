package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {
    private final static Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure read(String id) {
        LOG.debug("Creating reportingStructure with id [{}]", id);
        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        int reports = countReports(employee);
        ReportingStructure reportingStructure = new ReportingStructure(employee, reports);

        return reportingStructure;
    }

    // Count number of reports by level (BFS)
    private int countReports(Employee employee) {
        Queue<Employee> queue = new LinkedList<>();
        queue.offer(employee);
        int reports = 0;
        while (!queue.isEmpty()) {
            Employee currEmployee = queue.poll();
            List<Employee> directReportsList = currEmployee.getDirectReports();
            if (directReportsList != null) {
                for (Employee reportEmployee : directReportsList) {
                    // Employee object in directReports only offers the id of employees. To get sub-level employees' directReports, we have to get employee again.
                    queue.offer(employeeRepository.findByEmployeeId(reportEmployee.getEmployeeId()));
                    reports++;
                }
            }
        }
        System.out.println("Reports for employee: " + employee.getFirstName() + " " + employee.getLastName() + ": " + reports);
        return reports;
    }
}
