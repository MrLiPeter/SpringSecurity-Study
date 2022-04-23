package uaa.rest;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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

    @GetMapping("/principal")
    public Principal getPrincipal(Principal principal){
        return principal;
    }

    @GetMapping("/authentication")
    public Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }


    @Data
    static class Profile{
        private String gender;
        private String idNo;
    }

}
