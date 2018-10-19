package oauth.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

import oauth.model.OauthClientDetail;
import oauth.model.OauthClientResource;
import oauth.model.OauthClientScope;
import oauth.repo.OauthClientDetailRepo;
import oauth.repo.OauthClientResourceRepo;
import oauth.utilities.ErrorService;
import oauth.utilities.HttpResponseService;

@RestController
@CrossOrigin
@RequestMapping(value="yauth/aplicacion")
public class OauthAppRESTController {
  
  private Logger logger = LogManager.getLogger(OauthAppRESTController.class);
  
  @Autowired
  private OauthClientDetailRepo appRepo;
  
  @Autowired
  private OauthClientResourceRepo resRepo;
  
  
  @PreAuthorize("hasAuthority('yauth.app.get')")
  @RequestMapping(method = RequestMethod.GET)
  public List<OauthClientDetail> getAllAplicaciones(@RequestParam(value="type", required=false) String tipo) {
    if(tipo != null && tipo.equals("F"))
      return this.appRepo.findAll();
    else
      return this.appRepo.findAllBasic();
  }
  
  @PreAuthorize("hasAuthority('yauth.app.get')")
  @RequestMapping(value="{id}", method = RequestMethod.GET)
  public OauthClientDetail getOneAplicacion(@PathVariable("id") String id) {
      return this.appRepo.findOne(id);  
  }
  
  @PreAuthorize("hasAuthority('yauth.app.insert')")
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<?> insertAplicacion(@RequestBody @Valid OauthClientDetail app, Errors validation) {
    
    try {
      if(validation.hasErrors())
        throw new Exception(ErrorService.validacion(validation.getAllErrors()));    
      
      if(app.getOauthClientResources() == null  || app.getOauthClientResources().isEmpty())
        throw new Exception("La aplicación debe al menos tener un nombre de recurso.");
      
      for (OauthClientResource res : app.getOauthClientResources())
        res.setOauthClientDetail(app);
      
      this.appRepo.save(app);
      
      return HttpResponseService.responseOK();
    } catch (Exception e) {
      String accion = "POST OauthApp.insertAplicacion";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService("Error en el servidor.", accion, e));
    }
  }
  
  @PreAuthorize("hasAuthority('yauth.app.update')")
  @RequestMapping(value="{id}", method = RequestMethod.PUT)
  public ResponseEntity<?> updateAplicacion(@PathVariable("id") String id,
      @RequestBody @Valid OauthClientDetail app, Errors validation) {
    
    try {
      if(validation.hasErrors())
        throw new Exception(ErrorService.validacion(validation.getAllErrors()));       
      
      OauthClientDetail dbApp = this.appRepo.findOne(id);
      if(dbApp == null)
        throw new Exception("La aplicación no existe");
      
      if(app.getOauthClientResources() == null  || app.getOauthClientResources().isEmpty())
        throw new Exception("La aplicación debe almenos tener un nombre de recurso.");
      
      dbApp.setAccessTokenValidity(app.getAccessTokenValidity());
      dbApp.setAdditionalInformation(app.getAdditionalInformation());
      dbApp.setAuthorizedGrantTypes(app.getAuthorizedGrantTypes());
      dbApp.setAutoapprove(app.getAutoapprove());
      dbApp.setClientSecret(app.getClientSecret());
      dbApp.setRefreshTokenValidity(app.getRefreshTokenValidity());
      dbApp.setWebServerRedirectUri(app.getWebServerRedirectUri());
      
      this.appRepo.save(dbApp);
      
      return HttpResponseService.responseOK();
    } catch (Exception e) {
      String accion = "PUT OauthApp.updateAplicacion";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e)); 
    }
  }
  
  @PreAuthorize("hasAuthority('yauth.app.scopes')")
  @RequestMapping(value="{id}/scopes", method = RequestMethod.PUT)
  public ResponseEntity<?> insertAplicacionScopes(@PathVariable("id") String id,
      @RequestBody List<OauthClientScope> scopes) {
    
    try {
      OauthClientDetail dbApp = this.appRepo.findOne(id);
      if(dbApp == null)
        throw new Exception("La aplicación no existe");
      
      if(dbApp.getOauthClientResources() == null  || dbApp.getOauthClientResources().isEmpty())
        throw new Exception("La aplicación debe almenos tener un nombre de recurso.");
      
      for(OauthClientScope s : scopes){
        s.setOauthClientDetail(dbApp);
      }
      
      dbApp.getOauthClientScopes().addAll(scopes);
      
      this.appRepo.save(dbApp);
      
      return HttpResponseService.responseOK();
    }
    catch (DataIntegrityViolationException e) {
      String accion = "PUT OauthApp.insertAplicacionScopes";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService("Se violo una restricción de permisos únicos.", accion, e)); 
    }
    catch (Exception e) {
      String accion = "PUT OauthApp.insertAplicacionScopes";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e)); 
    }
  }
  
  @PreAuthorize("hasAuthority('yauth.app.resources')")
  @RequestMapping(value="{id}/resource", method = RequestMethod.PUT)
  public ResponseEntity<?> insertAplicacionResource(@PathVariable("id") String id,
      @RequestBody OauthClientResource resource) {
    
    try {
      OauthClientDetail dbApp = this.appRepo.findOne(id);
      if(dbApp == null)
        throw new Exception("La aplicación no existe");
      
      if(resource.getResourceId() != null)
        throw new Exception("El recurso ya existe");
      
      resource.setOauthClientDetail(dbApp);
      dbApp.getOauthClientResources().add(resource);
      
      for(OauthClientScope s : resource.getOauthClientScopes()){
        s.setScopeId(null);
        s.setOauthClientResource(resource);
        s.setOauthClientDetail(dbApp);
      }
      
      this.appRepo.save(dbApp);
      
      return HttpResponseService.responseOK(resource);
    } catch (Exception e) {
      String accion = "PUT OauthApp.insertAplicacionResource";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e));  
    }
  }
  
  @PreAuthorize("hasAuthority('yauth.app.resources')")
  @RequestMapping(value="{id}/resource/{res}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteAplicacionResource(@PathVariable("id") String id,
      @PathVariable("res") Integer res) {
    
    try {
      this.resRepo.deleteById(res);
      return HttpResponseService.responseOK();
    } catch (Exception e) {
      String accion = "DELETE OauthApp.deleteAplicacionResource";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e));  
    }
  }
  
  
}   