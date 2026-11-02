package erp.TicketSales;

import java.util.List;

/** Storage for issued tickets. */
public interface ITicketRepository {
    void initializeStorage();
    String nextTicketNumber();
    boolean save(Ticket ticket);
    List<Ticket> loadAll();
}
