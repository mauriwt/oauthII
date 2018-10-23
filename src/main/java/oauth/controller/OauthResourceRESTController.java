package oauth.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import oauth.model.OauthClientResource;
import oauth.repo.OauthClientResourceRepo;
import oauth.utilities.ErrorService;
import oauth.utilities.HttpResponseService;

@RestController
@CrossOrigin
@RequestMapping(value="yauth/resource")
public class OauthResourceRESTController {
  
  private Logger logger = LogManager.getLogger(OauthResourceRESTController.class);
  
  @Autowired
  private OauthClientResourceRepo resRepo;
  

  @PreAuthorize("hasAuthority('yauth.resource.get')")
  @RequestMapping(method = RequestMethod.GET)
  public List<OauthClientResource> getAllResources(@RequestParam(value="type", required=false) String tipo) {
    if(tipo != null && tipo.equals("F"))
      return this.resRepo.findAll();
    else
      return this.resRepo.findAllBasic();
  }
  
  @PreAuthorize("hasAuthority('yauth.resource.get')")
  @RequestMapping(value="{id}", method = RequestMethod.GET)
  public OauthClientResource getOneResource(@PathVariable("id") Integer id) {
      return this.resRepo.findOneBasic(id);
  }
  
  @PreAuthorize("hasAuthority('yauth.resource.insert')")
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<?> insertResource(@RequestBody @Valid OauthClientResource resource, Errors validation) {
    
    try {
      if(validation.hasErrors())
        throw new Exception(ErrorService.validacion(validation.getAllErrors()));
      
      this.resRepo.save(resource);
      
      return HttpResponseService.responseOK();
    } catch (Exception e) {
      String accion = "POST OauthResource.insertResource";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e));
    }
  }
  
  @PreAuthorize("hasAuthority('yauth.resource.update')")
  @RequestMapping(value="{id}", method = RequestMethod.PUT)
  public ResponseEntity<?> updateResource(@PathVariable("id") Integer id,
      @RequestBody @Valid OauthClientResource resource, Errors validation) {
    
    String accion = "PUT OauthResource.updateResource";
    
    try {
      if(validation.hasErrors())
        throw new Exception(ErrorService.validacion(validation.getAllErrors()));    
      
      OauthClientResource dbRes = this.resRepo.findOneBasic(id);
      if(dbRes == null)
        return HttpResponseService.responseBadRequest(new ErrorService("El scope no existe", accion));
      
      dbRes.setResource(resource.getResource());
      
      this.resRepo.save(dbRes);
      
      return HttpResponseService.responseOK();
    } catch (Exception e) {
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService("Error en el servidor.", accion, e)); 
    }
  }
  
}   