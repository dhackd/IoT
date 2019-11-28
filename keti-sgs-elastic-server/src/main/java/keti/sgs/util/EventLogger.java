package keti.sgs.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventLogger {

  @Autowired
  private ElasticsearchTemplate elasticTemplate;

  /**
   * write event log to elasticsearch.
   * 
   * @param type String
   * @param message String
   */
  public void writeEventLog(String type, String message) throws IOException {
    final Client client = elasticTemplate.getClient();
    @SuppressWarnings("unused")
    final IndexResponse indexResponse = client.prepareIndex(getIndex(), "log")
        .setSource(XContentFactory.jsonBuilder().startObject().field("type", type)
            .field("message", message).field("@timestamp", new Date()).endObject())
        .execute().actionGet();
  }

  /**
   * create time based index.
   * @return index String
   */
  private String getIndex() {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
    final String index = "event-" + simpleDateFormat.format(new Date());
    return index;
  }
}
