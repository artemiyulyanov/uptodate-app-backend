package me.artemiyulyanov.uptodate.repositories;

import me.artemiyulyanov.uptodate.models.FaqItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqItemRepository extends JpaRepository<FaqItem, Long> {
}