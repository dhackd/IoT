package keti.sgs.repository;

import java.text.ParseException;
import java.util.List;
import keti.sgs.model.TransactionEsForm;
import keti.sgs.util.TransactionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

  @Autowired
  private ElasticsearchTemplate elasticTemplate;

  @Override
  public Page<TransactionEsForm> findAllTransactions(String start, String end, Integer pageNum)
      throws ParseException {
    NativeSearchQueryBuilder searchQueryBuilder =
        new NativeSearchQueryBuilder().withSort(SortBuilders.fieldSort("ts").order(SortOrder.DESC))
            .withPageable(PageRequest.of(pageNum, 10));

    if (TransactionUtils.checkDate(start, end)) {
      long startTime = TransactionUtils.stringStartDateToStartTime(start);
      long endTime = TransactionUtils.stringEndDateToEndTime(end);
      searchQueryBuilder.withQuery(QueryBuilders.boolQuery()
          .must(QueryBuilders.rangeQuery("ts").gte(startTime).lte(endTime)));
    }

    return elasticTemplate.queryForPage(searchQueryBuilder.build(), TransactionEsForm.class);
  }


  @Override
  public Page<TransactionEsForm> findTransactionsByAddresses(Integer pageNum,
      List<String> addresses, String start, String end) throws ParseException {
    NativeSearchQueryBuilder searchQueryBuilder =
        new NativeSearchQueryBuilder().withSort(SortBuilders.fieldSort("ts").order(SortOrder.DESC))
            .withPageable(PageRequest.of(pageNum, 10));

    if (TransactionUtils.checkDate(start, end)) {
      BoolQueryBuilder shouldBoolQueryBuilder = QueryBuilders.boolQuery();
      for (int i = 0; i < addresses.size(); i++) {
        shouldBoolQueryBuilder.should(QueryBuilders.termQuery("from.keyword", addresses.get(i)));
      }
      long startTime = TransactionUtils.stringStartDateToStartTime(start);
      long endTime = TransactionUtils.stringEndDateToEndTime(end);
      searchQueryBuilder.withQuery(QueryBuilders.boolQuery().must(shouldBoolQueryBuilder)
          .must(QueryBuilders.rangeQuery("ts").gte(startTime).lte(endTime)));
      return elasticTemplate.queryForPage(searchQueryBuilder.build(), TransactionEsForm.class);

    } else {
      BoolQueryBuilder mustBoolQueryBuilder = QueryBuilders.boolQuery().must(
          QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("from.keyword", addresses)));
      searchQueryBuilder.withQuery(mustBoolQueryBuilder);
      return elasticTemplate.queryForPage(searchQueryBuilder.build(), TransactionEsForm.class);
    }
  }
}
