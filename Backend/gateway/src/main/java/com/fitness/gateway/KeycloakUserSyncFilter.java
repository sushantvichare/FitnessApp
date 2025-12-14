package com.fitness.gateway;

import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {


    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        String userId = serverWebExchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token = serverWebExchange.getRequest().getHeaders().getFirst("Authorization");

        RegisterRequest registerRequest = getUserDetails(token);

        if(userId == null){
            userId = registerRequest.getKeycloakId();
        }

        if (userId != null && token != null) {
            String finalUserId = userId;
            String finalUserId1 = userId;
            return userService.validateUser(userId)
                    .flatMap(exists -> {
                        if (!exists) {
                            //Register User

                            if (registerRequest != null) {
                                return userService.registerUser(registerRequest)
                                        .then(Mono.empty());
                            }else {
                            return Mono.empty();}
                        }else {
                            log.info("User {} already exists", finalUserId1);
                            return Mono.empty();
                        }
                    })
                    .then(Mono.defer(()->{
                        ServerHttpRequest mutatedRequest = serverWebExchange.getRequest().mutate()
                                .header("X-User-ID", finalUserId).build();
                        return webFilterChain.filter(serverWebExchange.mutate().request(mutatedRequest).build());
                    }));
        }
        return webFilterChain.filter(serverWebExchange);
    }

    private RegisterRequest getUserDetails(String token){
        try {
            String tokenWithoutBearer = token.replace("Bearer ", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setEmail(jwtClaimsSet.getStringClaim("email"));
            registerRequest.setFirstName(jwtClaimsSet.getStringClaim("given_name"));
            registerRequest.setLastName(jwtClaimsSet.getStringClaim("family_name"));
            registerRequest.setPassword("dummy@123");
            registerRequest.setKeycloakId(jwtClaimsSet.getStringClaim("sub"));

            return registerRequest;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
