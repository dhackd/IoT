package keti.sgs.service;

import hera.api.model.Transaction;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import keti.sgs.model.Devices;
import keti.sgs.model.RenderFormWithPage;
import keti.sgs.model.RenderTxForm;
import keti.sgs.model.TransactionEsForm;
import keti.sgs.model.TransactionInfo;
import keti.sgs.repository.DeviceJpaRepository;
import keti.sgs.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class TransactionService extends AbstractService {
  // transaction query service
  @Autowired
  DeviceJpaRepository deviceJpaRepository;

  @Autowired
  TransactionRepository transactionRepository;

  @Autowired
  BlockChainMonitorService blockChainService;

  /**
   * get transaction information from blockchain.
   * 
   * @param transactionId transaction id
   */
  public TransactionInfo getTransactionInfo(String transactionId) {

    Transaction txInfo = blockChainService.getTransaction(transactionId);

    String to = txInfo.getRecipient().getEncoded();

    TransactionInfo result = new TransactionInfo();
    result.setTo(to);
    result.setFrom(txInfo.getSender().getEncoded());
    result.setBlockHash(txInfo.getBlockHash().toString());
    result.setPayload(txInfo.getPayload().toString());
    result.setTxId(txInfo.getHash().toString());

    Optional<Devices> device = deviceJpaRepository.findByDeviceAddr(to);

    if (device.isPresent()) {
      result.setDeviceId(device.get().getDeviceId());
      result.setType(device.get().getDeviceType());
    }

    return result;
  }

  /**
   * get device list by query.
   * 
   * @param deviceType device type
   * @param start start search date
   * @param end end search date
   * @param searchType search data type
   * @param searchData search data
   * @param page page number
   */
  public RenderFormWithPage<RenderTxForm> getTransactionList(String deviceType, String start,
      String end, String searchType, String searchData, int page) throws ParseException {
    RenderFormWithPage<RenderTxForm> result = null;

    if (deviceType.equals("")) {
      result = noType(start, end, searchType, searchData, page);
    } else {
      result = yesType(deviceType, start, end, searchType, searchData, page);
    }

    return result;
  }

  private RenderFormWithPage<RenderTxForm> yesType(String deviceType, String start, String end,
      String searchType, String searchData, int page) throws ParseException {
    RenderFormWithPage<RenderTxForm> result = null;

    if (searchData.equals("")) {
      result = yesTypeNoSearchData(deviceType, start, end, page);
    } else {
      result = yesTypeYesSearchData(deviceType, start, end, searchType, searchData, page);
    }

    return result;
  }

  private RenderFormWithPage<RenderTxForm> noType(String start, String end, String searchType,
      String searchData, int page) throws ParseException {
    RenderFormWithPage<RenderTxForm> result = null;

    if (searchData.equals("")) {
      result = noTypeNoSearchData(start, end, page);
    } else {
      result = noTypeYesSearchData(start, end, searchType, searchData, page);
    }

    return result;
  }

  private RenderFormWithPage<RenderTxForm> yesTypeYesSearchData(String deviceType, String start,
      String end, String searchType, String searchData, int pageNum) throws ParseException {
    logger.debug("[TS] yesTypeYesSearchData called");

    List<Devices> searchTarget = null;

    if (searchType.equals("1")) {
      // device name
      searchTarget = deviceJpaRepository.findAllByDeviceTypeCodeAndDeviceId(deviceType, searchData);
    } else if (searchType.equals("2")) {
      // device addresses
      searchTarget =
          deviceJpaRepository.findAllByDeviceTypeCodeAndDeviceAddr(deviceType, searchData);
    }

    RenderFormWithPage<RenderTxForm> result = new RenderFormWithPage<RenderTxForm>();
    if (!searchTarget.isEmpty()) {
      // 임시저장
      WeakHashMap<String, Devices> temp = new WeakHashMap<String, Devices>();
      searchTarget.forEach(d -> {
        temp.put(d.getDeviceAddr(), d);
      });

      List<String> addresses =
          searchTarget.stream().map(d -> d.getDeviceAddr()).collect(Collectors.toList());

      Page<TransactionEsForm> esResult =
          transactionRepository.findTransactionsByAddresses(pageNum, addresses, start, end);

      List<RenderTxForm> beautyForm = convert2ResultForm(temp, esResult);
      result.setContent(beautyForm);
      result.setNumber(esResult.getNumber());
      result.setTotalPages(esResult.getTotalPages());

      // clean
      clean(searchTarget, addresses, temp);
    } else {
      result.setContent(new ArrayList<>());
    }

    return result;
  }

  private RenderFormWithPage<RenderTxForm> yesTypeNoSearchData(String deviceType, String start,
      String end, int pageNum) throws ParseException {
    logger.debug("[TS] yesTypeNoSearchData called");
    List<Devices> searchTarget = null;

    searchTarget = deviceJpaRepository.findAllByDeviceTypeCode(deviceType);

    RenderFormWithPage<RenderTxForm> result = new RenderFormWithPage<RenderTxForm>();
    if (!searchTarget.isEmpty()) {
      // 임시 저장
      WeakHashMap<String, Devices> temp = new WeakHashMap<String, Devices>();
      searchTarget.forEach(d -> {
        temp.put(d.getDeviceAddr(), d);
      });

      List<String> addresses =
          searchTarget.stream().map(d -> d.getDeviceAddr()).collect(Collectors.toList());

      Page<TransactionEsForm> esResult =
          transactionRepository.findTransactionsByAddresses(pageNum, addresses, start, end);

      List<RenderTxForm> beautyForm = convert2ResultForm(temp, esResult);
      result.setContent(beautyForm);
      result.setNumber(esResult.getNumber());
      result.setTotalPages(esResult.getTotalPages());

      // clean
      clean(searchTarget, addresses, temp);
    } else {
      result.setContent(new ArrayList<>());
    }

    return result;
  }

  private RenderFormWithPage<RenderTxForm> noTypeYesSearchData(String start, String end,
      String searchType, String searchData, int pageNum) throws ParseException {
    logger.debug("[TS] noTypeYesSearchData called");
    List<Devices> searchTarget = new ArrayList<>();

    if (searchType.equals("1")) {
      // 기기명 검색
      searchTarget = deviceJpaRepository.findAllByDeviceId(searchData);
    } else if (searchType.equals("2")) {
      // 기기주소 검색
      searchTarget = deviceJpaRepository.findAllByDeviceAddr(searchData);
    }

    RenderFormWithPage<RenderTxForm> result = new RenderFormWithPage<RenderTxForm>();
    if (!searchTarget.isEmpty()) {
      // 임시 저장
      WeakHashMap<String, Devices> temp = new WeakHashMap<String, Devices>();
      searchTarget.forEach(d -> {
        temp.put(d.getDeviceAddr(), d);
      });

      List<String> addresses =
          searchTarget.stream().map(d -> d.getDeviceAddr()).collect(Collectors.toList());

      Page<TransactionEsForm> esResult =
          transactionRepository.findTransactionsByAddresses(pageNum, addresses, start, end);
      searchTarget.clear();

      List<RenderTxForm> beautyForm = convert2ResultForm(temp, esResult);
      result.setContent(beautyForm);
      result.setNumber(esResult.getNumber());
      result.setTotalPages(esResult.getTotalPages());

      // clean
      clean(searchTarget, addresses, temp);
    } else {
      result.setContent(new ArrayList<>());
    }

    return result;
  }

  private RenderFormWithPage<RenderTxForm> noTypeNoSearchData(String start, String end, int pageNum)
      throws ParseException {
    logger.debug("[TS] noTypeNoSearchData called");

    Page<TransactionEsForm> esResult =
        transactionRepository.findAllTransactions(start, end, pageNum);
    RenderFormWithPage<RenderTxForm> result = new RenderFormWithPage<RenderTxForm>();
    List<RenderTxForm> resultForm = new ArrayList<>();

    esResult.forEach(r -> {
      Optional<Devices> device = deviceJpaRepository.findByDeviceAddr(r.getTo());
      RenderTxForm renderTransactionForm = new RenderTxForm();

      // default 검색에서만 발생하는 상황
      if (device.isPresent()) {
        renderTransactionForm.setDeviceId(device.get().getDeviceId());
        renderTransactionForm.setType(device.get().getDeviceType());
        renderTransactionForm.setFrom(r.getFrom());
        renderTransactionForm.setTo(r.getTo());
        renderTransactionForm.setTxid(r.getTxId());
        renderTransactionForm.setRegAt(r.getTs());
      } else {
        // 알수없는 기기의 경우
        renderTransactionForm.setFrom(r.getFrom());
        renderTransactionForm.setTo(r.getTo());
        renderTransactionForm.setTxid(r.getTxId());
        renderTransactionForm.setRegAt(r.getTs());
      }

      resultForm.add(renderTransactionForm);
    });

    result.setContent(resultForm);
    result.setNumber(esResult.getNumber());
    result.setTotalPages(esResult.getTotalPages());

    return result;
  }

  private List<RenderTxForm> convert2ResultForm(WeakHashMap<String, Devices> temp,
      Page<TransactionEsForm> esResult) {
    List<RenderTxForm> resultForm = new ArrayList<>();

    esResult.forEach(r -> {
      Devices device = temp.get(r.getTo());
      RenderTxForm renderTransactionForm = new RenderTxForm();
      renderTransactionForm.setDeviceId(device.getDeviceId());
      renderTransactionForm.setFrom(r.getFrom());
      renderTransactionForm.setTo(r.getTo());
      renderTransactionForm.setTxid(r.getTxId());
      renderTransactionForm.setRegAt(r.getTs());
      renderTransactionForm.setType(device.getDeviceType());
      resultForm.add(renderTransactionForm);
    });

    return resultForm;
  }

  private void clean(List<Devices> searchTarget, List<String> addresses,
      WeakHashMap<String, Devices> temp) {
    searchTarget.clear();
    addresses.clear();
    temp.clear();
  }
}
