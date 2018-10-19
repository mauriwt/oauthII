package oauth.repo;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.stereotype.Repository;

import oauth.model.OauthClientDetail;

@Repository
public interface OauthClientDetailRepo extends CrudRepository<OauthClientDetail, String>{
  
  @Query("SELECT new oauth.model.OauthClientDetail(o.clientId, o.accessTokenValidity, "
      + "o.additionalInformation, o.authorizedGrantTypes, o.autoapprove, o.clientSecret, "
      + "o.refreshTokenValidity,o.webServerRedirectUri) "
      + "FROM OauthClientDetail o")
  public List<OauthClientDetail> findAllBasic();
  
  @Query("SELECT o FROM OauthClientDetail o")
  @EntityGraph(value = "OauthClientDetail.findScopesResources", type = EntityGraphType.LOAD)
  public List<OauthClientDetail> findAll();
  
  @Query("SELECT new oauth.model.OauthClientDetail(o.clientId, o.accessTokenValidity, "
      + "o.additionalInformation, o.authorizedGrantTypes, o.autoapprove, o.clientSecret, "
      + "o.refreshTokenValidity,o.webServerRedirectUri) "
      + "FROM OauthClientDetail o WHERE o.clientId = :clientId")
  public OauthClientDetail findOneBasic(@Param("clientId") String clientId);
  
  @Query("SELECT o FROM OauthClientDetail o WHERE o.clientId = :clientId")
  @EntityGraph(value = "OauthClientDetail.findScopesResources", type = EntityGraphType.LOAD)
  public OauthClientDetail findOne(@Param("clientId") String clientId);
 
  
}
