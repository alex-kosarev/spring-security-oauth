package pro.akosarev.sandbox.controller;

import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.reactive.function.client.WebClient;
import pro.akosarev.sandbox.presentation.Greetings;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Controller
public class GreetingsController {

    private final WebClient webClient;

    public GreetingsController(ReactiveClientRegistrationRepository clientRegistrationRepository,
                               ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction filter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                clientRegistrationRepository, authorizedClientRepository);
        filter.setDefaultClientRegistrationId("greetings-app-authorization-code");
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8083")
                .filter(filter)
                .build();
    }

    @ModelAttribute("principal")
    public Mono<Principal> principal(Mono<Principal> principalMono) {
        return principalMono;
    }

    @GetMapping("/")
    public Mono<String> getGreetingsPage(Model model) {
        return this.webClient.get()
                .uri("/greetings-api/greetings")
                .retrieve()
                .bodyToMono(Greetings.class)
                .doOnNext(greetings -> model.addAttribute("greetings", greetings))
                .thenReturn("greetings");
    }
}
