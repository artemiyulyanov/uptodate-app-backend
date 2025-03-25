package me.artemiyulyanov.uptodate.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.slugify.Slugify;
import me.artemiyulyanov.uptodate.minio.MinioService;
import me.artemiyulyanov.uptodate.minio.resources.ArticleResourceManager;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.Category;
import me.artemiyulyanov.uptodate.models.ContentBlock;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.repositories.ArticleRepository;
import me.artemiyulyanov.uptodate.web.PageableObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArticleService implements ResourceService<ArticleResourceManager> {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    @Lazy
    private CategoryService categoryService;

    @Autowired
    private MinioService minioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Slugify slugify;

    public long count() {
        return articleRepository.count();
    }

    public List<Article> getAllArticles(Sort sort) {
        return articleRepository.findAll(sort);
    }

    public List<Article> getAllArticles(List<Long> ids) {
        return articleRepository.findAllById(ids);
    }

    public Page<Article> getAllArticles(PageableObject<Article> pageableObject) {
        return articleRepository.findAll(pageableObject.getCommonSpecification(), pageableObject.getPageable());
    }

    public Page<Article> getAllArticles(PageRequest pageRequest) {
        return articleRepository.findAll(pageRequest);
    }

    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    public Optional<Article> getArticleBySlug(String slug) {
        return articleRepository.findBySlug(slug);
    }

    public List<Article> getArticleByAuthor(User author) {
        return articleRepository.findByAuthor(author);
    }

    @Transactional
    public Article createArticle(User author, String heading, String description, String content, List<String> categoriesNames, MultipartFile cover, List<MultipartFile> resources) {
        try {
            Set<Category> categories = categoriesNames.stream()
                    .map(categoryService::getCategoryByName)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());

            Article article = Article.builder()
                    .heading(heading)
                    .description(description)
                    .categories(categories)
                    .slug(slugify.slugify(heading))
                    .createdAt(LocalDateTime.now())
                    .author(author)
                    .build();

            Article initiallySavedArticle = articleRepository.save(article);

            List<ContentBlock> updatedContentBlocks = getResourceManager().uploadContent(initiallySavedArticle, objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, ContentBlock.class)), resources);
            String updatedCover = getResourceManager().uploadCover(initiallySavedArticle, cover);

            initiallySavedArticle.setContent(updatedContentBlocks);
            initiallySavedArticle.setCover(updatedCover);

            return articleRepository.save(initiallySavedArticle);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public Article editArticle(Long id, String heading, String description, String content, List<String> categoriesNames, MultipartFile cover, List<MultipartFile> resources) {
        try {
            Article newArticle = articleRepository.findById(id).get();
            Set<Category> categories = categoriesNames.stream()
                    .map(categoryService::getCategoryByName)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());

            List<ContentBlock> newContentBlocks = objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(List.class, ContentBlock.class));

            newArticle.setHeading(heading);
            newArticle.setDescription(description);
            newArticle.setCategories(categories);
            newArticle.setSlug(slugify.slugify(heading));
            newArticle.setContent(getResourceManager().uploadContent(newArticle, newContentBlocks, resources));
            newArticle.setCover(getResourceManager().uploadCover(newArticle, cover));

            return articleRepository.save(newArticle);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Transactional
    public void deleteArticle(Article article) {
        getResourceManager().deleteResources(article);
        articleRepository.delete(article);
    }

    @Transactional
    public void deleteArticles(List<Article> articles) {
        articleRepository.deleteAll(articles);
    }

    @Transactional
    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public ArticleResourceManager getResourceManager() {
        return ArticleResourceManager
                .builder()
                .minioService(minioService)
                .objectMapper(objectMapper)
                .build();
    }
}