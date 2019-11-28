package keti.sgs.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TYPES")
@NoArgsConstructor
@Data
public class Type {

  @Id
  @Column(name = "CODE", nullable = false, length = 20, unique = true)
  protected String code;
  
  @Column(name = "NAME", nullable = false, length = 30)
  protected String name;
  
}
