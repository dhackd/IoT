package keti.sgs.controller;

import static org.slf4j.LoggerFactory.getLogger;

import keti.sgs.model.version.V1ApiVersionController;
import org.slf4j.Logger;

@V1ApiVersionController
public class AbstractRestController {
  protected final Logger logger = getLogger(getClass());
}
