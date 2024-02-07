package pro.akosarev.sandbox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Controller
@RequestMapping("user")
public class UserController {

    @ModelAttribute("principal")
    public Mono<Principal> principal(Mono<Principal> principalMono) {
        return principalMono;
    }

    @GetMapping
    public String getUserPage() {
        return "user";
    }
}
