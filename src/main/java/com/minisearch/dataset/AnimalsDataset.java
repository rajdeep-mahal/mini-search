package com.minisearch.dataset;

import com.minisearch.model.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Animals Dataset - A comprehensive collection of animal information
 * Perfect for testing search functionality with focused, rich content
 */
public class AnimalsDataset {
    
    private static final Logger logger = LoggerFactory.getLogger(AnimalsDataset.class);
    
    /**
     * Get a comprehensive list of animal web pages
     */
    public static List<WebPage> getAnimalsDataset() {
        List<WebPage> animals = new ArrayList<>();
        
        // Mammals
        animals.add(createAnimalPage(
            "https://animals.com/lion",
            "Lion - King of the Jungle",
            "animals.com",
            "The lion (Panthera leo) is a large cat species in the genus Panthera. " +
            "Lions are apex predators and are known for their distinctive mane in males. " +
            "They live in social groups called prides and are found in sub-Saharan Africa. " +
            "Lions are excellent hunters, working together to take down large prey like zebras, wildebeest, and buffalo. " +
            "They are also known for their powerful roar which can be heard from up to 5 miles away."
        ));
        
        animals.add(createAnimalPage(
            "https://animals.com/elephant",
            "Elephant - Gentle Giants of the Savanna",
            "animals.com",
            "Elephants are the largest land animals on Earth. There are three species: " +
            "African bush elephant, African forest elephant, and Asian elephant. " +
            "They are known for their intelligence, long memory, and strong family bonds. " +
            "Elephants use their trunks for breathing, smelling, touching, grasping, and producing sound. " +
            "They are herbivores and can consume up to 300 pounds of vegetation daily."
        ));
        
        animals.add(createAnimalPage(
            "https://animals.com/dolphin",
            "Dolphin - Intelligent Marine Mammals",
            "animals.com",
            "Dolphins are highly intelligent marine mammals belonging to the family Delphinidae. " +
            "They are known for their playful behavior, complex communication, and problem-solving abilities. " +
            "Dolphins use echolocation to navigate and find food in the ocean. " +
            "They are social animals that live in pods and can swim at speeds up to 20 mph. " +
            "Dolphins are found in oceans worldwide and some species can live up to 50 years."
        ));
        
        animals.add(createAnimalPage(
            "https://animals.com/eagle",
            "Eagle - Majestic Birds of Prey",
            "animals.com",
            "Eagles are large birds of prey belonging to the family Accipitridae. " +
            "They are known for their excellent eyesight, powerful flight, and hunting skills. " +
            "Eagles have keen vision that allows them to spot prey from great distances. " +
            "They build large nests called eyries in tall trees or on cliffs. " +
            "Eagles are found on every continent except Antarctica and are symbols of strength and freedom."
        ));
        
        animals.add(createAnimalPage(
            "https://animals.com/shark",
            "Shark - Apex Ocean Predators",
            "animals.com",
            "Sharks are a group of elasmobranch fish characterized by a cartilaginous skeleton. " +
            "They have been swimming in Earth's oceans for over 400 million years. " +
            "Sharks have multiple rows of teeth that are constantly replaced throughout their lives. " +
            "They use electroreception to detect prey and can sense blood from miles away. " +
            "There are over 500 species of sharks, ranging from the tiny dwarf lanternshark to the massive whale shark."
        ));
        
        animals.add(createAnimalPage(
            "https://animals.com/butterfly",
            "Butterfly - Beautiful Flying Insects",
            "animals.com",
            "Butterflies are insects belonging to the order Lepidoptera. " +
            "They are known for their colorful wings and graceful flight patterns. " +
            "Butterflies undergo complete metamorphosis: egg, larva, pupa, and adult. " +
            "They feed on nectar from flowers and play important roles in pollination. " +
            "There are over 20,000 species of butterflies worldwide, with the monarch being one of the most famous."
        ));
        
        animals.add(createAnimalPage(
            "https://animals.com/penguin",
            "Penguin - Flightless Birds of the Antarctic",
            "animals.com",
            "Penguins are flightless birds that live primarily in the Southern Hemisphere. " +
            "They are excellent swimmers and can dive to depths of over 1,000 feet. " +
            "Penguins have waterproof feathers and a layer of fat to keep them warm in cold waters. " +
            "They form large colonies for breeding and protection from predators. " +
            "The emperor penguin is the largest species and can survive temperatures as low as -40°C."
        ));
        
        animals.add(createAnimalPage(
            "https://animals.com/gorilla",
            "Gorilla - Gentle Giants of the Forest",
            "animals.com",
            "Gorillas are the largest living primates and are closely related to humans. " +
            "They are herbivores that live in family groups led by a dominant silverback male. " +
            "Gorillas are highly intelligent and can use tools and learn sign language. " +
            "They are found in the forests of central Africa and are critically endangered. " +
            "Gorillas are peaceful animals that rarely show aggression unless threatened."
        ));
        
        animals.add(createAnimalPage(
            "https://animals.com/octopus",
            "Octopus - Intelligent Cephalopods",
            "animals.com",
            "Octopuses are highly intelligent invertebrates belonging to the class Cephalopoda. " +
            "They have three hearts, blue blood, and can change color and texture for camouflage. " +
            "Octopuses are masters of escape and can squeeze through tiny openings. " +
            "They use their eight arms to move, hunt, and manipulate objects. " +
            "Some species can solve complex puzzles and remember solutions for months."
        ));
        
        animals.add(createAnimalPage(
            "https://animals.com/tiger",
            "Tiger - Striped Big Cats",
            "animals.com",
            "Tigers are the largest wild cats and are known for their distinctive orange fur with black stripes. " +
            "They are solitary hunters that can take down prey much larger than themselves. " +
            "Tigers are excellent swimmers and often hunt in water. " +
            "They are found in Asia and are critically endangered due to habitat loss and poaching. " +
            "Each tiger's stripe pattern is unique, like human fingerprints."
        ));
        
        animals.add(createAnimalPage(
            "https://animals.com/whale",
            "Whale - Giants of the Ocean",
            "animals.com",
            "Whales are the largest animals that have ever lived on Earth. " +
            "They are marine mammals that breathe air and give birth to live young. " +
            "Whales use echolocation to navigate and communicate over vast distances. " +
            "The blue whale can grow up to 100 feet long and weigh over 200 tons. " +
            "Whales are found in all oceans and some species migrate thousands of miles annually."
        ));
        
        animals.add(createAnimalPage(
            "https://animals.com/snake",
            "Snake - Legless Reptiles",
            "animals.com",
            "Snakes are elongated, limbless reptiles belonging to the suborder Serpentes. " +
            "They are found on every continent except Antarctica and in most oceans. " +
            "Snakes use their forked tongues to smell and detect chemical signals. " +
            "Some species are venomous and use venom to hunt and defend themselves. " +
            "Snakes can unhinge their jaws to swallow prey much larger than their head."
        ));
        
        animals.add(createAnimalPage(
            "https://animals.com/owl",
            "Owl - Nocturnal Birds of Prey",
            "animals.com",
            "Owls are nocturnal birds of prey known for their silent flight and excellent night vision. " +
            "They have large eyes that are fixed in their sockets, requiring them to turn their heads. " +
            "Owls have exceptional hearing and can locate prey by sound alone. " +
            "They are found worldwide and come in various sizes, from the tiny elf owl to the large Eurasian eagle-owl. " +
            "Owls are symbols of wisdom in many cultures and are featured in folklore worldwide."
        ));
        
        logger.info("Created {} animal pages for the dataset", animals.size());
        return animals;
    }
    
