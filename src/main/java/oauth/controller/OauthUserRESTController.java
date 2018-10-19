package oauth.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import oauth.model.OauthPermiso;
import oauth.model.OauthUser;
import oauth.repo.OauthUserRepo;
import oauth.utilities.ErrorService;
import oauth.utilities.HttpResponseService;

@RestController
@CrossOrigin
@RequestMapping(value="yauth/usuario")
public class OauthUserRESTController {
  
  private Logger logger = LogManager.getLogger(OauthUserRESTController.class);
  
  @Autowired
  private OauthUserRepo userRepo;  
  
  @Autowired
  private PasswordEncoder passwordencoder;
  
  public OauthUserRESTController() {
   
  }

  @PreAuthorize("hasAuthority('yauth.user.get')")
  @RequestMapping(method = RequestMethod.GET)
  public List<OauthUser> getAllUsarios(@RequestParam(value="type", required=false) String tipo, 
                                       @RequestParam(value="enabled", required=false) boolean enabled) {
    if(tipo != null && tipo.equals("F"))
      return this.userRepo.findAll(enabled);
    else
      return this.userRepo.findAllBasic(enabled);
  }
  
  @PreAuthorize("hasAuthority('yauth.user.get')")
  @RequestMapping(value="{id}", method = RequestMethod.GET)
  public OauthUser getOneUsario(@PathVariable("id") Integer id) {
    return this.userRepo.findOneBasic(id);
  }
  
  @PreAuthorize("hasAuthority('yauth.user.insert')")
  @RequestMapping(value = "PASSWORD", method = RequestMethod.POST)
  public ResponseEntity<?> insertUsarioNormal(@RequestBody @Valid OauthUser usuario, Errors validation) {
    
     try {
       
       if(validation.hasErrors())
         throw new Exception(ErrorService.validacion(validation.getAllErrors()));
       
       if(usuario.getUserId() != null)
         throw new Exception("El id del usuario ya existe");    
       
       usuario.setPassword(this.passwordencoder.encode(usuario.getPassword()));
       usuario.setEnabled(true);
       this.userRepo.save(usuario);
       
       return HttpResponseService.responseOK();
    }
     catch (DataIntegrityViolationException e) {
       String accion = "POST OauthUser.insertUsario";
       this.logger.error(e);
       return HttpResponseService.responseInternalError(new ErrorService("Error de claves únicas el nombre de usuario yá existe.", accion, e));
     }
     catch (Exception e) {
      String accion = "POST OauthUser.insertUsario";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e));
    }
  }
  
  //@PreAuthorize("hasAuthority('yauth.user.insert')")
  @RequestMapping(value="LDAP" ,method = RequestMethod.POST)
  public ResponseEntity<?> insertUsarioLDAP(@RequestBody @Valid OauthUser usuario, Errors validation) {
    
     try {
       
       if(validation.hasErrors())
         throw new Exception(ErrorService.validacion(validation.getAllErrors()));
       
       if(usuario.getUserId() != null)
         throw new Exception("El id del usuario ya existe");    
       
       usuario.setPassword(OauthUser.LDAP_TYPE);
       usuario.setEnabled(true);
       
       this.userRepo.save(usuario);
       
       return HttpResponseService.responseOK();
    } catch (Exception e) {
      String accion = "POST OauthUser.insertUsario";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e));
    }
  }
  
  @PreAuthorize("hasAuthority('yauth.user.update')")
  @RequestMapping(value="{id}", method = RequestMethod.PUT)
  public ResponseEntity<?> updateStatusUsario(@PathVariable("id") Integer id, 
      @RequestBody OauthUser usuario, Errors validation) {
    
     try {
       
       if(validation.hasErrors())
         throw new Exception(ErrorService.validacion(validation.getAllErrors()));
       
       OauthUser dbUser = this.userRepo.findOneBasic(id);
       if(dbUser == null)
         throw new Exception("El usuario no existe");
       dbUser.setEnabled(usuario.getEnabled());
       this.userRepo.save(dbUser);
       
       return HttpResponseService.responseOK();
    } catch (Exception e) {
      String accion = "PUT OauthUser.updateUsario";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e));
    }
  }
  
  @PreAuthorize("hasAuthority('yauth.user.update')")
  @RequestMapping(value="{id}/{ldapType}", method = RequestMethod.PUT)
  public ResponseEntity<?> updateUsario(@PathVariable("id") Integer id, @PathVariable("ldapType") String ldapType, 
      @RequestBody OauthUser usuario, Errors validation) {
    
     try {
       
       if(validation.hasErrors())
         throw new Exception(ErrorService.validacion(validation.getAllErrors()));
       
       OauthUser dbUser = this.userRepo.findOneBasic(id);
       if(dbUser == null)
         throw new Exception("El usuario no existe");
       dbUser.setMail(usuario.getMail());
       dbUser.setUserDNI(usuario.getUserDNI());
       dbUser.setUsername(usuario.getUsername());
       
       if(ldapType.equals("PASSWORD"))
         dbUser.setPassword(this.passwordencoder.encode(usuario.getPassword()));
       else
         dbUser.setPassword(OauthUser.LDAP_TYPE);
       
       this.userRepo.save(dbUser);
       
       return HttpResponseService.responseOK();
    } catch (Exception e) {
      String accion = "PUT OauthUser.updateUsario";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e));
    }
  }
  
  @PreAuthorize("hasAuthority('yauth.user.permisos')")
  @RequestMapping(value="{id}/permiso", method = RequestMethod.PUT)
  public ResponseEntity<?> addPermisosUsario(@PathVariable("id") Integer id, 
      @RequestBody @Valid List<OauthPermiso> permisos, Errors validation) {

     try {
       
       if(validation.hasErrors())
         throw new Exception(ErrorService.validacion(validation.getAllErrors()));
       
       OauthUser dbUser = this.userRepo.findOneBasic(id);
       if(dbUser == null)
         throw new Exception("El usuario no existe");
       
       for(OauthPermiso permiso : permisos){
         permiso.setOauthUser(dbUser);
       }
       
       dbUser.getOauthPermisos().addAll(permisos);
       this.userRepo.save(dbUser);
       
       return HttpResponseService.responseOK();
    }
     catch (DataIntegrityViolationException e) {
       String accion = "PUT OauthApp.insertAplicacionScopes";
       this.logger.error(e);
       return HttpResponseService.responseInternalError(new ErrorService("Se violo una restricción de permisos únicos.", accion, e)); 
     }
     catch (Exception e) {
      String accion = "PUT OauthUser.addPermisosUsario";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e));
    }
  }
  
}   