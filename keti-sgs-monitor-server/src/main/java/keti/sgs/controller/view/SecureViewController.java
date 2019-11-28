package keti.sgs.controller.view;

import java.security.Principal;
import javax.servlet.http.HttpSession;
import keti.sgs.controller.AbstractController;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Profile("!insecure")
public class SecureViewController extends AbstractController {
  // 디폴트 페이지
  @GetMapping("/")
  public String main(Principal principal, HttpSession session) {
    session.setAttribute("userName", principal.getName());
    return "pages/deviceManage";
  }
  
  // 로그인
  @GetMapping("/login")
  public String login() {
    return "login";
  }
  
  // 기기 관리
  @GetMapping("/deviceManage")
  public String deviceManage() {
    return "pages/deviceManage";
  }
  
  // 트랜젝션 관리
  @GetMapping("/transactionManage")
  public String transactionManage() {
    return "pages/transactionManage";
  }
  
  // 트랜젝션 상세 조회
  @GetMapping("/transactionManage/*")
  public String transactionDetail() {
    return "pages/transactionDetail";
  }
  
  // 기기 모니터링
  @GetMapping("/monitoring/devices")
  public String deviceMonitoring() {
    return "pages/deviceMonitoring";
  }
  
  // 기기 상세 모니터링
  @GetMapping("/monitoring/devices/*")
  public String deviceDetail() {
    return "pages/deviceDetail";
  }
  
  // 서버 모니터링
  @GetMapping("/monitoring/servers")
  public String serverMonitoring() {
    return "pages/serverMonitoring";
  }
  
  // 서버 상세 모니터링
  @GetMapping("/monitoring/servers/*/*")
  public String serverDetail() {
    return "pages/serverDetail";
  }
  
  // 데모용 가스 사용내역
  @GetMapping("/gasReceipts")
  public String gasReceipt() {
    return "pages/gasReceipt";
  }
}
