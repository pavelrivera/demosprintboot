package com.example.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.entity.People;
import com.example.demo.service.PeopleService;


import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/people")
public class PeopleController {

    private final PeopleService peopleservice;

    public PeopleController(PeopleService peopleservice){
        this.peopleservice=peopleservice;
    }

    @PostMapping("/create")
    public People SavePeople(@RequestParam("name")String name, @RequestParam("lastname")String lastname,
                            @RequestParam("birth")Date birth, @RequestParam("address")String address,
                            @RequestParam("phone")String phone, @RequestParam(value = "photo", required = false) MultipartFile photo){
        People newpeople= new People();
        newpeople.setName(name);
        newpeople.setLastname(lastname);
        newpeople.setBirth(birth);
        newpeople.setAddress(address);
        newpeople.setPhone(phone);

        String prenameimg=phone+"_"+name;
        if(peopleservice.savephoto(photo, prenameimg))                        
            newpeople.setPhoto(prenameimg+"_"+photo.getOriginalFilename());

        return peopleservice.SavePeople(newpeople);
    }

    @GetMapping("/{id}")
    public People getPeopleById(@PathVariable Long id) {
        return peopleservice.getPeopleById(id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseDTO deletePeopleById(@PathVariable("id") Long id) throws IOException
    {
        peopleservice.deletePeopleById(id);
        return new ResponseDTO("Deleted Successfully");
    }
    
    @GetMapping("/")
    public List<People> getAllPeople() {
        return peopleservice.getAllPeople();
    }

    @PutMapping("/update/{id}")
    public People updatePeople(@RequestParam("name")String name, @RequestParam("lastname")String lastname,
                                @RequestParam("birth")Date birth, @RequestParam("address")String address,
                                @RequestParam("phone")String phone, @RequestParam(value = "photo", required = false) MultipartFile photo, 
                                @PathVariable("id") Long id){
        People newpeople= peopleservice.getPeopleById(id);

        newpeople.setName(name);
        newpeople.setLastname(lastname);
        newpeople.setBirth(birth);
        newpeople.setAddress(address);
        newpeople.setPhone(phone);

        String prenameimg=phone+"_"+name;
        if(peopleservice.savephoto(photo, prenameimg))                        
            newpeople.setPhoto(prenameimg+"_"+photo.getOriginalFilename()); 

        return peopleservice.updatePeople(newpeople, id);
    }

    @GetMapping("/peoplephoto/{id}")
    public ResponseEntity<?> getPhotoPeopleById(@PathVariable Long id){
        return peopleservice.getPhotoPeopleById2(id);

    }

    @PostMapping("/SearchByNameAndAddress")
    public List<People> SavePeople(@RequestParam String name, @RequestParam String address){
        return peopleservice.getAllPeopleByNameAndAddress(name, address);
    }

    @PostMapping("/SearchByRangeAge")
    public List<People> SearchByRangeAge(@RequestParam String from, @RequestParam String to){
        int afrom= from.equals("")?0:Integer.parseInt(from);
        int ato= to.equals("")?0:Integer.parseInt(to);
        return peopleservice.getAllPeopleByRangAge(afrom, ato);
    }
    

}
