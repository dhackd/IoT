package keti.sgs.model;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

//for security login
@Entity
@Table(name = "USERS")
@Data
public class Users {
  @Id
  @Column(name = "USERNAME", length = 50, nullable = false, unique = true)
  protected String userId;

  @Column(name = "PASSWORD", length = 100, nullable = false)
  protected String password;

  @Column(name = "ENABLED", length = 1)
  protected int enabled;

  @Column(name = "CREATED_AT")
  protected Timestamp createAt;
}
