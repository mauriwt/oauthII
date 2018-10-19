package oauth.repo;


import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import oauth.model.OauthRol;

@Repository
public interface OauthRolRepo extends CrudRepository<OauthRol, Integer>{

  @Query("SELECT o FROM OauthRol o WHERE o.name = :name")
  public OauthRol findByName(@Param("name") String name);
  
  @Query("SELECT new oauth.model.OauthRol(o.rolId, o.name, o.desc, o.enabled) FROM OauthRol o")
  public List<OauthRol> findAllBasic();
  
  @Query("SELECT new oauth.model.OauthRol(o.rolId, o.name, o.desc, o.enabled) "
      + "FROM OauthRol o WHERE o.enabled = :enabled")
  public List<OauthRol> findAllBasic(@Param("enabled") boolean enabled);
  
  @Query("SELECT o FROM OauthRol o WHERE o.enabled = :enabled")
  @EntityGraph(value = "OauthRol.findRolScopes", type = EntityGraphType.LOAD)
  public List<OauthRol> findAll(@Param("enabled") boolean enabled);
  
  @Query("SELECT o FROM OauthRol o")
  @EntityGraph(value = "OauthRol.findRolScopes", type = EntityGraphType.LOAD)
  public List<OauthRol> findAll();
  
  @Query("SELECT new oauth.model.OauthRol(o.rolId, o.name, o.desc, o.enabled) "
      + "FROM OauthRol o WHERE o.rolId = :id")
  public OauthRol findOneBasic(@Param("id") Integer id);
  
//  @EntityGraph(value = "OauthRol.findRolScopes", type = EntityGraphType.LOAD)
//  public OauthRol findOne(Integer id);
  
}

