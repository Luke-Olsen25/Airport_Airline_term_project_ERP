package erp.HumanResources;

import java.util.List;

/**
 * What Human Resources needs to know about airport gates in order to assign gate agents.
 * HR depends on this interface instead of on Airport Operations' classes or files.
 */
public interface IGateLookup {
    List<String> getGateIds();
}
