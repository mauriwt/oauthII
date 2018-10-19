package oauth.config;


import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import oauth.config.ResourceServerConfig.CustomAuthenticationFailureHandler;
import oauth.utilities.LoginAttemptService;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {


  @Autowired
  private DataSource dataSource;

  @Autowired
  private LoginAttemptService loginAttemptService;

  @Autowired
  private CustomAuthenticationFailureHandler authenticationFailureHandler;

  public static final String PERMISOS_DEFAULT = "PERMISOS_BASICOS";

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(this.customAuthenticationProvider());
  }

  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  @Bean
  public UserDetailsService userDetailsService() {
    JdbcDaoImpl jdbcImpl = new JdbcDaoImpl();
    jdbcImpl.setDataSource(this.dataSource);
    jdbcImpl.setUsersByUsernameQuery("select username, password, enabled from oauth_user where username=?");
    jdbcImpl.setAuthoritiesByUsernameQuery(
        "select b.username, a.permiso_name from oauth_permiso a, oauth_user b where b.username=? and a.user_id=b.user_id");
    return jdbcImpl;
  }

  @Bean
  public PasswordEncoder passwordencoder() {
    return new BCryptPasswordEncoder();
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Bean
  public FilterRegistrationBean corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    source.registerCorsConfiguration("/**", config);
    FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return bean;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().antMatchers("/fonts/**").permitAll().antMatchers("/img/**").permitAll()
        .antMatchers("/assets/**").permitAll().antMatchers("/login").permitAll().antMatchers("/oauth/logout")
        .permitAll().antMatchers("/oauth/me").permitAll().antMatchers("/auth/logout").permitAll().anyRequest()
        .authenticated().and().formLogin().failureHandler(this.authenticationFailureHandler).loginPage("/login")
        .permitAll().and().csrf().disable().authorizeRequests().antMatchers("/oauth/token").permitAll().and()
        .authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/oauth/token").permitAll()
        .and().httpBasic();
  }

  @Configuration
  public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }

    @Bean
    public InternalResourceViewResolver setupViewResolver() {
      InternalResourceViewResolver resolver = new InternalResourceViewResolver();
      resolver.setPrefix("/WEB-INF/views/");
      resolver.setSuffix(".jsp");
      return resolver;
    }

  }

  @Bean
  public CustomAuthenticationProvider customAuthenticationProvider() {
    return new CustomAuthenticationProvider();
  }

  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(this.userDetailsService());
    authProvider.setPasswordEncoder(this.passwordencoder());
    return authProvider;
  }

  private class CustomAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
      if (authentication.getDetails() instanceof WebAuthenticationDetails) {
        if (loginAttemptService.isBlocked( ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress()) )
          throw new AuthenticationServiceException("Se han intentado demasiadas veces hacer login desde tu dirección.");
      } else {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
            .getRequest();
        if (loginAttemptService.isBlocked(getClientIP(request)))
          throw new AuthenticationServiceException("Se han intentado demasiadas veces hacer login desde tu dirección.");
      }
      
      return daoAuthenticationProvider().authenticate(authentication);
    }

    private String getClientIP(final HttpServletRequest request) {
      String xfHeader = request.getHeader("X-Forwarded-For");
      if (xfHeader == null) {
        return request.getRemoteAddr();
      }
      return xfHeader.split(",")[0];
    }

    @Override
    public boolean supports(Class<?> authentication) {
      return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
  }
}
