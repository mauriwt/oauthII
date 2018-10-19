package oauth.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import oauth.utilities.LoginAttemptService;

@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

  private Logger logger = LogManager.getLogger(ResourceServerConfig.class);

  @Autowired
  private LoginAttemptService loginAttemptService;

  @Component
  public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    public void onApplicationEvent(AuthenticationSuccessEvent e) {
      logger.info("CORRECT WEB LOGIN: " + e.getAuthentication().getName());
    }
  }

  @Component
  public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
      if (e.getAuthentication().getDetails() instanceof WebAuthenticationDetails){
        loginAttemptService.loginFailed( ((WebAuthenticationDetails)e.getAuthentication().getDetails()).getRemoteAddress() );
      }else{
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
            .getRequest();
        loginAttemptService.loginFailed(getClientIP(request));
      }      
      logger.info("FAIL WEB LOGIN: " + e.getAuthentication().getName());
    }
  }

  @Component
  public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
        final AuthenticationException exception) throws IOException, ServletException {
      setDefaultFailureUrl("/login?error=true");

      super.onAuthenticationFailure(request, response, exception);

      
      String errorMessage = "Las credenciales ingresadas son incorrectas.";
      if (loginAttemptService.isBlocked(getClientIP(request))) {
        errorMessage = "Se ha bloqueado tu dirección, por demasiados intentos de acceso.";
      }
      else if (exception.getMessage().equalsIgnoreCase("User is disabled")) {
        errorMessage = "El usuario esta desactivado.";
      } else if (exception.getMessage().equalsIgnoreCase("User account has expired")) {
        errorMessage = "La cuenta ha expirado.";
      } else if (exception.getMessage().equalsIgnoreCase("blocked")) {
        errorMessage = "Ha sido bloqueado por demasiados intentos de acceder desde su dirección.";
      }

      request.getSession().setAttribute("auth_err", errorMessage);

    }
  }

  private String getClientIP(final HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }

}
