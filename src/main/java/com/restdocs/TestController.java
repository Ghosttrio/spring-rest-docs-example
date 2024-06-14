package com.restdocs;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping
    public Test test(@RequestParam String req,
                     @RequestHeader(value = "test") String test){
        Contact contact = new Contact();
        contact.setName("test1");
        contact.setEmail("test2");

        Test testr = new Test();
        testr.setContact(contact);
        return testr;
    }

    @GetMapping("/head")
    public void head(@RequestHeader(value = "test") String test){
    }
}
