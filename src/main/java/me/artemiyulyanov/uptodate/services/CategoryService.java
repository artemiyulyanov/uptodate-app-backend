package me.artemiyulyanov.uptodate.services;

import jakarta.annotation.PostConstruct;
import me.artemiyulyanov.uptodate.models.Category;
import me.artemiyulyanov.uptodate.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {
    public static final List<Category> CATEGORIES = List.of(
            Category.of("Technology", "Технологии", "Artificial Intelligence", "Искусственный интеллект"),
            Category.of("Technology", "Технологии", "Software Development", "Разработка ПО"),
            Category.of("Technology", "Технологии", "Cybersecurity", "Кибербезопасность"),
            Category.of("Technology", "Технологии", "Blockchain", "Блокчейн"),
            Category.of("Technology", "Технологии", "Cloud Computing", "Облачные вычисления"),
            Category.of("Technology", "Технологии", "Virtual Reality & Augmented Reality", "Виртуальная реальность"),
            Category.of("Technology", "Технологии", "Internet of Things (IoT)", "Интернет вещей"),
            Category.of("Technology", "Технологии", "Big Data", "Большие данные"),
            Category.of("Technology", "Технологии", "Wearable Tech", "Носимые технологии"),

            Category.of("Health & Wellness", "Здоровье и благополучие", "Mental Health", "Ментальное здоровье"),
            Category.of("Health & Wellness", "Здоровье и благополучие", "Nutrition", "Питание"),
            Category.of("Health & Wellness", "Здоровье и благополучие", "Fitness & Exercise", "Фитнес и упражнения"),
            Category.of("Health & Wellness", "Здоровье и благополучие", "Chronic Illness", "Хронические заболевания"),
            Category.of("Health & Wellness", "Здоровье и благополучие", "Holistic Medicine", "Холистическая медицина"),
            Category.of("Health & Wellness", "Здоровье и благополучие", "Medical Research", "Медицинские исследования"),
            Category.of("Health & Wellness", "Здоровье и благополучие", "Sleep Health", "Здоровый сон"),
            Category.of("Health & Wellness", "Здоровье и благополучие", "Skin Care", "Уход за кожей"),
            Category.of("Health & Wellness", "Здоровье и благополучие", "Women's Health", "Женское здоровье"),
            Category.of("Health & Wellness", "Здоровье и благополучие", "Physical Therapy", "Физиотерапия"),

            Category.of("Business & Finance", "Бизнес и финансы", "Entrepreneurship", "Предпринимательство"),
            Category.of("Business & Finance", "Бизнес и финансы", "Investing & Stock Market", "Инвестирование и фондовый рынок"),
            Category.of("Business & Finance", "Бизнес и финансы", "Personal Finance", "Личные финансы"),
            Category.of("Business & Finance", "Бизнес и финансы", "Cryptocurrency", "Криптовалюта"),
            Category.of("Business & Finance", "Бизнес и финансы", "Corporate Strategies", "Корпоративные стратегии"),
            Category.of("Business & Finance", "Бизнес и финансы", "Startups", "Стартапы"),
            Category.of("Business & Finance", "Бизнес и финансы", "E-commerce", "Электронная коммерция"),
            Category.of("Business & Finance", "Бизнес и финансы", "Real Estate", "Недвижимость"),
            Category.of("Business & Finance", "Бизнес и финансы", "Marketing & Advertising", "Маркетинг и реклама"),
            Category.of("Business & Finance", "Бизнес и финансы", "Leadership & Management", "Лидерство и управление"),

            Category.of("Science & Innovation", "Наука и инновации", "Space Exploration", "Освоение космоса"),
            Category.of("Science & Innovation", "Наука и инновации", "Environmental Science", "Науки об окружающей среде"),
            Category.of("Science & Innovation", "Наука и инновации", "Physics", "Физика"),
            Category.of("Science & Innovation", "Наука и инновации", "Chemistry", "Химия"),
            Category.of("Science & Innovation", "Наука и инновации", "Biotechnology", "Биотехнологии"),
            Category.of("Science & Innovation", "Наука и инновации", "Climate Change", "Изменение климата"),
            Category.of("Science & Innovation", "Наука и инновации", "Renewable Energy", "Возобновляемая энергия"),
            Category.of("Science & Innovation", "Наука и инновации", "Medical Breakthroughs", "Прорывы в медицине"),
            Category.of("Science & Innovation", "Наука и инновации", "Quantum Computing", "Квантовые вычисления"),
            Category.of("Science & Innovation", "Наука и инновации", "Robotics", "Робототехника"),

            Category.of("Education & Learning", "Образование и учёба", "Online Learning", "Онлайн-обучение"),
            Category.of("Education & Learning", "Образование и учёба", "STEM Education", "STEM-образование"),
            Category.of("Education & Learning", "Образование и учёба", "Language Learning", "Изучение языков"),
            Category.of("Education & Learning", "Образование и учёба", "Skill Development", "Развитие навыков"),
            Category.of("Education & Learning", "Образование и учёба", "Educational Technology", "Образовательные технологии"),
            Category.of("Education & Learning", "Образование и учёба", "Classroom Strategies", "Стратегии в классе"),
            Category.of("Education & Learning", "Образование и учёба", "Higher Education", "Высшее образование"),
            Category.of("Education & Learning", "Образование и учёба", "Early Childhood Education", "Дошкольное образование"),
            Category.of("Education & Learning", "Образование и учёба", "Adult Learning", "Обучение для взрослых"),
            Category.of("Education & Learning", "Образование и учёба", "Special Education", "Специальное образование"),

            Category.of("Arts & Culture", "Искусство и культура", "Fine Arts (Painting, Sculpture)", "Изобразительное искусство (рисование, скульптинг)"),
            Category.of("Arts & Culture", "Искусство и культура", "Music", "Музыка"),
            Category.of("Arts & Culture", "Искусство и культура", "Literature", "Литература"),
            Category.of("Arts & Culture", "Искусство и культура", "Film & Television", "Фильмы и телевидение"),
            Category.of("Arts & Culture", "Искусство и культура", "Performing Arts (Theater, Dance)", "Исполнительное искусство (театр, танцы)"),
            Category.of("Arts & Culture", "Искусство и культура", "Photography", "Фотография"),
            Category.of("Arts & Culture", "Искусство и культура", "Cultural Heritage", "Культурное наследие"),
            Category.of("Arts & Culture", "Искусство и культура", "Fashion", "Мода"),
            Category.of("Arts & Culture", "Искусство и культура", "Architecture", "Архитектура"),
            Category.of("Arts & Culture", "Искусство и культура", "Street Art", "Уличное искусство"),

            Category.of("Travel & Adventure", "Путешествия и приключения", "Budget Travel", "Бюджетные путешествия"),
            Category.of("Travel & Adventure", "Путешествия и приключения", "Luxury Travel", "Роскошные путешествия"),
            Category.of("Travel & Adventure", "Путешествия и приключения", "Adventure Tourism", "Экстремальный туризм"),
            Category.of("Travel & Adventure", "Путешествия и приключения", "Cultural Travel", "Культурные путешествия"),
            Category.of("Travel & Adventure", "Путешествия и приключения", "Sustainable Travel", "Устойчивые путешествия"),
            Category.of("Travel & Adventure", "Путешествия и приключения", "Solo Travel", "Путешествия в одиночку"),
            Category.of("Travel & Adventure", "Путешествия и приключения", "Travel Tips & Hacks", "Советы и рекомендации для путешественников"),
            Category.of("Travel & Adventure", "Путешествия и приключения", "Travel Photography", "Туристическая фотография"),
            Category.of("Travel & Adventure", "Путешествия и приключения", "Backpacking", "Пеший туризм"),
            Category.of("Travel & Adventure", "Путешествия и приключения", "City Guides", "Путеводители по городам"),

            Category.of("Food & Drink", "Еда и напитки", "Recipes", "Рецепты"),
            Category.of("Food & Drink", "Еда и напитки", "Baking", "Выпечка"),
            Category.of("Food & Drink", "Еда и напитки", "Vegan & Vegetarian", "Веганство и вегетарианство"),
            Category.of("Food & Drink", "Еда и напитки", "World Cuisines", "Блюда мировой кухни"),
            Category.of("Food & Drink", "Еда и напитки", "Wine & Beer", "Вина и пиво"),
            Category.of("Food & Drink", "Еда и напитки", "Street Food", "Уличная еда"),
            Category.of("Food & Drink", "Еда и напитки", "Healthy Eating", "Здоровое питание"),
            Category.of("Food & Drink", "Еда и напитки", "Food Science", "Наука о еде"),
            Category.of("Food & Drink", "Еда и напитки", "Culinary Techniques", "Кулинарные техники"),
            Category.of("Food & Drink", "Еда и напитки", "Restaurant Reviews", "Отзывы о ресторанах"),

            Category.of("Lifestyle", "Образ жизни", "Home & Interior Design", "Дизайн дома и интерьера"),
            Category.of("Lifestyle", "Образ жизни", "Fashion & Style", "Мода и стиль"),
            Category.of("Lifestyle", "Образ жизни", "Self-Improvement", "Саморазвитие"),
            Category.of("Lifestyle", "Образ жизни", "Relationships & Dating", "Отношения и свидания"),
            Category.of("Lifestyle", "Образ жизни", "Work-Life Balance", "Баланс между работой и личной жизнью"),
            Category.of("Lifestyle", "Образ жизни", "Sustainability in Lifestyle", "Устойчивость в образе жизни"),
            Category.of("Lifestyle", "Образ жизни", "Minimalism", "Минимализм"),
            Category.of("Lifestyle", "Образ жизни", "Parenting", "Воспитание детей"),
            Category.of("Lifestyle", "Образ жизни", "Personal Growth", "Личностный рост"),
            Category.of("Lifestyle", "Образ жизни", "Mindfulness", "Осознанность"),

            Category.of("Sports & Recreation", "Спорт и отдых", "Football (Soccer)", "Футбол"),
            Category.of("Sports & Recreation", "Спорт и отдых", "Basketball", "Баскетбол"),
            Category.of("Sports & Recreation", "Спорт и отдых", "Tennis", "Теннис"),
            Category.of("Sports & Recreation", "Спорт и отдых", "Extreme Sports", "Экстремальный спорт"),
            Category.of("Sports & Recreation", "Спорт и отдых", "Olympics", "Олимпийские игры"),
            Category.of("Sports & Recreation", "Спорт и отдых", "Martial Arts", "Боевые искусства"),
            Category.of("Sports & Recreation", "Спорт и отдых", "Fitness & Bodybuilding", "Фитнес и бодибилдинг"),
            Category.of("Sports & Recreation", "Спорт и отдых", "Water Sports", "Водные виды спорта"),
            Category.of("Sports & Recreation", "Спорт и отдых", "Esports", "Киберспорт"),
            Category.of("Sports & Recreation", "Спорт и отдых", "Running & Marathon", "Бег и марафоны")
    );

    @Autowired
    private CategoryRepository categoryRepository;

    @PostConstruct
    public void init() {
        if (categoryRepository.count() > 0) return;

        List<Category> categories = CATEGORIES;
        categoryRepository.saveAll(categories);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public List<Category> findByParent(String parent) {
        return categoryRepository.findByParentInEnglishOrRussian(parent);
    }

    public Optional<Category> findByName(String name) {
        return categoryRepository.findByNameInEnglishOrRussian(name);
    }
}