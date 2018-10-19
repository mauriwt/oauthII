package oauth.model;

import java.io.Serializable;
import javax.persistence.*;



/**
 * The persistent class for the oauth_parametro database table.
 * 
 */
@Entity
@Table(name="oauth_parametro")
@NamedQuery(name="OauthParametro.findAll", query="SELECT o FROM OauthParametro o")
public class OauthParametro implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="parametro_id")
	@SequenceGenerator(name="oauth_parametro_parametro_id_seq", sequenceName="oauth_parametro_parametro_id_seq", allocationSize=1, initialValue=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="oauth_parametro_parametro_id_seq")
	private Integer parametroId;

	@Column(name="parametro")
	private String parametro;

	@Column(name="valor")
	private String valor;

	
	public OauthParametro() {
	}


    public Integer getParametroId() {
      return parametroId;
    }


    public String getParametro() {
      return parametro;
    }


    public void setParametro(String parametro) {
      this.parametro = parametro;
    }


    public String getValor() {
      return valor;
    }


    public void setValor(String valor) {
      this.valor = valor;
    }
}