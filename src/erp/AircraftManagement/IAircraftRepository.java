package erp.AircraftManagement;

import java.util.List;
import java.util.Optional;

public interface IAircraftRepository {
    void initializeStorage();
    boolean existsByIdentifiers(String tailNumber, String serialNumber);
    boolean save(Aircraft aircraft);
    List<String> getAllAircraftRecords();
    boolean deleteByTailNumber(String tailNumber);
    Optional<Aircraft> findByTailNumber(String tailNumber);
    boolean updateAircraft(Aircraft updatedAircraft);

}