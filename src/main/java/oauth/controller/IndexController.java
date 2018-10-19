package oauth.controller;

import org.springframework.security.oauth2.provider.AuthorizationRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class IndexController {


  @RequestMapping(value="/", method=RequestMethod.GET)
  public String getIndex(@ModelAttribute AuthorizationRequest clientAuth) throws Exception {
    return "index";
  }


}