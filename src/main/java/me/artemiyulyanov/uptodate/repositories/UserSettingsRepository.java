package me.artemiyulyanov.uptodate.repositories;

import me.artemiyulyanov.uptodate.models.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
}