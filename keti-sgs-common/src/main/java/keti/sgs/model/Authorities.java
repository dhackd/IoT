package keti.sgs.model;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "AUTHORITIES")
@Data
public class Authorities {
  @Id
  @Column(name = "USERNAME", length = 50, nullable = false, unique = true)
  protected String userId;

  @Column(name = "AUTHORITY", length = 10, nullable = false)
  protected String authority;

  @Column(name = "CREATED_AT")
  protected Timestamp createAt;
}
