package com.lxh.uaa.rest;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserResource {

    // http://localhost:8080/api/greeting
    @GetMapping("/greeting")
    public String greeting(){
        return "Hello World";
    }

    @PostMapping("/greeting")
    public String greeting(String name){
        return "Hello World: "+ name;
    }

    @PutMapping("/greeting/{name}")
    public String putGreeting(@PathVariable String name){
        return "呀屎啦: "+ name;
    }

    @PostMapping("/entityGreeting")
    public String entityGreeting(@RequestParam String name,@RequestBody Profile profile){
        return "Hello World: "+ name + "!\n" + profile.gender;
    }

    @Data
    static class Profile{
        private String gender;
        private String idNo;
    }

}
