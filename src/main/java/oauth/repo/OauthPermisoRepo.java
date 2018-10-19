package oauth.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import oauth.model.OauthPermiso;

@Repository
public interface OauthPermisoRepo extends CrudRepository<OauthPermiso, Integer>{
  @Query("SELECT o FROM OauthPermiso o WHERE o.oauthUser.username = :username")
  public List<OauthPermiso> findByUsername(@Param("username") String username);
  
  @Query("SELECT o.permisoName FROM OauthPermiso o WHERE o.oauthUser.username = :username")
  public Set<String> findByUsernameSet(@Param("username") String username);
  
  @Query("SELECT new oauth.model.OauthPermiso(o.permisoId, o.permisoName) FROM OauthPermiso o")
  public List<OauthPermiso> findAllBasic();
  
  @Query("SELECT o FROM OauthClientScope o")
  @EntityGraph(value = "OauthPermiso.findPermisoUser", type = EntityGraphType.LOAD)
  public List<OauthPermiso> findAll();
  
  @Query("SELECT new oauth.model.OauthPermiso(o.permisoId, o.permisoName) FROM OauthPermiso o WHERE o.permisoId = :id")
  public OauthPermiso findOneBasic(@Param("id")Integer id);
  
//  @EntityGraph(value = "OauthPermiso.findPermisoUser", type = EntityGraphType.LOAD)
//  public OauthPermiso findOne(Integer id);
}
