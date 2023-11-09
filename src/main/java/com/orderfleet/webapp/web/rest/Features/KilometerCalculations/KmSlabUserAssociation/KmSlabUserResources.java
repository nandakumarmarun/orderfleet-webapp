package com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlabUserAssociation;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.web.rest.Features.KilometerCalculations.Exception.ErrorReponse;
import org.apache.poi.ss.formula.functions.T;
import org.checkerframework.checker.units.qual.K;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/web")
public class KmSlabUserResources {

    private final Logger log = LoggerFactory.getLogger(KmSlabUserResources.class);

    @Inject
    private KmSlabUserService kmSlabUserService;

    @RequestMapping(value = "/km-slab/km-slab-users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> GetAllAssocisation(@RequestParam("kmSlabPid") String kmSlabPid, HttpServletRequest request){
        log.debug("Get User KmSlab Associations.." + request.getContextPath() + request.getRequestURI());
        List<KmSlabUserDTO> Response = null;
        try {
            Response =  kmSlabUserService.findAllByCompanyIdUserPidInKmSlabPid(kmSlabPid);
            if(Response.isEmpty() && Response.size() == 0 && Response == null){
                return new ResponseEntity<>(new ErrorReponse("No Value Present",HttpStatus.NO_CONTENT,request.getRequestURI()),HttpStatus.NO_CONTENT);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new ErrorReponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR,request.getRequestURI()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(Response,HttpStatus.OK);
    }



    @RequestMapping(value = "/km-slab/km-slab-users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> saveAssocisation(@RequestParam("kmSlabPid") String kmSlabPid, @RequestParam List<String> userPids, HttpServletRequest request){
        log.debug("Saving User KmSlab Association..");
        List<KmSlabUserDTO> Response = null;
        try {
           Response =  kmSlabUserService.AssociateUserSlab(kmSlabPid,userPids);
            if(Response.isEmpty() && Response.size() == 0 && Response == null){
                return new ResponseEntity<>(new ErrorReponse("No Value Present",HttpStatus.NO_CONTENT,request.getRequestURI()),HttpStatus.NO_CONTENT);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new ErrorReponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR,request.getRequestURI()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(Response,HttpStatus.CREATED);
    }
}
