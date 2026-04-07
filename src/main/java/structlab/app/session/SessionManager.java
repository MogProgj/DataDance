package structlab.app.session;

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
