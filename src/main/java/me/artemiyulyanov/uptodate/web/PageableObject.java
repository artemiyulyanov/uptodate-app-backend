package me.artemiyulyanov.uptodate.web;

import lombok.*;
import me.artemiyulyanov.uptodate.models.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageableObject<T> {
    private Sort sort;
    private List<Specification<T>> specifications = new ArrayList<>();
    private int page, pageSize;

    public void addSpecification(Specification<T> specification) {
        specifications.add(specification);
    }

    public Specification<T> getCommonSpecification() {
        return specifications.stream()
                .reduce(Specification::and)
                .orElse(null);
    }

    public Pageable getPageable() {
        return PageRequest.of(page, pageSize, sort);
    }

    public static <T> PageableObject<T> of(Class<T> tClass, int page, int pageSize) {
        PageableObject<T> pageableObject = new PageableObject<>();

        pageableObject.setPage(page);
        pageableObject.setPageSize(pageSize);
        pageableObject.setSort(Sort.by(Sort.Order.asc("id")));

        return pageableObject;
    }
}