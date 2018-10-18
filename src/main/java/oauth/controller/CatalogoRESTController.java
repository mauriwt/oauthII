package oauth.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value="/catalogos")
@CrossOrigin
public class CatalogoRESTController {
  
  //@PreAuthorize("#oauth2.hasScope('base.scope.action')")
  @RequestMapping(method=RequestMethod.GET)
  public String getCatalogo()
  {
    return "Hola mauri";
  }
}
