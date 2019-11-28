package keti.sgs.model;

public interface DeviceManagable {
  String registerDevice(String deviceId, String typeCode, String deviceAddr);
  
  String removeDevice(String deviceId);
  
  boolean isDuplicatedId(String deviceId);
  
  boolean isDuplicatedAddr(String deviceAddr);
}
