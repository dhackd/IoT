package keti.sgs.model;

import com.google.gson.Gson;
import lombok.Data;

@Data
public class DeviceBcForm {
  String message;
  
  String signature;
  
  @Override
  public String toString() {
    // for elastic search
    return new Gson().toJson(this).toString();
  }
}
