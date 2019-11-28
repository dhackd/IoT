package keti.sgs.model;

import java.sql.Timestamp;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "DEVICES")
@NoArgsConstructor
@Data
public class Devices {
  private static final Timestamp DEFAULT_DATA_UPDATE_AT = new Timestamp(0);

  @Id
  @Column(name = "DEVICE_ID", unique = true, length = 30, nullable = false)
  protected String deviceId;

  @Column(name = "DEVICE_ADDR", unique = true, length = 52, nullable = false)
  protected String deviceAddr;

  @ManyToOne(targetEntity = Type.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "DEVICE_TYPE", nullable = false)
  protected Type deviceType;

  @Column(name = "TRANSACTION_ID", length = 45, unique = true)
  protected String transactionId;
  
  @Nullable
  @Column(name = "DATA_TRANSACTION_ID", length = 45, unique = true)
  protected String dataTransactionId;
  
  @Nullable
  @Column(name = "DATA_UPDATED_AT")
  protected Timestamp dataUpdatedAt = DEFAULT_DATA_UPDATE_AT;
  
  @Column(name = "CREATED_AT")
  @CreationTimestamp
  protected Timestamp createAt;

  @Column(name = "UPDATED_AT")
  @UpdateTimestamp
  protected Timestamp updateAt;
  
  // default false;
  protected boolean connection = false;
}
