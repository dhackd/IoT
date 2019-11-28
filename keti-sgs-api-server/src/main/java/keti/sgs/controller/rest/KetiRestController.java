package keti.sgs.controller.rest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import hera.api.model.AccountAddress;
import hera.api.model.ContractInterface;
import hera.key.AergoSignVerifier;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import keti.sgs.annotation.CheckSessionValid;
import keti.sgs.controller.AbstractController;
import keti.sgs.model.BlockChainInfo;
import keti.sgs.model.Devices;
import keti.sgs.model.HandShakeForm;
import keti.sgs.model.RenderFormWithPage;
import keti.sgs.model.ResultMsg;
import keti.sgs.model.SessionInfo;
import keti.sgs.model.TransactionForm;
import keti.sgs.model.version.V1ApiVersionController;
import keti.sgs.service.KetiAuthService;
import keti.sgs.service.KetiBlockchainService;
import keti.sgs.service.KetiDeviceService;
import keti.sgs.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@V1ApiVersionController
public class KetiRestController extends AbstractController {

  @Autowired
  private KetiAuthService ketiAuthService;

  @Autowired
  private KetiBlockchainService ketiBlockchainService;

  @Autowired
  private KetiDeviceService ketiDeviceService;

  private HashMap<String, String> temperarySpace = new HashMap<>();
  private AergoSignVerifier verify = new AergoSignVerifier();

  /**
   * request request random key.
   * 
   * @param auth hand shake form(address, signature)
   * @param request http request
   * @return random key (20byte)
   * @throws NoSuchAlgorithmException algorithim exception
   * @throws ParseException parse exception
   */
  @PostMapping("/challenge")
  public ResponseEntity<Object> createChallenge(@RequestBody HandShakeForm auth,
      HttpServletRequest request) throws NoSuchAlgorithmException, ParseException {
    logger.info("[KETI-API-SERVER] post /challenge >>>>>> auth:{}", auth.toString());

    Optional<Devices> devices = ketiDeviceService.getDeviceAddr(auth.getAddress());

    if (devices.isPresent()) {
      final String transactionId = devices.map(o -> o.getTransactionId()).orElse(null);
      logger.info("[KETI-API-SERVER] post /challenge >>>>>> checkTransactionId:{}", transactionId);
      if (ketiBlockchainService.checkTransactionId(transactionId)) {
        String randomMessage = Utils.randomKeyGenerator(20).toLowerCase();
        temperarySpace.put(auth.getAddress(), randomMessage);
        return new ResponseEntity<>(new ResultMsg(randomMessage), HttpStatus.OK);
      }
    }
    return new ResponseEntity<>(new ResultMsg("register your device first.", false),
        HttpStatus.BAD_REQUEST);
  }

  /**
   * authenticate method.
   * 
   * @param auth hand shake form(address, signature)
   * @param request http request
   * @return sessionInfo(id, addr, session, createTime)
   * @throws ParseException exception
   */
  @PostMapping("/authenticate")
  public ResponseEntity<Object> checkResponse(@RequestBody HandShakeForm auth,
      HttpServletRequest request) throws ParseException {
    logger.info("[KETI-API-SERVER] post /authenticate >>>>>> address:{}, signature:{}",
        auth.getAddress(), auth.getSignature());

    AccountAddress accountAddress = new AccountAddress(auth.getAddress());
    String randomMessage = temperarySpace.get(auth.getAddress());
    temperarySpace.remove(auth.getAddress());

    logger.debug(
        "[KETI-API-SERVER] post /authenticate >>>>>>>>>>>> address:{}, message:{}, signature:{}",
        accountAddress, randomMessage, auth.getSignature());

    try {
      if (verify.verifyMessage(accountAddress, randomMessage, auth.getSignature())) {
        SessionInfo sessionInfo = ketiAuthService.createSession(auth.getAddress(), request);
        return new ResponseEntity<>(new ResultMsg(sessionInfo), HttpStatus.OK);
      }
    } catch (Exception e) {
      logger.debug("[KETI-API-SERVER] post /authenticate >>>>>> exception:{}", e);
    }
    return new ResponseEntity<>(new ResultMsg("signature validation failed", false),
        HttpStatus.BAD_REQUEST);
  }

