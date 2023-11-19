package com.example.hikemate.Database;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.hikemate.Database.Dao.HikeDao;
import com.example.hikemate.Database.Dao.ObservationDao;
import com.example.hikemate.Database.Model.Animal;
import com.example.hikemate.Database.Dao.AnimalDao;
import com.example.hikemate.Database.Model.Hike;
import com.example.hikemate.Database.Model.Observation;
import com.example.hikemate.Database.Model.Plant;
import com.example.hikemate.Database.Dao.PlantDao;
import com.example.hikemate.Database.Model.Skill;
import com.example.hikemate.Database.Dao.SkillDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HikeDatabaseTest {

    private HikeDatabase hikeDatabase;
    private AnimalDao animalDao;
    private PlantDao plantDao;
    private SkillDao skillDao;
    private HikeDao hikeDao;
    private ObservationDao observationDao;

    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        hikeDatabase = Room.inMemoryDatabaseBuilder(context, HikeDatabase.class).build();
        animalDao = hikeDatabase.animalDao();
        plantDao = hikeDatabase.plantDao();
        skillDao = hikeDatabase.skillDao();
        hikeDao = hikeDatabase.hikeDao();
        observationDao = hikeDatabase.observationDao();
    }

    @After
    public void closeDatabase() {
        hikeDatabase.close();
    }

    @Test
    public void insertAndGetAnimal() {
        Animal animal = new Animal("https://example.com/image.jpg", "Test Animal", "Test Species", "9/10", "-", "-",
                "Test Description EN", "Test Description VN");
        animalDao.insert(animal);

        List<Animal> animals = animalDao.getAnimal();
        assertEquals(1, animals.size());
        Animal retrievedAnimal = animals.get(0);
        assertEquals("Test Animal", retrievedAnimal.getNameVi());
        assertEquals("Test Species", retrievedAnimal.getNameEn());
        assertEquals("9/10", retrievedAnimal.getDanger());
        assertEquals("Test Description EN", retrievedAnimal.getDescriptionVi());
        assertEquals("Test Description VN", retrievedAnimal.getDescriptionEn());
    }

    @Test
    public void insertAndGetPlant() {
        Plant plant = new Plant("https://example.com/image.jpg", "Test Plant", "Test Species", "9/10", "-", "-",
                "Test Description EN", "Test Description VN");
        plantDao.insert(plant);

        List<Plant> plants = plantDao.getPlan();
        assertEquals(1, plants.size());
        Plant retrievedPlant = plants.get(0);
        assertEquals("Test Plant", retrievedPlant.getNameVi());
        assertEquals("Test Species", retrievedPlant.getNameEn());
        assertEquals("9/10", retrievedPlant.getDanger());
        assertEquals("Test Description EN", retrievedPlant.getDescriptionVi());
        assertEquals("Test Description VN", retrievedPlant.getDescriptionEn());
    }

    @Test
    public void insertAndGetSkill() {
        Skill skill = new Skill("https://example.com/image.jpg", "Test Skill", "Test Category", "9/10", "-", "-",
                "Test Description EN", "Test Description VN");
        skillDao.insert(skill);

        List<Skill> skills = skillDao.getSkill();
        assertEquals(1, skills.size());
        Skill retrievedSkill = skills.get(0);
        assertEquals("Test Skill", retrievedSkill.getNameVi());
        assertEquals("Test Category", retrievedSkill.getNameEn());
        assertEquals("9/10", retrievedSkill.getDanger());
        assertEquals("Test Description EN", retrievedSkill.getDescriptionVi());
        assertEquals("Test Description VN", retrievedSkill.getDescriptionEn());
    }

    @Test
    public void testAddAndRetrieveHike() {
        // Create a Hike for testing
        Hike hike = new Hike("Test Hike", "Test Location", 123456L, true, 10.5, 2, "Test Description");

        // Add the Hike to the database
        hikeDao.insert(hike);

        // Retrieve the Hike from the database
        List<Hike> hikesFromDb = hikeDao.getAllHikes();

        // Ensure that the list is not null and contains the added Hike
        assertNotNull(hikesFromDb);
        assertEquals(1, hikesFromDb.size());

        // Retrieve the Hike from the list
        Hike retrievedHike = hikesFromDb.get(0);

        // Verify that the retrieved Hike is equal to the original
        assertEquals(1, retrievedHike.getId());
        assertEquals(hike.getHikeName(), retrievedHike.getHikeName());
        assertEquals(hike.getLocation(), retrievedHike.getLocation());
        assertEquals(hike.getDate(), retrievedHike.getDate());
        assertEquals(hike.isParkingAvailable(), retrievedHike.isParkingAvailable());
        assertEquals(hike.getLength(), retrievedHike.getLength(), 0);
        assertEquals(hike.getDifficulty(), retrievedHike.getDifficulty());
        assertEquals(hike.getDescription(), retrievedHike.getDescription());
    }

    @Test
    public void writeAndReadObservation() {
        Hike hike = new Hike("Test Hike", "Test Location", 123456L, true, 10.5, 2, "Test Description");

        // Add the Hike to the database
        hikeDao.insert(hike);
        int hikeId = hikeDao.getAllHikes().get(0).getId();; // Replace with a valid hike ID from your database
        Observation observation = new Observation(hikeId, "Test Observation", System.currentTimeMillis(), "Test Comment");

        long insertedId = observationDao.insert(observation);
        Observation loadedObservation = observationDao.getAllObservation().get(0);

        assertNotNull(loadedObservation);
        assertEquals(observation.getHikeId(), loadedObservation.getHikeId());
        assertEquals(observation.getName(), loadedObservation.getName());
        assertEquals(observation.getTimeObservation(), loadedObservation.getTimeObservation());
        assertEquals(observation.getAdditionalComment(), loadedObservation.getAdditionalComment());
    }
}