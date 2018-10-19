package oauth.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import oauth.model.OauthClientResource;

@Repository
public interface OauthClientResourceRepo extends CrudRepository<OauthClientResource, Integer>{

  @Query("SELECT o.resource FROM OauthClientResource o JOIN o.oauthClientDetail d WHERE d.clientId = :clientId")
  public List<String> findByClientId(@Param("clientId") String clientId);
  
  
  @Query("SELECT new oauth.model.OauthClientResource(o.resourceId, o.resource) FROM OauthClientResource o")
  public List<OauthClientResource> findAllBasic();
  
  //@EntityGraph(value = "OauthClientResource.findClientScopes", type = EntityGraphType.LOAD)
  public List<OauthClientResource> findAll();
  
  @Query("SELECT new oauth.model.OauthClientResource(o.resourceId, o.resource) FROM OauthClientResource o WHERE o.resourceId = :id")
  public OauthClientResource findOneBasic(@Param("id")Integer id);
  
  //@EntityGraph(value = "OauthClientResource.findClientScopes", type = EntityGraphType.FETCH)
  //public OauthClientResource findOne(Integer id);
  

}
