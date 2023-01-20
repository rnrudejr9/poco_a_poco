package teamproject.pocoapoco.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import teamproject.pocoapoco.repository.CrewRepository;
import teamproject.pocoapoco.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

class CrewServiceTest {



    @Mock
    private CrewRepository crewRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CrewService crewService = new CrewService(crewRepository, userRepository);

    @Test
    void addCrew() {
    }

    @Test
    void updateCrew() {
    }

    @Test
    void deleteCrew() {
    }

    @Test
    void detailCrew() {
    }

    @Test
    void allCrew() {
    }

    @Test
    void allCrewWithSport() {
    }

    @Test
    void testToString() {
    }
}