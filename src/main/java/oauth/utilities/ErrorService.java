package oauth.utilities;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

@Service
public class ErrorService {
  
  private String mensaje;
  private String accionEjecutada;
  
  private Exception exception;
  
  public ErrorService() {
  }
  
  public ErrorService(String mensaje, String accionEjecutada, Exception exception) {
    this.mensaje = mensaje;
    this.accionEjecutada = accionEjecutada;
    this.exception = exception;
  }
  
  public ErrorService(String mensaje, String accionEjecutada) {
    this.mensaje = mensaje;
    this.accionEjecutada = accionEjecutada;
  }
  
  
  public static String validacion(List<ObjectError> errores)
  {
    StringBuilder mensaje = new StringBuilder();
    mensaje.append("Existe errores de validación en los campos: \n");
    
    
    for (ObjectError error : errores) {
      mensaje.append(error.getDefaultMessage());
      mensaje.append(" (");
      mensaje.append(error.getObjectName());
      mensaje.append(")\n");
    }
    return mensaje.toString();
  }
  

  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  public String getAccionEjecutada() {
    return accionEjecutada;
  }

  public void setAccionEjecutada(String accionEjecutada) {
    this.accionEjecutada = accionEjecutada;
  }

  public Exception getException() {
    return exception;
  }

  public void setException(Exception exception) {
    this.exception = exception;
  }
  
}
