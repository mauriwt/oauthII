package oauth.repo;


import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import oauth.model.OauthUser;

@Repository
public interface OauthUserRepo extends CrudRepository<OauthUser, Integer>{

  @Query("SELECT o FROM OauthUser o WHERE o.username = :username")
  public OauthUser findByUsername(@Param("username") String username);
  
  @Query("SELECT o.password FROM OauthUser o WHERE o.username = :username")
  public String findTypeAuth(@Param("username") String username);
  
  @Query("SELECT new oauth.model.OauthUser(o.userId, o.userUUID, o.userDNI, o.username, o.mail, o.password, o.enabled) FROM OauthUser o")
  public List<OauthUser> findAllBasic();
  
  @Query("SELECT new oauth.model.OauthUser(o.userId, o.userUUID, o.userDNI, o.username, o.mail, o.password, o.enabled) "
      + "FROM OauthUser o WHERE o.enabled = :enabled")
  public List<OauthUser> findAllBasic(@Param("enabled") boolean enabled);
  
  @Query("SELECT o FROM OauthUser o WHERE o.enabled = :enabled")
  @EntityGraph(value = "OauthUser.findUserPermisos", type = EntityGraphType.LOAD)
  public List<OauthUser> findAll(@Param("enabled") boolean enabled);
  
  @Query("SELECT o FROM OauthUser o")
  @EntityGraph(value = "OauthUser.findUserPermisos", type = EntityGraphType.LOAD)
  public List<OauthUser> findAll();
  
  @Query("SELECT new oauth.model.OauthUser(o.userId, o.userUUID, o.userDNI, o.username, o.mail, o.password, o.enabled) "
      + "FROM OauthUser o WHERE o.userId = :id")
  public OauthUser findOneBasic(@Param("id") Integer id);
  
//  @EntityGraph(value = "OauthUser.findUserPermisos", type = EntityGraphType.LOAD)
//  public OauthUser findOne(Integer id);
  
}

