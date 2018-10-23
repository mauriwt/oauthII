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

import oauth.model.OauthClientScope;
import oauth.repo.OauthClientScopeRepo;
import oauth.utilities.ErrorService;
import oauth.utilities.HttpResponseService;

@RestController
@CrossOrigin
@RequestMapping(value="yauth/scope")
public class OauthScopeRESTController {
  
  private Logger logger = LogManager.getLogger(OauthScopeRESTController.class);
  
  @Autowired
  private OauthClientScopeRepo scopeRepo;
  

  @PreAuthorize("hasAuthority('yauth.scope.get')")
  @RequestMapping(method = RequestMethod.GET)
  public List<OauthClientScope> getAllScopes(@RequestParam(value="type", required=false) String tipo) {
    if(tipo != null && tipo.equals("F"))
      return this.scopeRepo.findAll();
    else
      return this.scopeRepo.findAllBasic();
  }
  
  @PreAuthorize("hasAuthority('yauth.scope.get')")
  @RequestMapping(value="{id}", method = RequestMethod.GET)
  public OauthClientScope getOneScope(@PathVariable("id") Integer id) {
      return this.scopeRepo.findOneBasic(id);
  }
  
  @PreAuthorize("hasAuthority('yauth.scope.insert')")
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<?> insertScope(@RequestBody @Valid List<OauthClientScope> scopes, Errors validation) {
    
    try {
      if(validation.hasErrors())
        throw new Exception(ErrorService.validacion(validation.getAllErrors()));
      
      this.scopeRepo.saveAll(scopes);
      
      return HttpResponseService.responseOK();
    } catch (Exception e) {
      String accion = "POST OauthScope.insertScope";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e));
    }
  }
  
  @PreAuthorize("hasAuthority('yauth.scope.update')")
  @RequestMapping(value="{id}", method = RequestMethod.PUT)
  public ResponseEntity<?> updateScope(@PathVariable("id") Integer id,
      @RequestBody @Valid OauthClientScope scope, Errors validation) {
    
    try {
      if(validation.hasErrors())
        throw new Exception(ErrorService.validacion(validation.getAllErrors()));
      
      OauthClientScope dbScope = this.scopeRepo.findOneBasic(id);
      if(dbScope == null)
        throw new Exception("El scope no existe");
      
      dbScope.setScope(scope.getScope());
      
      this.scopeRepo.save(dbScope);
      
      return HttpResponseService.responseOK();
    } catch (Exception e) {
      String accion = "PUT OauthScope.updateScope";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e)); 
    }
  }
  
  @PreAuthorize("hasAuthority('yauth.scope.delete')")
  @RequestMapping(value="{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteScope(@PathVariable Integer id) {
    
    try {
      OauthClientScope dbScope = this.scopeRepo.findOneBasic(id);
      this.scopeRepo.delete(dbScope);
      return HttpResponseService.responseOK();
    } catch (Exception e) {
      String accion = "DELETE OauthScope.deleteScopes";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService("Error en el servidor.", accion, e)); 
    }
  }
  
}   