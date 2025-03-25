package me.artemiyulyanov.uptodate.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.artemiyulyanov.uptodate.minio.MinioService;
import me.artemiyulyanov.uptodate.minio.resources.FaqResourceManager;
import me.artemiyulyanov.uptodate.minio.resources.ResourceManager;
import me.artemiyulyanov.uptodate.models.*;
import me.artemiyulyanov.uptodate.repositories.FaqItemRepository;
import me.artemiyulyanov.uptodate.repositories.FaqSectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FaqService implements ResourceService<FaqResourceManager> {
    @Autowired
    private FaqItemRepository faqItemRepository;

    @Autowired
    private FaqSectionRepository faqSectionRepository;

    @Autowired
    private MinioService minioService;

    @Autowired
    private ObjectMapper objectMapper;

    public List<FaqItem> getAllFaqItems() {
        return faqItemRepository.findAll();
    }

    public List<FaqSection> getAllFaqSections() {
        return faqSectionRepository.findAll();
    }

    public Optional<FaqItem> getFaqItemById(Long id) {
        return faqItemRepository.findById(id);
    }

    public Optional<FaqSection> getFaqSectionById(Long id) {
        return faqSectionRepository.findById(id);
    }

    public boolean itemExists(Long id) {
        return faqItemRepository.existsById(id);
    }

    public boolean sectionExists(Long id) {
        return faqSectionRepository.existsById(id);
    }

    @Transactional
    public FaqItem saveFaqItem(FaqItem item) {
        return faqItemRepository.save(item);
    }

    @Transactional
    public FaqSection saveFaqSection(FaqSection section) {
        return faqSectionRepository.save(section);
    }

    @Transactional
    public FaqItem createFaqItem(String title, String content, FaqSection section, List<MultipartFile> resources) {
        try {
            FaqItem item = FaqItem.builder()
                    .title(title)
                    .createdAt(LocalDateTime.now())
                    .section(section)
                    .build();

            FaqItem initiallySavedItem = faqItemRepository.save(item);

            List<ContentBlock> updatedContentBlocks = getResourceManager().uploadContent(initiallySavedItem, objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, ContentBlock.class)), resources);
            initiallySavedItem.setContent(updatedContentBlocks);

            section.getItems().add(item);
            faqSectionRepository.save(section);

            return faqItemRepository.save(initiallySavedItem);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public FaqSection createFaqSection(String title) {
        FaqSection section = FaqSection.builder()
                .title(title)
                .createdAt(LocalDateTime.now())
                .build();

        return faqSectionRepository.save(section);
    }

    @Transactional
    public void deleteFaqItem(FaqItem item) {
        faqItemRepository.delete(item);
    }

    @Transactional
    public void deleteFaqItem(Long id) {
        faqItemRepository.deleteById(id);
    }

    @Transactional
    public void deleteFaqSection(FaqSection section) {
        faqSectionRepository.delete(section);
    }

    @Transactional
    public void deleteFaqSection(Long id) {
        faqSectionRepository.deleteById(id);
    }

    @Override
    public FaqResourceManager getResourceManager() {
        return FaqResourceManager.builder()
                .minioService(minioService)
                .objectMapper(objectMapper)
                .build();
    }
}