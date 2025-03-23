package me.artemiyulyanov.uptodate.controllers.api;

import me.artemiyulyanov.uptodate.kafka.producers.ArticleProducerService;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.services.ArticleService;
import me.artemiyulyanov.uptodate.solr.services.SolrArticleRepository;
import me.artemiyulyanov.uptodate.web.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {
    @Autowired
    private SolrArticleRepository solrArticleRepository;

    @Autowired
    private RequestService requestService;

    @Autowired
    private ArticleProducerService articleProducerService;

    @Autowired
    private ArticleService articleService;

    @GetMapping("/test")
    public ResponseEntity<?> test(
            @RequestParam(defaultValue = "", required = false) Long id
    ) {
        Article article = articleService.findById(id).orElse(null);
        articleProducerService.sendTest("test", article);
        return requestService.executeApiResponse(HttpStatus.OK, "The article has been sent to Kafka!");
    }
}