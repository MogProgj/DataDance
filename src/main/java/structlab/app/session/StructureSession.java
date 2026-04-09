package structlab.app.session;

public interface StructureSession {
    String getStructureId();
    String getImplementationId();
    void close();
}
