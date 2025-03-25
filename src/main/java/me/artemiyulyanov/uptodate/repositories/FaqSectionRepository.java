package me.artemiyulyanov.uptodate.repositories;

import me.artemiyulyanov.uptodate.models.FaqSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqSectionRepository extends JpaRepository<FaqSection, Long> {
}