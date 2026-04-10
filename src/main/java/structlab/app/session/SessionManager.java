package structlab.app.session;

import structlab.app.comparison.ComparisonSession;

import java.util.Optional;

public class SessionManager {
    private StructureSession activeSession = null;

    public boolean hasActiveSession() {
        return activeSession != null;
    }

    public Optional<StructureSession> getActiveSession() {
        return Optional.ofNullable(activeSession);
    }

    public Optional<ActiveStructureSession> getActiveStructureSession() {
        if (activeSession instanceof ActiveStructureSession ass) {
            return Optional.of(ass);
        }
        return Optional.empty();
    }

    public Optional<ComparisonSession> getComparisonSession() {
        if (activeSession instanceof ComparisonSession cs) {
            return Optional.of(cs);
        }
        return Optional.empty();
    }

    public boolean isComparisonMode() {
        return activeSession instanceof ComparisonSession;
    }

    public void startSession(StructureSession session) {
        if (this.activeSession != null) {
            this.activeSession.close();
        }
        this.activeSession = session;
    }

    public void clearSession() {
        if (this.activeSession != null) {
            this.activeSession.close();
            this.activeSession = null;
        }
    }
}
