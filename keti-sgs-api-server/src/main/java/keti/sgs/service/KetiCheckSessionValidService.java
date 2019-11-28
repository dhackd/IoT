package keti.sgs.service;

import static org.slf4j.LoggerFactory.getLogger;

import javax.servlet.http.HttpServletRequest;
import keti.sgs.model.TransactionForm;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class KetiCheckSessionValidService {

  protected final Logger logger = getLogger(getClass());

  @Autowired
  private KetiAuthService ketiAuthService;

  @Pointcut(value = "execution(public * *(..))")
  public void anyPublicMethod() {}

  /**
   * session check AOP method.
   * 
   * @param joinPoint joinpoint
   * @return object
   * @throws Throwable exception
   */
  @Around(value = "anyPublicMethod() && @annotation(keti.sgs.annotation.CheckSessionValid)")
  public Object before(final ProceedingJoinPoint joinPoint) throws Throwable {
    HttpServletRequest req = null;
    Object[] obj = joinPoint.getArgs();
    TransactionForm payload = (TransactionForm) obj[0];

    if (obj[1] instanceof HttpServletRequest) {
      req = (HttpServletRequest) obj[1];
    }

    if (!ketiAuthService.isSessionValidate(payload.getFrom(), req)) {
      obj[2] = false;
    } else {
      obj[2] = true;
    }
    return joinPoint.proceed(obj);
  }
}
