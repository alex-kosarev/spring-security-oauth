package pro.akosarev.sandbox.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestClient;
import pro.akosarev.sandbox.presentation.Greetings;

import java.security.Principal;

@Controller
public class GreetingsController {

    private final RestClient restClient;

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public GreetingsController(ClientRegistrationRepository clientRegistrationRepository,
                               OAuth2AuthorizedClientRepository authorizedClientRepository) {
        this.authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientRepository);
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8081")
                .requestInterceptor((request, body, execution) -> {
                    if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                        var token = this.authorizedClientManager.authorize(
                                        OAuth2AuthorizeRequest.withClientRegistrationId("greetings-app-authorization-code")
                                                .principal(SecurityContextHolder.getContext().getAuthentication())
                                                .build())
                                .getAccessToken().getTokenValue();

                        request.getHeaders().setBearerAuth(token);
                    }

                    return execution.execute(request, body);
                })
                .build();
    }

    @ModelAttribute("principal")
    public Principal principal(Principal principal) {
        return principal;
    }

    @GetMapping("/")
    public String getGreetingsPage(Model model) {
        model.addAttribute("greetings",
                this.restClient.get()
                        .uri("/greetings-api/greetings")
                        .retrieve()
                        .body(Greetings.class));
        return "greetings";
    }
}
