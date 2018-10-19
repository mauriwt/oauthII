package oauth.controller;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import oauth.utilities.ErrorService;
import oauth.utilities.HttpResponseService;

@Controller
public class MeController {

  @Autowired
  TokenStore tokenStore;

  private Logger logger = LogManager.getLogger(MeController.class);

  // @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(method = RequestMethod.GET, value = "/oauth/me")
  @ResponseBody
  public ResponseEntity<?> user(HttpServletRequest req) {
    try {
      OAuth2Authentication token = this.tokenStore
          .readAuthentication(req.getHeader("Authorization").substring("Bearer ".length()));

      Map<String, Object> info = this.tokenStore.getAccessToken(token).getAdditionalInformation();
      info.put("username", token.getName());
      info.put("authorities", token.getAuthorities());

      return HttpResponseService.responseOK(info);
    } catch (Exception e) {
      String accion = "GET OauthMe.userInfo";
      e.printStackTrace();
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e));
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "/auth/me")
  @ResponseBody
  public Object user(Principal user) {
    return user;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/yauth/sesion")
  @ResponseBody
  public Collection<OAuth2AccessToken> sessions(Principal user) {
    if (user != null) {
      return ((JdbcTokenStore) this.tokenStore).findTokensByUserName(user.getName());
    }
    return null;
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/yauth/sesion/{access}/{refresh}")
  @ResponseBody
  public ResponseEntity<?> rmSession(@PathVariable("access") String access, @PathVariable("refresh") String refresh) {

    try {
      OAuth2AccessToken accessToken = tokenStore.readAccessToken(access);
      OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(refresh);
      tokenStore.removeAccessToken(accessToken);
      tokenStore.removeRefreshToken(refreshToken);
      return HttpResponseService.responseOK();
    } catch (Exception e) {
      String accion = "DELETE OauthMe.removeToken";
      this.logger.error(e);
      return HttpResponseService.responseInternalError(new ErrorService(e.getMessage(), accion, e));
      
    }
  }
}