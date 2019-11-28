package keti.sgs.except;

public class DuplicateIdException extends RuntimeException {
  private static final long serialVersionUID = -1588936010328653937L;

  public DuplicateIdException() {
    super("이미 등록된 기기명입니다.");
  }
}
