package keti.sgs.service;

import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.ContractAddress;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.wallet.Wallet;
import hera.wallet.WalletBuilder;
import hera.wallet.WalletType;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import keti.sgs.controller.AbstractController;
import keti.sgs.model.BlockChainInfo;
import keti.sgs.model.Devices;
import keti.sgs.model.TransactionForm;
import keti.sgs.repository.DeviceJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KetiBlockchainService extends AbstractController {
  @Value("${aergo.endpoint}")
  String aergoEndpoint;

  @Autowired
  DeviceJpaRepository deviceJpaRepository;

  /**
   * sendTransaction method.
   * 
   * @param txForm transaction form
   * @return return
   */
  public String sendTransaction(TransactionForm txForm) {
    final ChainIdHash chain = ChainIdHash.of(txForm.getChainIdHash());
    final AccountAddress from = AccountAddress.of(txForm.getFrom());
    final AccountAddress to = AccountAddress.of(txForm.getTo());

    byte[] decodedSign = Base64.getDecoder().decode(txForm.getSign());
    final Signature signature = Signature.of(BytesValue.of(decodedSign));
    final TxHash txHash = TxHash.of(txForm.getHash());

    final RawTransaction rawTransaction = RawTransaction.newBuilder(chain).from(from).to(to)
        .amount(Aer.of(txForm.getAmount())).nonce(txForm.getNonce())
        .payload(BytesValue.of(txForm.getPayload().getBytes())).fee(Fee.ZERO).build();

    final Transaction signedTx = new Transaction(rawTransaction, signature, txHash);
    AergoClient aergoClient =
        new AergoClientBuilder().withEndpoint(aergoEndpoint).withNonBlockingConnect().build();
    String txId = aergoClient.getTransactionOperation().commit(signedTx).toString();

    String recipient = txForm.getFrom();
    updateDeviceDataInfo(recipient, txId);
    return txId;
  }

  /**
   * update device date information(txid, time).
   * 
   * @param address device address
   * @param transactionId transaction id
   */
  public void updateDeviceDataInfo(String address, String transactionId) {
    Devices device = deviceJpaRepository.findByDeviceAddr(address).get();

    Date date = new Date();
    Timestamp timeStamp = new Timestamp(date.getTime());

    device.setDataUpdatedAt(timeStamp);
    device.setDataTransactionId(transactionId);
    deviceJpaRepository.save(device);
  }

  /**
   * getBlockChainInfo method.
   * 
   * @param deviceAddr device addr
   * @return return
   */
  public BlockChainInfo getBlockChainInfo(String deviceAddr) {
    AergoClient aergoClient =
        new AergoClientBuilder().withEndpoint(aergoEndpoint).withNonBlockingConnect().build();
    String chainIdHash = aergoClient.getBlockchainOperation().getChainIdHash().toString();
    long nonce =
        aergoClient.getAccountOperation().getState(AccountAddress.of(deviceAddr)).getNonce();

    BlockChainInfo blockChainInfo = new BlockChainInfo();
    blockChainInfo.setChainIdHash(chainIdHash);
    blockChainInfo.setNonce(nonce);
    return blockChainInfo;
  }

  /**
   * transactionIdCheck method.
   * 
   * @param transactionId tx hash
   * @return booleean
   */
  public Boolean checkTransactionId(String transactionId) {
    AergoClient aergoClient =
        new AergoClientBuilder().withEndpoint(aergoEndpoint).withNonBlockingConnect().build();
    String txId = aergoClient.getTransactionOperation().getTransaction(TxHash.of(transactionId))
        .getHash().toString();
    return Optional.ofNullable(txId).isPresent();
  }

  /**
   * get contract interface.
   * @param contractAddress contract address
   */
  public ContractInterface getAbi(String contractAddress) {
    AergoClient aergoClient =
        new AergoClientBuilder().withEndpoint(aergoEndpoint).withNonBlockingConnect().build();
    ContractAddress contract = ContractAddress.of(contractAddress);
    return aergoClient.getContractOperation().getContractInterface(contract);
  }

  /**
   * query contract.
   * @param contractAddress contract address
   * @param funcName function name
   * @param args arguments
   */
  public ContractResult queryContract(String contractAddress, String funcName, Object... args) {
    Wallet wallet = new WalletBuilder().withEndpoint(aergoEndpoint).withNonBlockingConnect()
        .build(WalletType.Naive);

    ContractAddress contract = ContractAddress.of(contractAddress);
    ContractInterface contractInterface = wallet.getContractInterface(contract);

    // get execution result by making query invocation
    ContractInvocation query =
        contractInterface.newInvocationBuilder().function(funcName).args(args).build();

    return wallet.query(query);
  }
}
