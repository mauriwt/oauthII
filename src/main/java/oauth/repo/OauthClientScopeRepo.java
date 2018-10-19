package oauth.repo;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import oauth.model.OauthClientScope;

@Repository
public interface OauthClientScopeRepo extends CrudRepository<OauthClientScope, Integer>{

  @Query("SELECT o.scope FROM OauthClientScope o WHERE o.oauthClientDetail.clientId = :clientId")
  public List<String> findByClientId(@Param("clientId") String clientId);
  
  @Modifying
  @Query("SELECT o.scope FROM OauthClientScope o WHERE o.oauthClientDetail.clientId = :clientId"
        + " AND o.oauthClientResource.resourceId IN :resources")
  public List<String> findByClientAndResourceIDs(@Param("clientId") String clientId,
      @Param("resources") List<String> resources);
  
  @Modifying
  @Query("SELECT o.scope FROM OauthClientScope o WHERE o.scope LIKE :scopeMask")
  public List<String> findByScopeMask(@Param("scopeMask") String scopeMask);
  
  
  @Query("SELECT new oauth.model.OauthClientScope(o.scopeId, o.scope) FROM OauthClientScope o")
  public List<OauthClientScope> findAllBasic();
  
  @Query("SELECT o FROM OauthClientScope o")
  @EntityGraph(value = "OauthClientScope.findClientResourse", type = EntityGraphType.LOAD)
  public List<OauthClientScope> findAll();
  
  @Query("SELECT new oauth.model.OauthClientScope(o.scopeId, o.scope) FROM OauthClientScope o WHERE o.scopeId = :id")
  public OauthClientScope findOneBasic(@Param("id")Integer id);
  
//  @EntityGraph(value = "OauthClientScope.findClientResourse", type = EntityGraphType.LOAD)
//  public OauthClientScope findOne(Integer id);
  
}
