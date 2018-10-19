package oauth.utilities;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class HttpResponseService {
  
  
  public static <T> ResponseEntity<?> responseOK(T body){
    return new ResponseEntity<T>(body, HttpStatus.OK);
  }
  
  public static <T> ResponseEntity<?> responseOK(){
    return new ResponseEntity<T>(HttpStatus.OK);
  }
  
  public static <T> ResponseEntity<?> responseBadRequest(T body){
    return new ResponseEntity<T>(body, HttpStatus.BAD_REQUEST);
  }
  
  public static <T> ResponseEntity<?> responseBadRequest(){
    return new ResponseEntity<T>(HttpStatus.BAD_REQUEST);
  }
  
  public static <T> ResponseEntity<?> responseUnauthorized(T body){
    return new ResponseEntity<T>(body, HttpStatus.UNAUTHORIZED);
  }
  
  public static <T> ResponseEntity<?> responseUnauthorized(){
    return new ResponseEntity<T>(HttpStatus.UNAUTHORIZED);
  }
  
  public static <T> ResponseEntity<?> responseInternalError(T body){
    return new ResponseEntity<T>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
  
  public static <T> ResponseEntity<?> responseInternalError(){
    return new ResponseEntity<T>(HttpStatus.INTERNAL_SERVER_ERROR);
  }
  
  
  
}
