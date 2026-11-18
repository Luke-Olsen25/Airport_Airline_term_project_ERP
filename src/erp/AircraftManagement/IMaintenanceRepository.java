package erp.AircraftManagement;

import java.util.List;
import java.util.Optional;

public interface IMaintenanceRepository {
    void initializeStorage();
    boolean save(MaintenanceRecord record);
    boolean update(MaintenanceRecord record);
    Optional<MaintenanceRecord> findScheduledByTailNumber(String tailNumber);
    List<MaintenanceRecord> findAll();
}
