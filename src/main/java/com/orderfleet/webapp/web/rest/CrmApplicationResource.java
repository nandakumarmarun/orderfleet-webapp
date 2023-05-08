package com.orderfleet.webapp.web.rest;
import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.async.event.EventProducer;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.api.dto.keycloakTokenRespose;
import com.orderfleet.webapp.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Controller
@RequestMapping(value = "/web")
public class CrmApplicationResource {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.keycloak.client-id}")
    private String userClientId;

    @Value("${user.keycloak.realm}")
    private String userRealm;

    @Value("${user.keycloak.client_secret}")
    private String userClientSecret;

    @Value("${user.keycloak.url}")
    private String keycloakIp;
    @Value("${user.keycloak.grant_type}")
    private String userGrantType;



    @Autowired
    private EventProducer eventProducer;
    private final Logger log = LoggerFactory.getLogger(CrmApplicationResource.class);

    @GetMapping("/mod-c/features-enable")
    @Secured(AuthoritiesConstants.SITE_ADMIN)
    @Timed
    public String modCFeaturesEnable(Model model)throws URISyntaxException {
        model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
        return "site_admin/mod-c-feature-enable";
    }

    @RequestMapping(value = "/mod-c/user-list/{companypid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Timed
    public List<UserDTO> getAllUsersByCompany(@Valid @PathVariable("companypid") String companypid) throws URISyntaxException {
        log.debug("Web request to Get Users by companyPid: {}", companypid);
        List<UserDTO> users=userService.findAllByCompanyPid(companypid);
        return users;
    }

    @RequestMapping(value = "/mod-c/enable-feature", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Timed
    public ResponseEntity<List<UserDTO>> featuresEnable(@RequestParam("userPid") List<String> userPid,@RequestParam("status") boolean status) {
        System.out.println("list size"+userPid.size());
        List<UserDTO> users = updateUserEnableModcFeature(userPid, status);
        System.out.println("produce user list size"+users.size());
        if (!users.isEmpty() && status){
            eventProducer.snrichUserSynk(users);
        }
        return ResponseEntity.ok().body(users);
    }
    @RequestMapping(value = "/mod-c/disable-feature", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Timed
    public ResponseEntity<List<UserDTO>> featuresDisable(@RequestParam("userPid") List<String> userPid,@RequestParam("status") boolean status) {
        System.out.println("list size"+userPid.size());
        List<UserDTO> users = updateUserEnableModcFeature(userPid, status);
        System.out.println("produce disable user list size"+users.size());
        if (!users.isEmpty() && !status){
            eventProducer.snrichUserDisable(users);
        }
        return ResponseEntity.ok().body(users);
    }
    @GetMapping("/crm")
    public RedirectView getCrmPage() {
        String login = SecurityUtils.getCurrentUserLogin();
        Optional<User> optionalUser = userRepository.findOneByLogin(login);
        ResponseEntity<keycloakTokenRespose> token = getKeycloakToken(optionalUser);
        if (token.getStatusCode().toString().equals("200")){
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://"+keycloakIp+":8090/#/login");
            builder.queryParam("token", token.getBody().access_token);
            RedirectView redirectView = new RedirectView(builder.toUriString());
            return redirectView;
        }else {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://"+keycloakIp+":8090/#/login");
            RedirectView redirectView = new RedirectView(builder.toUriString());
            return redirectView;
        }
    }
    public List<UserDTO> updateUserEnableModcFeature(List<String> userPid,boolean status) {
        List<User> users = userRepository.findByUserPidIn(userPid);
        List<UserDTO> result = new ArrayList<>();
        if(status){
            users.forEach(user -> {
                if (!user.getEnableModcFeature()){
                    UserDTO userDTO = new UserDTO(user.getPid(),user.getLogin(),user.getPassword(),user.getCompany().getLegalName(),user.getCompany().getId(),user.getCompany().getEmail(),user.getCompany().getAddress1(),user.getEmail(),user.getFirstName(),user.getLastName());
                    result.add(userDTO);
                }
            });
        }
        if(!status){
            users.forEach(user -> {
                if (user.getEnableModcFeature()){
                    UserDTO userDTO = new UserDTO(user.getPid(),user.getLogin(),user.getPassword(),user.getCompany().getLegalName(),user.getCompany().getId(),user.getCompany().getEmail(),user.getCompany().getAddress1(),user.getEmail(),user.getFirstName(),user.getLastName());
                    result.add(userDTO);
                }
            });
        }
        return result;
    }
    public void updateUserStatus(List<String> userPid,boolean status){
        System.out.println("change status"+status);
        List<User> users = userRepository.findByUserPidIn(userPid);
        users.forEach(user -> {
            user.setEnableModcFeature(status);
        });
        users.forEach(data -> System.out.println("change status"+data.getEnableModcFeature()));
        userRepository.save(users);
    }
    public ResponseEntity<keycloakTokenRespose> getKeycloakToken(Optional<User> optionalUser){
        if (optionalUser.isPresent()){
            System.out.println("getToken");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String,String> multiValueMap= new LinkedMultiValueMap<>();
            multiValueMap.add("client_id",userClientId);
            multiValueMap.add("client_secret",userClientSecret);
            multiValueMap.add("grant_type",userGrantType);
            multiValueMap.add("username",optionalUser.get().getLogin());
            multiValueMap.add("password",optionalUser.get().getPassword());
            HttpEntity<MultiValueMap<String,String>> httpEntity = new HttpEntity<>(multiValueMap,headers);
            ResponseEntity<keycloakTokenRespose> result = restTemplate.postForEntity("http://"+keycloakIp+":8080/realms/"+userRealm+"/protocol/openid-connect/token", httpEntity, keycloakTokenRespose.class);
            log.debug("keycloak tocke ========"+result.getBody().getAccess_token()+"===="+result.getStatusCode());
            return result;
        }
        return null;
    }
    @ExceptionHandler({Exception.class, BadRequestAlertException.class})
    public ResponseEntity<String> ExceptionController(Exception ex)
    {
        log.debug("Catching Exception");
        return ResponseEntity.badRequest().body("Page Not Found Please Contact Administrator"+" ("+ ex.getMessage()+")");
    }
}
