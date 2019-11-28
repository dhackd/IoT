package keti.sgs.controller.rest;

import keti.sgs.controller.AbstractRestController;
import keti.sgs.model.ResultMsg;
import keti.sgs.repository.TypeJpaRepository;
import keti.sgs.service.RenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RenderRestController extends AbstractRestController {

  @Autowired
  RenderService renderService;

  @Autowired
  TypeJpaRepository typeJpaRepository;

  /**
   * get device type list.
   */
  @GetMapping("types")
  public ResultMsg getTypeList() {
    ResultMsg resultMsg = new ResultMsg();
    resultMsg.setData(renderService.getTypeList());
    return resultMsg;
  }

  /**
   * ger monitoring server list.
   */
  @GetMapping("servers")
  public ResultMsg getServerList() {
    ResultMsg resultMsg = new ResultMsg();
    resultMsg.setData(renderService.getServerList());
    return resultMsg;
  }
}
