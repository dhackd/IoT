package keti.sgs.repository;

import java.text.ParseException;
import java.util.List;
import keti.sgs.model.TransactionEsForm;
import org.springframework.data.domain.Page;

public interface TransactionRepository {
  Page<TransactionEsForm> findAllTransactions(String start, String end, Integer pageNum)
      throws ParseException;

  Page<TransactionEsForm> findTransactionsByAddresses(Integer pageNum, List<String> addresses,
      String start, String end) throws ParseException;
}
