package edu.rutmiit.incident.model;

public record IncidentState(boolean open, boolean resolved, Long incidentId,
        int consecutiveFailures, int consecutiveSuccesses) {
    public static IncidentState empty() {
        return new IncidentState(false, false, null, 0, 0);
    }

    public IncidentState onFailure() {
        return new IncidentState(open, resolved, incidentId, consecutiveFailures + 1, 0);
    }

    public IncidentState onSuccess() {
        return new IncidentState(open, resolved, incidentId, 0, consecutiveSuccesses + 1);
    }

    public IncidentState opened(Long incidentId) {
        return new IncidentState(true, false, incidentId, consecutiveFailures, 0);
    }

    public IncidentState markResolved() {
        return new IncidentState(false, true, incidentId, 0, consecutiveSuccesses);
    }

    public IncidentState reopened() {
        return new IncidentState(true, false, incidentId, consecutiveFailures, 0);
    }

    public IncidentState closed() {
        return empty();
    }
}
