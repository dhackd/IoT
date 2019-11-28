package keti.sgs.controller.rest;

import java.text.ParseException;
import keti.sgs.controller.AbstractRestController;
import keti.sgs.model.RenderFormWithPage;
import keti.sgs.model.RenderTxForm;
import keti.sgs.model.ResultMsg;
import keti.sgs.model.TransactionInfo;
import keti.sgs.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionsConroller extends AbstractRestController {

  @Autowired
  private TransactionService transactionService;

  /**
   * query transaction by params.
   * 
   * @param pageNum page number
   * @param type device type
   * @param searchType search type
   * @param searchData search data
   * @param startDate start date
   * @param endDate end date
   */
  @GetMapping("transactions/pages/{pageNum}")
  public ResultMsg findTransactions(@PathVariable("pageNum") final Integer pageNum,
      final String type, final String searchType, final String searchData,
      @RequestParam(defaultValue = "0") final String startDate,
      @RequestParam(defaultValue = "0") final String endDate) throws ParseException {

    logger.info(
        "[TC] transaction query deviceType: {}, startDate: {}, endDate: {}, "
            + "searchType: {}, searchData: {}, page: {}",
        type, startDate, endDate, searchType, searchData, pageNum);

    RenderFormWithPage<RenderTxForm> result = transactionService.getTransactionList(type, startDate,
        endDate, searchType, searchData, pageNum);

    return new ResultMsg(result);
  }

  /**
   * get transaction information.
   * 
   * @param transactionId trnasction id(txid)
   */
  @GetMapping("transactions/{transactionId}")
  public ResultMsg getTransactionaInfo(@PathVariable("transactionId") final String transactionId) {
    logger.info("[TC] get transaction info txId: {}", transactionId);

    TransactionInfo result = transactionService.getTransactionInfo(transactionId);

    logger.info("[TC] get transaction return");

    return new ResultMsg(result);
  }
}
