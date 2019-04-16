package com.orderfleet.webapp.web.rest.api;

import com.orderfleet.webapp.security.jwt.JWTConfigurer;
import com.orderfleet.webapp.security.jwt.TokenProvider;
import com.orderfleet.webapp.service.AuthenticationAuditEventService;
import com.orderfleet.webapp.web.rest.api.dto.LoginDTO;

import java.util.Collections;

import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserJWTController {

    private final Logger log = LoggerFactory.getLogger(UserJWTController.class);

    private final TokenProvider tokenProvider;

    private final AuthenticationManager authenticationManager;
    
    private final AuthenticationAuditEventService authenticationAuditEventService;

    public UserJWTController(TokenProvider tokenProvider, AuthenticationManager authenticationManager, AuthenticationAuditEventService authenticationAuditEventService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.authenticationAuditEventService = authenticationAuditEventService;
    }
    
    @PostMapping(value = "/authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity authorize(@Valid @RequestBody LoginDTO loginDTO,HttpServletRequest request, HttpServletResponse response) {

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        try {
            Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            boolean rememberMe = (loginDTO.getRememberMe() == null) ? false : loginDTO.getRememberMe();
            String jwt = tokenProvider.createToken(authentication, rememberMe);
            response.addHeader(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
            return ResponseEntity.ok(new JWTToken(jwt));
        } catch (AuthenticationException ae) {
            log.trace("Authentication exception trace: {}", ae);
            //save authentication log to database
            authenticationAuditEventService.save(loginDTO.getUsername(), loginDTO.getPassword(), request.getRemoteAddr());
            return new ResponseEntity<>(Collections.singletonMap("AuthenticationException",
                ae.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
        }
    }
    
}