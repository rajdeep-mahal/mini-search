package com.minisearch.dataset;

import com.minisearch.model.WebPage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for AnimalsDataset
 */
public class AnimalsDatasetTest {
    
    @Test
    public void testGetAnimalsDataset() {
        List<WebPage> animals = AnimalsDataset.getAnimalsDataset();
        
        // Should have 13 animals
        assertEquals(13, animals.size());
        
        // Check that we have animals from different categories
        boolean hasLion = animals.stream().anyMatch(a -> a.getTitle().contains("Lion"));
        boolean hasElephant = animals.stream().anyMatch(a -> a.getTitle().contains("Elephant"));
        boolean hasDolphin = animals.stream().anyMatch(a -> a.getTitle().contains("Dolphin"));
        
        assertTrue(hasLion, "Should contain Lion");
        assertTrue(hasElephant, "Should contain Elephant");
        assertTrue(hasDolphin, "Should contain Dolphin");
    }
    
    @Test
    public void testGetAnimalsByCategory() {
        List<WebPage> mammals = AnimalsDataset.getAnimalsByCategory("mammal");
        
        // Should find several mammals
        assertTrue(mammals.size() > 0, "Should find mammals");
        
        // All results should contain "mammal" in content
        for (WebPage mammal : mammals) {
            assertTrue(mammal.getContent().toLowerCase().contains("mammal"), 
                      "Mammal should contain 'mammal' in content");
        }
    }
    
    @Test
    public void testGetAnimalsByHabitat() {
        List<WebPage> oceanAnimals = AnimalsDataset.getAnimalsByHabitat("ocean");
        
        // Should find ocean animals
        assertTrue(oceanAnimals.size() > 0, "Should find ocean animals");
        
        // All results should contain "ocean" in content
        for (WebPage animal : oceanAnimals) {
            assertTrue(animal.getContent().toLowerCase().contains("ocean"), 
                      "Ocean animal should contain 'ocean' in content");
        }
    }
    
    @Test
    public void testSearchAnimalsByCharacteristics() {
        List<WebPage> intelligentAnimals = AnimalsDataset.searchAnimalsByCharacteristics("intelligent");
        
        // Should find intelligent animals
        assertTrue(intelligentAnimals.size() > 0, "Should find intelligent animals");
        
        // All results should contain "intelligent" in content
        for (WebPage animal : intelligentAnimals) {
            assertTrue(animal.getContent().toLowerCase().contains("intelligent"), 
                      "Intelligent animal should contain 'intelligent' in content");
        }
    }
    
    @Test
    public void testAnimalContentQuality() {
        List<WebPage> animals = AnimalsDataset.getAnimalsDataset();
        
        for (WebPage animal : animals) {
            // Each animal should have meaningful content
            assertNotNull(animal.getTitle(), "Animal should have a title");
            assertNotNull(animal.getContent(), "Animal should have content");
            assertTrue(animal.getContent().length() > 100, "Animal content should be substantial");
            assertNotNull(animal.getDomain(), "Animal should have a domain");
            assertNotNull(animal.getUrl(), "Animal should have a URL");
        }
    }
}
