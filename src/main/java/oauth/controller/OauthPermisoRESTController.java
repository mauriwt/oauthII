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

import oauth.model.OauthPermiso;
import oauth.repo.OauthPermisoRepo;
import oauth.utilities.ErrorService;
import oauth.utilities.HttpResponseService;

@RestController
@CrossOrigin
@RequestMapping(value="yauth/permiso")
public class OauthPermisoRESTController {
  
  private Logger logger = LogManager.getLogger(OauthPermisoRESTController.class);
  
  @Autowired
  private OauthPermisoRepo permisoRepo;
  

  @PreAuthorize("hasAuthority('yauth.permiso.get')")
  @RequestMapping(method = RequestMethod.GET)
  public List<OauthPermiso> getAllPermisos(@RequestParam(value="type", required=false) String tipo) {
    if(tipo != null && tipo.equals("F"))
      return this.permisoRepo.findAll();
    else
      return this.permisoRepo.findAllBasic();
  }
  
  @PreAuthorize("hasAuthority('yauth.permiso.get')")
  @RequestMapping(value="{id}", method = RequestMethod.GET)
  public OauthPermiso getOnePermiso(@PathVariable("id") Integer id) {
      return this.permisoRepo.findOneBasic(id);
  }
  
  @PreAuthorize("hasAuthority('yauth.permiso.insert')")
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<?> insertPermiso(@RequestBody @Valid List<OauthPermiso> permisos, Errors validation) {
    
    try {
      if(validation.hasErrors())
        throw new Exception(ErrorService.validacion(validation.getAllErrors()));    
      
      this.permisoRepo.saveAll(permisos);
      
      return HttpResponseService.responseOK();
    } catch (Exception e) {
      String accion = "POST OauthPermiso.insertPermiso";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e));
    }
  }
  
  @PreAuthorize("hasAuthority('yauth.permiso.update')")
  @RequestMapping(value="{id}", method = RequestMethod.PUT)
  public ResponseEntity<?> updatePermiso(@PathVariable("id") Integer id,
      @RequestBody @Valid OauthPermiso permiso, Errors validation) {
    
    try {
      if(validation.hasErrors())
        throw new Exception(ErrorService.validacion(validation.getAllErrors()));
      
      OauthPermiso dbPermiso = this.permisoRepo.findOneBasic(id);
      if(dbPermiso == null)
        throw new Exception("El scope no existe");
      
      dbPermiso.setPermisoName(permiso.getPermisoName());
      
      this.permisoRepo.save(dbPermiso);
      
      return HttpResponseService.responseOK();
    } catch (Exception e) {
      String accion = "PUT OauthPermiso.updatePermiso";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e)); 
    }
  }
  
  @PreAuthorize("hasAuthority('yauth.permiso.delete')")
  @RequestMapping(value="{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deletePermiso(@PathVariable("id") Integer permiso) {
    
    try {
      
      this.permisoRepo.deleteById(permiso);
      return HttpResponseService.responseOK();
      
    } catch (Exception e) {
      String accion = "DELETE OauthPermiso.deletePermiso";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e)); 
    }
  }
  
}   