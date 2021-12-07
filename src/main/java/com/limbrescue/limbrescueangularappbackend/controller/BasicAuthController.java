package com.limbrescue.limbrescueangularappbackend.controller;


import com.limbrescue.limbrescueangularappbackend.model.AuthenticationBean;
import org.springframework.web.bind.annotation.*;

//Port number 8081
@CrossOrigin(originPatterns = "*", methods = {RequestMethod.GET, RequestMethod.POST})
@RestController
@RequestMapping("/api/v1")
public class BasicAuthController {
    /**
     * Authorizes the login.
     *
     * @return
     *          The authentication bean.
     */
    @GetMapping(path = "/basicauth")
    public AuthenticationBean helloWorldBean() {
        return new AuthenticationBean("You are authenticated");
    }
}