    /**
     * Get animals by category
     */
    public static List<WebPage> getAnimalsByCategory(String category) {
        List<WebPage> allAnimals = getAnimalsDataset();
        List<WebPage> categoryAnimals = new ArrayList<>();
        
        String lowerCategory = category.toLowerCase();
        
        for (WebPage animal : allAnimals) {
            if (animal.getContent().toLowerCase().contains(lowerCategory) ||
                animal.getTitle().toLowerCase().contains(lowerCategory)) {
                categoryAnimals.add(animal);
            }
        }
        
        return categoryAnimals;
    }
    
    /**
     * Get animals by habitat
     */
    public static List<WebPage> getAnimalsByHabitat(String habitat) {
        List<WebPage> allAnimals = getAnimalsDataset();
        List<WebPage> habitatAnimals = new ArrayList<>();
        
        String lowerHabitat = habitat.toLowerCase();
        
        for (WebPage animal : allAnimals) {
            if (animal.getContent().toLowerCase().contains(lowerHabitat)) {
                habitatAnimals.add(animal);
            }
        }
        
        return habitatAnimals;
    }
    
    /**
     * Search animals by characteristics
     */
    public static List<WebPage> searchAnimalsByCharacteristics(String... characteristics) {
        List<WebPage> allAnimals = getAnimalsDataset();
        List<WebPage> matchingAnimals = new ArrayList<>();
        
        for (WebPage animal : allAnimals) {
            boolean matches = true;
            String content = animal.getContent().toLowerCase();
            
            for (String characteristic : characteristics) {
                if (!content.contains(characteristic.toLowerCase())) {
                    matches = false;
                    break;
                }
            }
            
            if (matches) {
                matchingAnimals.add(animal);
            }
        }
        
        return matchingAnimals;
    }
    
    /**
     * Create an animal web page with the given details
     */
    private static WebPage createAnimalPage(String url, String title, String domain, String content) {
        WebPage page = new WebPage(url);
        page.setTitle(title);
        page.setDomain(domain);
        page.setContent(content);
        return page;
    }
    
    /**
     * Get dataset statistics
     */
    public static void printDatasetStats() {
        List<WebPage> animals = getAnimalsDataset();
        logger.info("=== Animals Dataset Statistics ===");
        logger.info("Total animals: {}", animals.size());
        
        // Count by habitat
        long oceanAnimals = animals.stream()
            .filter(a -> a.getContent().toLowerCase().contains("ocean"))
            .count();
        long forestAnimals = animals.stream()
            .filter(a -> a.getContent().toLowerCase().contains("forest"))
            .count();
        long savannaAnimals = animals.stream()
            .filter(a -> a.getContent().toLowerCase().contains("savanna"))
            .count();
        
        logger.info("Ocean animals: {}", oceanAnimals);
        logger.info("Forest animals: {}", forestAnimals);
        logger.info("Savanna animals: {}", savannaAnimals);
        
        // Count by type
        long mammals = animals.stream()
            .filter(a -> a.getContent().toLowerCase().contains("mammal"))
            .count();
        long birds = animals.stream()
            .filter(a -> a.getContent().toLowerCase().contains("bird"))
            .count();
        long fish = animals.stream()
            .filter(a -> a.getContent().toLowerCase().contains("fish"))
            .count();
        
        logger.info("Mammals: {}", mammals);
        logger.info("Birds: {}", birds);
        logger.info("Fish: {}", fish);
    }
}
