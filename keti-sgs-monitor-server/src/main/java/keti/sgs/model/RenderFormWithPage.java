package keti.sgs.model;

import java.util.List;
import lombok.Data;

@Data
public class RenderFormWithPage<T> {
  List<T> content;

  int number = 0;

  int totalPages = 0;
}
