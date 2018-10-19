package oauth.controller;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import oauth.model.OauthClientDetail;
import oauth.repo.OauthClientDetailRepo;

@Controller
@SessionAttributes(types = AuthorizationRequest.class)
public class AccessConfirmController {
  
  @Autowired
  private OauthClientDetailRepo appRepo;
  
  @Autowired
  private AuthorizationEndpoint nativeEndpoint;

  @RequestMapping("/oauth/confirm_access")
  public ModelAndView getAccessConfirmation(Map<String, Object> model, HttpServletRequest request, Principal user) throws Exception {
    if (request.getAttribute("_csrf") != null) {
      model.put("_csrf", request.getAttribute("_csrf"));
    }
    
    List<String> myScp = new ArrayList<>();
    @SuppressWarnings("unchecked")
    Map<String, String> scopes = (Map<String, String>) (model.containsKey("scopes") ? model.get("scopes")
        : request.getAttribute("scopes"));
    
    for(String s : scopes.keySet() ){
      myScp.add(s.split("=")[0]);
    }
    
    model.put("myScp", myScp);
    
    
    OauthClientDetail app = this.appRepo.findOneBasic(request.getAttribute("client_id").toString());
    
    if(app != null)
      model.put("client_desc", app.getAdditionalInformation());
    if(user != null)
      model.put("username", user.getName());
    
    return new ModelAndView("access_confirmation", model);
  }
  


  protected String createTemplate(Map<String, Object> model, HttpServletRequest request) {
    String template = TEMPLATE;
    if (model.containsKey("scopes") || request.getAttribute("scopes") != null) {
      template = template.replace("%scopes%", createScopes(model, request)).replace("%denial%", "");
    } else {
      template = template.replace("%scopes%", "").replace("%denial%", DENIAL);
    }
    if (model.containsKey("_csrf") || request.getAttribute("_csrf") != null) {
      template = template.replace("%csrf%", CSRF);
    } else {
      template = template.replace("%csrf%", "");
    }
    return template;
  }
  
  @RequestMapping(value = "/oauth/authorize")
  ModelAndView authorize(Map<String, Object> model, @RequestParam Map<String, String> parameters,
      SessionStatus sessionStatus, Principal principal) {
    return this.nativeEndpoint.authorize(model, parameters, sessionStatus, principal);
  }

  @RequestMapping(value = "/oauth/authorize", method = RequestMethod.POST, params = OAuth2Utils.USER_OAUTH_APPROVAL )
  public View approveOrDeny(@RequestParam Map<String, String> approvalParameters, 
      Map<String, ?> model, @RequestParam(defaultValue="", required=false) String source, 
      SessionStatus sessionStatus, Principal principal) {
    AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");
    
    Map<String, Serializable> extentions = authorizationRequest.getExtensions();
    extentions.put("source", source);
    return this.nativeEndpoint.approveOrDeny(approvalParameters, model, sessionStatus, principal);
  }
  
  private CharSequence createScopes(Map<String, Object> model, HttpServletRequest request) {
    StringBuilder builder = new StringBuilder("<ul>");
    @SuppressWarnings("unchecked")
    Map<String, String> scopes = (Map<String, String>) (model.containsKey("scopes") ? model.get("scopes")
        : request.getAttribute("scopes"));
    for (String scope : scopes.keySet()) {
      String approved = "true".equals(scopes.get(scope)) ? " checked" : "";
      String denied = !"true".equals(scopes.get(scope)) ? " checked" : "";
      String value = SCOPE.replace("%scope%", scope).replace("%key%", scope).replace("%approved%", approved)
          .replace("%denied%", denied);
      builder.append(value);
    }
    builder.append("</ul>");
    return builder.toString();
  }

  private static String CSRF = "<input type='hidden' name='${_csrf.parameterName}' value='${_csrf.token}' />";

  private static String DENIAL = "<form id='denialForm' name='denialForm' action='${path}/oauth/authorize' method='post'><input name='user_oauth_approval' value='false' type='hidden'/>%csrf%<label><input name='deny' value='Deny' type='submit'/></label></form>";

  private static String TEMPLATE = "<html><body><h1>OAuth Approval</h1>"
      + "<p>Do you authorize '${authorizationRequest.clientId}' to access your protected resources?</p>"
      + "<form id='confirmationForm' name='confirmationForm' action='${path}/oauth/authorize' method='post'><input name='user_oauth_approval' value='true' type='hidden'/>%csrf%%scopes%<label><input name='authorize' value='Authorize' type='submit'/></label></form>"
      + "%denial%</body></html>";

  private static String SCOPE = "<li><div class='form-group'>%scope%: <input type='radio' name='%key%'"
      + " value='true'%approved%>Approve</input> <input type='radio' name='%key%' value='false'%denied%>Deny</input></div></li>";

}