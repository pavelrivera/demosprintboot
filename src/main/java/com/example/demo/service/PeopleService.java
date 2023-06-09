package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;



import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.People;
import com.example.demo.repository.PeopleRepository;

import lombok.AllArgsConstructor;


@AllArgsConstructor
@Service
public class PeopleService {
    private final PeopleRepository peopleRepository;


    public People SavePeople(People people){
        return peopleRepository.save(people);
    }
    
    public People getPeopleById(Long id){
        People people = new People();
        people = peopleRepository.findById(id).get();
        return people;
    }

    public List<People> getAllPeople(){
        return peopleRepository.findAll();
    }

    public void deletePeopleById(Long id) throws IOException{
        People peopledb = peopleRepository.findById(id).get();
        if(peopledb.getPhoto()!=null)
        {
            Path path = Paths.get("src//main//resources//static//images").resolve(peopledb.getPhoto());
            File thedir= new File(path.toString());
            if(thedir.exists())
                Files.delete(path);
        }
         
        peopleRepository.deleteById(id);
    }

    public People updatePeople(People people, Long id){
        People peopledb = peopleRepository.findById(id).get();
 
        if (Objects.nonNull(people.getName()) && !"".equalsIgnoreCase(peopledb.getName())) 
        {
            peopledb.setName(people.getName());
        }

        if (Objects.nonNull(people.getBirth()) && !people.getBirth().equals(peopledb.getBirth())) 
        {
            peopledb.setBirth(people.getBirth());
        }

        if (Objects.nonNull(people.getLastname()) && !"".equalsIgnoreCase(peopledb.getLastname())) 
        {
            peopledb.setLastname(people.getLastname());
        }

        if (Objects.nonNull(people.getPhone()) && !"".equalsIgnoreCase(peopledb.getPhone())) 
        {
            peopledb.setPhone(people.getPhone());
        }

        if (Objects.nonNull(people.getAddress()) && !"".equalsIgnoreCase(peopledb.getAddress())) 
        {
            peopledb.setAddress(people.getAddress());
        }

        return peopleRepository.save(peopledb);
    }

    public List<People> getAllPeopleByNameAndAddress(String name, String address){
        List<People> peoplesdb = peopleRepository.findAll();
        if(!name.equals("") || !address.equals("") ){
            List<People> newpeoples = new ArrayList<People>();
            for (People objpeople: peoplesdb) {
                if(!name.equals("") && !address.equals("") ){
                    if (objpeople.getName().toLowerCase().contains(name.toLowerCase()) && objpeople.getAddress().toLowerCase().contains(address.toLowerCase())) {
                        newpeoples.add(objpeople);
                    }
                }
                else{
                    if(!name.equals("") && address.equals("") ){
                        if (objpeople.getName().toLowerCase().contains(name.toLowerCase())) {
                            newpeoples.add(objpeople);
                        }
                    }
                    else{
                        if (objpeople.getAddress().toLowerCase().contains(address.toLowerCase())) {
                            newpeoples.add(objpeople);
                        }
                    }
                }
            }
            return newpeoples;
        }
        else
            return peoplesdb;
    }

    public List<People> getAllPeopleByRangAge(int from, int to){
        List<People> peoplesdb = peopleRepository.findAll();
        List<People> newpeoples = new ArrayList<People>();
        if(from==0 && to==0){
            return peoplesdb;
        }
        else{
            //DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate now = LocalDate.now();
            for (People objpeople: peoplesdb) {
                //LocalDate birthday = LocalDate.parse(objpeople.getBirth().toString(), fmt);
                LocalDate birthday =objpeople.getBirth().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
                Period period = Period.between(birthday, now);
                if(from >0 && to>0){
                    if(from<=period.getYears() && to>=period.getYears()){
                        newpeoples.add(objpeople);
                    }
                }
                else{
                    if(from>0 && to==0 )
                    {
                        if(from<=period.getYears()){
                            newpeoples.add(objpeople);
                        }
                    }
                    else{
                        if(to>=period.getYears()){
                            newpeoples.add(objpeople);
                        }
                    }
                } 
        }
        }
        return newpeoples;
    }

    public boolean savephoto(MultipartFile photo, String prename){
        if(photo!=null)
        {
            Path directorioimagenes = Paths.get("src//main//resources//static//images");
            String rutaAbsoluta= directorioimagenes.toFile().getAbsolutePath();
            try {
                byte[] bytesimg=photo.getBytes();
                String nameimg=prename+"_"+photo.getOriginalFilename();
                Path rutaCompleta=Paths.get(rutaAbsoluta+"//"+nameimg);
                Files.write(rutaCompleta, bytesimg); 
            } catch (IOException e) {
               e.printStackTrace();
               return false;
            }
            return true;
        }
        else
            return false;
    }

    public ResponseEntity<?> getPhotoPeopleById2(Long id){
        People peopledb = peopleRepository.findById(id).get();
        if(peopledb.getPhoto()!=null)
        {
            Resource resource = null;
            try {
                String filename= peopledb.getPhoto();
                Path file = Paths.get("src//main//resources//static//images").resolve(filename);
                File theDir = new File(file.toString());
                if(theDir.exists())
                    resource = new UrlResource(file.toUri());
            } catch (IOException e) {
                return ResponseEntity.internalServerError().build();
            }
            
            if (resource == null) {
                return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
            }
            
            String contentType = "application/octet-stream";
            String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .body(resource);
        }
        return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
    }

}
