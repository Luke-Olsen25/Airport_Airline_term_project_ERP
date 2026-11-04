package erp.AircraftManagement;

import java.util.List;

public interface IAircraftRepository {
    void initializeStorage();
    boolean existsByIdentifiers(String tailNumber, String serialNumber);
    boolean save(Aircraft aircraft);
    List<String> getAllAircraftRecords();
}