package engine.exceptions;

public class LayerNotFoundError extends RuntimeException {
    
    String LayerNotFound = "unknown";
    
    public LayerNotFoundError(String LayerNotFound) {
        this.LayerNotFound = LayerNotFound;
    }
}
