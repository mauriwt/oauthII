package oauth.repo;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import oauth.model.OauthParametro;

@Repository
public interface OauthParametroRepo extends CrudRepository<OauthParametro, Integer>{

  
  @Query("SELECT o.valor FROM OauthParametro o WHERE o.parametro = :parametro")
  public List<String> findValoresXParametro(@Param("parametro") String parametro);
}
