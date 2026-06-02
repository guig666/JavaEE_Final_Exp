package com.konsonx.controller.client.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Client {
    @RequestMapping({"/client", "/client/**"})
    public String clientPage(){
        return "client/index";
    }
}
