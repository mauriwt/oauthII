package oauth.config;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.DefaultSecurityContextAccessor;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.OAuth2RequestValidator;
import org.springframework.security.oauth2.provider.SecurityContextAccessor;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import oauth.model.OauthClientDetail;
import oauth.model.OauthUser;
import oauth.repo.OauthUserRepo;
import oauth.repo.OauthClientDetailRepo;
import oauth.repo.OauthClientResourceRepo;
import oauth.repo.OauthClientScopeRepo;
import oauth.repo.OauthPermisoRepo;



@Configuration
@EnableAuthorizationServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    
  @Autowired
  PasswordEncoder encoder;

  @Autowired
  @Qualifier("authenticationManagerBean")
  private AuthenticationManager authenticationManager;
  
  @Autowired
  @Qualifier("userDetailsService")
  private UserDetailsService userDetailsService;
  
  @Autowired
  private DataSource dataSource;
  
  @Override
  public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
    oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
  }
  
  @Override
  public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
    configurer.withClientDetails(this.clientDetailsService());
//    configurer.inMemory().withClient(CLIEN_ID).secret(CLIENT_SECRET)
//        .authorizedGrantTypes(GRANT_TYPE_PASSWORD, AUTHORIZATION_CODE, REFRESH_TOKEN, IMPLICIT)
//        .scopes(SCOPE_READ, SCOPE_WRITE, TRUST).accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS)
//        .refreshTokenValiditySeconds(FREFRESH_TOKEN_VALIDITY_SECONDS);
  }

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//    endpoints.tokenStore(tokenStore()).authenticationManager(authenticationManager)
//        .accessTokenConverter(accessTokenConverter());
    endpoints.tokenStore(this.tokenStore()).tokenEnhancer(this.tokenEnhancer())
    .authenticationManager(authenticationManager).userDetailsService(this.userDetailsService)
    .requestValidator(this.customOAuth2RequestValidator())
    .requestFactory(this.scopeMappingOAuth2RequestFactory(this.clientDetailsService()));

  }

  @Bean
  public TokenStore tokenStore() {
    JdbcTokenStore store = new JdbcTokenStore(this.dataSource);
    store.setAuthenticationKeyGenerator(this.uniqueAutentication());
    return store;
  }
  
  @Bean
  public TokenEnhancer tokenEnhancer() {
    return new TokenEnhancer() {
      @Autowired
      private OauthUserRepo userRepo;

      @Override
      public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        try {
          final Map<String, Object> additionalInfo = new HashMap<>();
          if (authentication.getOAuth2Request().getGrantType().equals("client_credentials")) {
            additionalInfo.put("grant_type", "client_credentials");
            additionalInfo.put("required_by",
                authentication.getOAuth2Request().getRequestParameters().get("required_by"));
          } else {
            User user = (User) authentication.getPrincipal();
            final OauthUser dbUser = this.userRepo.findByUsername(user.getUsername());

            additionalInfo.put("dni", dbUser.getUserDNI());
            additionalInfo.put("uuid", dbUser.getUserUUID());
            additionalInfo.put("mail", dbUser.getMail());
            additionalInfo.put("username", user.getUsername());
          }

          additionalInfo.put("aplication", authentication.getOAuth2Request().getClientId());
          additionalInfo.put("timestamp", new Date().getTime());
          additionalInfo.put("source", authentication.getOAuth2Request().getExtensions().get("source"));
          ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
          return accessToken;
        } catch (Exception e) {

          return accessToken;
        }
      }
    };
  }

  
  @Bean
  public AuthenticationKeyGenerator uniqueAutentication() {
    return new UniqueAuthenticationKeyGenerator();
  }

  public class UniqueAuthenticationKeyGenerator implements AuthenticationKeyGenerator {

    private static final String CLIENT_ID = "client_id";

    private static final String SCOPE = "scope";

    private static final String USERNAME = "username";

    private static final String SOURCE = "source";

    public String extractKey(OAuth2Authentication authentication) {
      Map<String, String> values = new LinkedHashMap<>();
      OAuth2Request authorizationRequest = authentication.getOAuth2Request();

      if (!authentication.isClientOnly()) {
        values.put(USERNAME, authentication.getName());
      }
      values.put(CLIENT_ID, authorizationRequest.getClientId());
      if (authorizationRequest.getScope() != null) {
        values.put(SCOPE, OAuth2Utils.formatParameterList(new TreeSet<String>(authorizationRequest.getScope())));
      }

      if (!authentication.getOAuth2Request().getGrantType().equals("client_credentials")) {
        Map<String, Serializable> extentions = authorizationRequest.getExtensions();
        String source;
        if (extentions == null) {
          extentions = new HashMap<>(1);
          source = authorizationRequest.getRequestParameters().get(SOURCE) == null ? "Sin descripción"
              : authorizationRequest.getRequestParameters().get(SOURCE);
          extentions.put(SOURCE, source);
        } else {
          source = (String) extentions.get(SOURCE);
          if (source == null) {
            source = authorizationRequest.getRequestParameters().get(SOURCE) == null ? "Sin descripción"
                : authorizationRequest.getRequestParameters().get(SOURCE);// java.util.UUID.randomUUID().toString();
            extentions.put(SOURCE, source);
          } else {
            source = (String) extentions.get(SOURCE);
            if (source == null) {
              source = authorizationRequest.getRequestParameters().get(SOURCE) == null ? "Sin descripción"
                  : authorizationRequest.getRequestParameters().get(SOURCE);
              ;
              extentions.put(SOURCE, source);
            }
          }
          values.put(SOURCE, source);
        }
      }

      return generateKey(values);
    }

    protected String generateKey(Map<String, String> values) {
      MessageDigest digest;
      try {
        digest = MessageDigest.getInstance("MD5");
        byte[] bytes = digest.digest(values.toString().getBytes("UTF-8"));
        return String.format("%032x", new BigInteger(1, bytes));
      } catch (NoSuchAlgorithmException nsae) {
        throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).", nsae);
      } catch (UnsupportedEncodingException uee) {
        throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).", uee);
      }
    }
  }

  @Bean
  public ClientDetailsService clientDetailsService() {
    return new ClientDetailsService() {
      @Autowired
      private OauthClientDetailRepo clientRepo;

      @Autowired
      private OauthClientScopeRepo scopeRepo;

      @Autowired
      private OauthClientResourceRepo resourceRepo;

      @Override
      public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {

        try {
          OauthClientDetail client = this.clientRepo.findOne(clientId);
          if (client == null)
            throw new Exception("El cliente no existe.");

          BaseClientDetails details = new BaseClientDetails();
          details.setClientId(clientId);
          details.setClientSecret(encoder.encode(client.getClientSecret()));

          // Cargar manual por lazy exception
          List<String> resources = this.resourceRepo.findByClientId(clientId);

          List<String> scopes = this.scopeRepo.findByClientId(clientId);

          if (client.getAuthorizedGrantTypes() != null)
            details.setAuthorizedGrantTypes(Arrays.asList(client.getAuthorizedGrantTypes().split(",")));

          details.setScope(scopes);
          details.setResourceIds(resources);
          details.setAccessTokenValiditySeconds(client.getAccessTokenValidity());
          details.setRefreshTokenValiditySeconds(client.getRefreshTokenValidity());

          // if (client.getAuthorities() != null) {
          // String[] clientAuthorities = client.getAuthorities().split(",");
          // Set<GrantedAuthority> authorities = new
          // HashSet<GrantedAuthority>();
          // for (String a : clientAuthorities)
          // authorities.add(new SimpleGrantedAuthority(a));
          // details.setAuthorities(authorities);
          // }

          return details;

        } catch (Exception e) {
          throw new ClientRegistrationException(e.getMessage());

        }
      }
    };
  }

  @Controller
  @CrossOrigin
  protected class RevokeTokenEndpoint {

    @Autowired
    TokenStore tokenStore;

    @RequestMapping(method = RequestMethod.GET, value = "/oauth/logout")
    @ResponseBody
    public String revokeToken(HttpServletRequest request) {

      String respuesta = "{\"status\": \"{d}\"}";
      try {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.contains("Bearer")) {
          String tokenId = authorization.substring("Bearer".length() + 1);
          OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenId);
          ((JdbcTokenStore) tokenStore).removeRefreshToken(accessToken.getRefreshToken());
          tokenStore.removeAccessToken(accessToken);
        }

        String refresh = request.getHeader("Refresh-Token");
        if (refresh != null) {
          OAuth2RefreshToken rToken = tokenStore.readRefreshToken(refresh);
          tokenStore.removeRefreshToken(rToken);
        }

        return respuesta.replace("{d}", "Tokens and sesion deleted correctly.");
      } catch (NullPointerException ne) {
        return respuesta.replace("{d}", "Tokens not found for deletion.");
      }

    }

    @RequestMapping(method = RequestMethod.GET, value = "/auth/logout")
    @ResponseBody
    public String revokeSession(HttpServletRequest request) {

      // ELIMINAR SESION DEL SERVIDOR
      String respuesta = "{\"status\": \"{d}\"}";
      try {

        request.getSession().invalidate();

        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.contains("Bearer")) {
          String tokenId = authorization.substring("Bearer".length() + 1);
          OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenId);
          ((JdbcTokenStore) tokenStore).removeRefreshToken(accessToken.getRefreshToken());
          tokenStore.removeAccessToken(accessToken);
        }

        String refresh = request.getHeader("Refresh-Token");
        if (refresh != null) {
          OAuth2RefreshToken rToken = tokenStore.readRefreshToken(refresh);
          tokenStore.removeRefreshToken(rToken);
        }

        return respuesta.replace("{d}", "Tokens and sesion deleted correctly.");
      } catch (NullPointerException ne) {
        return respuesta.replace("{d}", "Tokens not found for deletion.");
      }

    }
  }
  
  @Bean
  public ScopeMappingOAuth2RequestFactory scopeMappingOAuth2RequestFactory(ClientDetailsService clientDetailsService) {
    return new ScopeMappingOAuth2RequestFactory(clientDetailsService);
  }
  
  private class ScopeMappingOAuth2RequestFactory implements OAuth2RequestFactory {

    private final ClientDetailsService clientDetailsService;

    private SecurityContextAccessor securityContextAccessor = new DefaultSecurityContextAccessor();

    private boolean checkUserScopes = true;

    @Autowired
    private OauthPermisoRepo permisoRepo;

    public ScopeMappingOAuth2RequestFactory(ClientDetailsService clientDetailsService) {
      this.clientDetailsService = clientDetailsService;
    }

    @SuppressWarnings("unused")
    public void setSecurityContextAccessor(SecurityContextAccessor securityContextAccessor) {
      this.securityContextAccessor = securityContextAccessor;
    }

    public void setCheckUserScopes(boolean checkUserScopes) {
      this.checkUserScopes = checkUserScopes;
    }

    public AuthorizationRequest createAuthorizationRequest(Map<String, String> authorizationParameters) {

      String clientId = authorizationParameters.get(OAuth2Utils.CLIENT_ID);
      String state = authorizationParameters.get(OAuth2Utils.STATE);
      String redirectUri = authorizationParameters.get(OAuth2Utils.REDIRECT_URI);
      Set<String> responseTypes = OAuth2Utils
          .parseParameterList(authorizationParameters.get(OAuth2Utils.RESPONSE_TYPE));

      Set<String> scopes = extractScopes(authorizationParameters, clientId);

      AuthorizationRequest request = new AuthorizationRequest(authorizationParameters,
          Collections.<String, String>emptyMap(), clientId, scopes, null, null, false, state, redirectUri,
          responseTypes);

      ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
      request.setResourceIdsAndAuthoritiesFromClientDetails(clientDetails);

      if (securityContextAccessor.isUser()) {
        request.setAuthorities(securityContextAccessor.getAuthorities());
      }
      return request;
    }

    public TokenRequest createTokenRequest(Map<String, String> requestParameters, ClientDetails authenticatedClient) {

      if (requestParameters.get(OAuth2Utils.GRANT_TYPE).equals("client_credentials")) {
        if (requestParameters.get("required_by") == null)
          throw new InvalidClientException("Required_by param not found.");
        this.setCheckUserScopes(false);
      } else
        this.setCheckUserScopes(true);

      String clientId = requestParameters.get(OAuth2Utils.CLIENT_ID);
      if (clientId == null) {
        // if the clientId wasn't passed in in the map, we add pull it from the
        // authenticated client object
        clientId = authenticatedClient.getClientId();
      } else {
        // otherwise, make sure that they match
        if (!clientId.equals(authenticatedClient.getClientId())) {
          throw new InvalidClientException("Given client ID does not match authenticated client");
        }
      }
      String grantType = requestParameters.get(OAuth2Utils.GRANT_TYPE);

      Set<String> scopes = extractScopes(requestParameters, clientId);
      return new TokenRequest(requestParameters, clientId, scopes, grantType);
    }

    public TokenRequest createTokenRequest(AuthorizationRequest authorizationRequest, String grantType) {
      return new TokenRequest(authorizationRequest.getRequestParameters(), authorizationRequest.getClientId(),
          authorizationRequest.getScope(), grantType);
    }

    public OAuth2Request createOAuth2Request(AuthorizationRequest request) {
      return request.createOAuth2Request();
    }

    public OAuth2Request createOAuth2Request(ClientDetails client, TokenRequest tokenRequest) {
      return tokenRequest.createOAuth2Request(client);
    }

    // Metodos utilitarios
    private Set<String> extractScopes(Map<String, String> requestParameters, String clientId) {
      Set<String> scopes = OAuth2Utils.parseParameterList(requestParameters.get(OAuth2Utils.SCOPE));
      ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
      String grantType = requestParameters.get(OAuth2Utils.GRANT_TYPE);

      if ((scopes == null || scopes.isEmpty())) {
        scopes = clientDetails.getScope();
      }

      if (checkUserScopes) {
        if (grantType != null && grantType.equals("password")) {
          String username = requestParameters.get("username");
          scopes = checkUserScopes(scopes, clientDetails, this.permisoRepo.findByUsernameSet(username));
        } else
          scopes = checkUserScopes(scopes, clientDetails);
      }
      return scopes;
    }

    private Set<String> checkUserScopes(Set<String> scopes, ClientDetails clientDetails) {
      if (!securityContextAccessor.isUser()) {
        return scopes;
      }
      Set<String> result = new LinkedHashSet<>();
      Set<String> authorities = AuthorityUtils.authorityListToSet(securityContextAccessor.getAuthorities());

      for (String scope : scopes) {
        if (authorities.contains(scope) || authorities.contains(scope.toUpperCase())
            || authorities.contains("ROLE_" + scope.toUpperCase())) {
          result.add(scope);
        }
      }
      return result;
    }

    private Set<String> checkUserScopes(Set<String> scopes, ClientDetails clientDetails, Set<String> userScopes) {
      if (!securityContextAccessor.isUser()) {
        return scopes;
      }
      Set<String> result = new LinkedHashSet<>();

      for (String scope : scopes) {
        if (userScopes.contains(scope) || userScopes.contains(scope.toUpperCase())
            || userScopes.contains("ROLE_" + scope.toUpperCase())) {
          result.add(scope);
        }
      }
      return result;
    }

  }
  
  @Bean
  public OAuth2RequestValidator customOAuth2RequestValidator() {

    return new OAuth2RequestValidator() {

      @Override
      public void validateScope(final AuthorizationRequest authorizationRequest, final ClientDetails client)
          throws InvalidScopeException {
        this.validateScope(authorizationRequest.getScope(), client.getScope());
      }

      @Override
      public void validateScope(final TokenRequest tokenRequest, final ClientDetails client)
          throws InvalidScopeException {
        this.validateScope(tokenRequest.getScope(), client.getScope());
      }

      private void validateScope(final Set<String> requestScopes, final Set<String> clientScopes) {

        if (CollectionUtils.isEmpty(clientScopes)) {
          throw new InvalidScopeException(
              "Emptys scope (either the client or the user is not allowed the requested scopes)");
        }
        /*
         * if (!CollectionUtils.isEmpty(clientScopes)) { if
         * (CollectionUtils.isEmpty(requestScopes)) { throw new
         * InvalidScopeException(
         * "Empty scope (either the client or the user is " +
         * "not allowed the requested scopes)"); }
         * 
         * for (final String scope : requestScopes) { if
         * (!clientScopes.contains(scope)) { throw new
         * InvalidScopeException("Invalid scope: " + scope, clientScopes); } }
         */
      }
    };
  }

}
