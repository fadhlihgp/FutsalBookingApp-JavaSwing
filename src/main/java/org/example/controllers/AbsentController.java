package org.example.controllers;

import org.example.dtos.AbsentResponse;
import org.example.models.Absent;
import org.example.services.AbsentService;

import java.util.List;

public class AbsentController {
    private AbsentService service;
    public AbsentController (AbsentService service) {
        this.service = service;
    }
    public List<String> getIdNameEmployees(){
        return service.getIdNameEmployees();
    }
    public Absent createAbsent(Absent absent){
        return service.createAbsent(absent);
    }
    public List<AbsentResponse> findAllAbsent(){
        return service.findAllAbsent();
    }
    public List<AbsentResponse> findAllByDate(int date, int year){
        return service.findAllAbsentByDate(date, year);
    }
    public List<AbsentResponse> findAllAbsentDetails(){
        return service.findAllAbsentDetails();
    }
    public List<AbsentResponse> findAllAbsentDetailByDate(int date, int year){
        return service.findAllAbsentDetailByDate(date, year);
    }
}
