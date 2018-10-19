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

import oauth.model.OauthClientScope;
import oauth.model.OauthRol;
import oauth.repo.OauthRolRepo;
import oauth.utilities.ErrorService;
import oauth.utilities.HttpResponseService;

@RestController
@CrossOrigin
@RequestMapping(value="yauth/rol")
public class OauthRolRESTController {
  
  private Logger logger = LogManager.getLogger(OauthRolRESTController.class);
  
  @Autowired
  private OauthRolRepo rolRepo;  
  

  @PreAuthorize("hasAuthority('yauth.rol.get')")
  @RequestMapping(method = RequestMethod.GET)
  public List<OauthRol> getAllRoles(@RequestParam(value="type", required=false) String tipo, 
                                       @RequestParam(value="enabled", required=true) boolean enabled) {
    if(tipo != null && tipo.equals("F"))
      return this.rolRepo.findAll(enabled);
    else
      return this.rolRepo.findAllBasic(enabled);
  }
  
  @PreAuthorize("hasAuthority('yauth.rol.get')")
  @RequestMapping(value="{id}", method = RequestMethod.GET)
  public OauthRol getOneRol(@PathVariable("id") Integer id) {
    return this.rolRepo.findOneBasic(id);
  }
  
  @PreAuthorize("hasAuthority('yauth.rol.insert')")
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<?> insertRol(@RequestBody @Valid OauthRol rol, Errors validation) {
    
     try {
       
       if(validation.hasErrors())
         throw new Exception(ErrorService.validacion(validation.getAllErrors()));
       
       if(rol.getRolId() != null)
         throw new Exception("El id del rol ya existe");    
       
       rol.setEnabled(true);
       rol = this.rolRepo.save(rol);
       
       return HttpResponseService.responseOK(rol);
    } catch (Exception e) {
      String accion = "POST OauthRol.insertRol";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e));
    }
  }
  
  @PreAuthorize("hasAuthority('yauth.rol.update')")
  @RequestMapping(value="{id}", method = RequestMethod.PUT)
  public ResponseEntity<?> updateRol(@PathVariable("id") Integer id, 
      @RequestBody @Valid OauthRol rol, Errors validation) {
    
     try {
       
       if(validation.hasErrors())
         throw new Exception(ErrorService.validacion(validation.getAllErrors()));
       
       OauthRol db = this.rolRepo.findOneBasic(id);
       if(db == null)
         throw new Exception("El rol no existe");
       
       db.setEnabled(rol.isEnabled());
       db.setName(rol.getName());
       db.setDesc(rol.getDesc());
       
       this.rolRepo.save(db);
       
       return HttpResponseService.responseOK();
    } catch (Exception e) {
      String accion = "PUT OauthRol.updateRol";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e));
    }
  }
  
  @PreAuthorize("hasAuthority('yauth.rol.permisos')")
  @RequestMapping(value="{id}/permiso", method = RequestMethod.PUT)
  public ResponseEntity<?> addPermisos(@PathVariable("id") Integer id, 
      @RequestBody @Valid List<OauthClientScope> permisos, Errors validation) {

     try {
       
       if(validation.hasErrors())
         throw new Exception(ErrorService.validacion(validation.getAllErrors()));
       
       OauthRol db = this.rolRepo.findOneBasic(id);
       if(db == null)
         throw new Exception("El rol no existe");
      
       
       db.getOauthClientScope().addAll(permisos);
       this.rolRepo.save(db);
       
       return HttpResponseService.responseOK();
    }
     catch (DataIntegrityViolationException e) {
       String accion = "PUT OauthRol.addPermisos";
       this.logger.error(e);
       return HttpResponseService.responseInternalError(new ErrorService("Se violo una restricción de permisos únicos.", accion, e)); 
     }
     catch (Exception e) {
      String accion = "PUT OauthRol.addPermisosRol";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e));
    }
  }
  
  @PreAuthorize("hasAuthority('yauth.rol.permisos')")
  @RequestMapping(value="{id}/permiso/{scopeId}", method = RequestMethod.DELETE)
  public ResponseEntity<?> rmPermisos(@PathVariable("id") Integer id,
      @PathVariable("scopeId") Integer scopeId) {

     try {
       
       OauthRol db = this.rolRepo.findOneBasic(id);
       if(db == null)
         throw new Exception("El rol no existe");
      
       OauthClientScope s = new OauthClientScope();
       s.setScopeId(scopeId);
       db.getOauthClientScope().remove(s);
       this.rolRepo.save(db);
       
       return HttpResponseService.responseOK();
    } catch (Exception e) {
      String accion = "DELETE OauthRol.rmPermisosRol";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e));
    }
  }
  
}   