  /**
   * writeToBlockchain method.
   * 
   * @param payload transaction form(from, to, chainid...)
   * @param request http request
   * @param isValid session check(annotaion)
   * @return txid
   * @throws JsonMappingException JsonMappingException
   * @throws JsonParseException JsonParseException
   * @throws ParseException parse exception
   */
  @CheckSessionValid
  @PostMapping("/blockchain")
  public ResponseEntity<Object> writeToBlockchain(@RequestBody TransactionForm payload,
      HttpServletRequest request, boolean isValid)
      throws JsonParseException, JsonMappingException, ParseException {
    logger.info("[KETI-API-SERVER] post /blockchain >>>>>> payload:{}", payload.toString());
    if (!isValid) {
      return new ResponseEntity<>(
          new ResultMsg("authentication is required. please get a new certificate.", false),
          HttpStatus.FORBIDDEN);
    }
    String txId = ketiBlockchainService.sendTransaction(payload);
    return new ResponseEntity<>(new ResultMsg(txId), HttpStatus.CREATED);
  }

  /**
   * getChainInfo method.
   * 
   * @return chainIdHash, nonce
   */
  @GetMapping("/devices/{addr}/blockchain-info")
  public ResponseEntity<Object> getChainsInfo(@PathVariable("addr") String addr) {
    logger.info("[KETI-API-SERVER] get /devices/{addr}/blockchain-info >>>>>> addr param:{}", addr);
    BlockChainInfo blockChainInfo = ketiBlockchainService.getBlockChainInfo(addr);
    return new ResponseEntity<>(new ResultMsg(blockChainInfo), HttpStatus.OK);
  }

  /**
   * getSessionList method.
   * 
   * @return connection list
   * @throws ParseException exception
   */
  @GetMapping("/devices/connections/{pageNum}")
  public ResponseEntity<Object> getConnectionStatus(@PathVariable int pageNum)
      throws ParseException {
    logger.info("[KETI-API-SERVER] get /devices/connections/{}", pageNum);
    RenderFormWithPage<Devices> connectionList = ketiAuthService.getDeviceUpdateStatus(pageNum);
    return new ResponseEntity<>(new ResultMsg(connectionList), HttpStatus.OK);
  }

  /**
   * get contract interface.
   * 
   * @param contractAddress contract address
   */
  @GetMapping("/contracts/{contractAddress}/abi")
  public ResponseEntity<Object> getAbi(@PathVariable String contractAddress) {
    logger.info("[KETI-API-SERVER] get /contracts/{}/abi", contractAddress);
    ContractInterface abi = ketiBlockchainService.getAbi(contractAddress);
    String jsonAbi = new Gson().toJson(abi);
    return new ResponseEntity<>(new ResultMsg(jsonAbi), HttpStatus.OK);
  }

  /**
   * query contract data.
   * 
   * @param contractAddress contract address
   * @param request request
   */
  @GetMapping("/contracts/{contractAddress}/data")
  public ResponseEntity<Object> queryData(@PathVariable String contractAddress,
      HttpServletRequest request) {
    String funcName = request.getParameter("funcName");
    String args = request.getParameter("args");
    logger.info("[KETI-API-SERVER] get /contracts/{}/data func: {}, args: {}", contractAddress,
        funcName, args);

    Object[] argsArray = request.getParameter("args").split(",");

    String contractResult =
        ketiBlockchainService.queryContract(contractAddress, funcName, argsArray).toString();
    return new ResponseEntity<>(new ResultMsg(contractResult), HttpStatus.OK);
  }
}
