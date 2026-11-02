package erp.AircraftManagement;

public interface IAircraftRepository {
    void initializeStorage();
    boolean existsByIdentifiers(String tailNumber, String serialNumber);
    boolean save(Aircraft aircraft);
}