package keti.sgs.service;

import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import keti.sgs.model.DeviceBcForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BlockChainMonitorService {

  @Value("${server.key}")
  private String serverPrivateKey;

  @Value("${aergo.endpoint}")
  String aergoEndpoint;

  /**
   * insert block chain info.
   * 
   * @param message message
   * @param deviceAddr deviceAddr
   */
  public String insertBlockchain(String message, String deviceAddr) {
    String signature = signMessage(message);

    DeviceBcForm blockChainForm = new DeviceBcForm();
    blockChainForm.setMessage(message);
    blockChainForm.setSignature(signature);
    String payload = blockChainForm.toString();

    String password = "keti123!@";

    AergoKey aergoKey = AergoKey.of(serverPrivateKey, password);
    AergoClient aergoClient =
        new AergoClientBuilder().withEndpoint(aergoEndpoint).withNonBlockingConnect().build();

    AccountAddress recipient = AccountAddress.of(deviceAddr);

    ChainIdHash chainIdHash = aergoClient.getBlockchainOperation().getChainIdHash();
    long nonce = aergoClient.getAccountOperation().getState(aergoKey.getAddress()).getNonce() + 1;

    final RawTransaction rawTransaction = RawTransaction.newBuilder(chainIdHash)
        .from(aergoKey.getAddress()).to(recipient).amount(Aer.ZERO).nonce(nonce)
        .payload(BytesValue.of(payload.getBytes())).fee(Fee.ZERO).build();

    Transaction signedTransaction = aergoKey.sign(rawTransaction);

    String txid = aergoClient.getTransactionOperation().commit(signedTransaction).toString();

    // close
    aergoClient.close();

    return txid;
  }

  /**
   * get transaction info.
   * 
   * @param transactionId transactionId
   */
  public Transaction getTransaction(String transactionId) {
    AergoClient aergoClient =
        new AergoClientBuilder().withEndpoint(aergoEndpoint).withNonBlockingConnect().build();
    return aergoClient.getTransactionOperation().getTransaction(TxHash.of(transactionId));
  }

  private String signMessage(String message) {
    return AergoKey.of(serverPrivateKey, "keti123!@").signMessage(message);
  }

}
