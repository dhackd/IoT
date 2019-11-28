package keti.sgs.model;

import lombok.Data;

@Data
public class ResultMsg {
  Object data;
  boolean status;

  public ResultMsg() {
    this.data = "";
  }

  public ResultMsg(Object data, Boolean status) {
    this.data = data;
    this.status = status;
  }

  public ResultMsg(Object data) {
    this.data = data;
    this.status = true;
  }
}
