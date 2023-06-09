package com.example.demo.dto;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PeopleDTO {
    private String name;
    private String lastname;
    private String address;
    private String birth;
    private String phone;
    private MultipartFile photo;
}
