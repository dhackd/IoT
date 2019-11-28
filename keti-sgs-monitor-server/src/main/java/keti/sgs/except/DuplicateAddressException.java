package keti.sgs.except;

public class DuplicateAddressException extends RuntimeException {
  private static final long serialVersionUID = -1588936010328653937L;

  public DuplicateAddressException() {
    super("이미 등록된 주소입니다.");
  }
}
