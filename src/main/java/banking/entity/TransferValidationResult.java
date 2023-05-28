package banking.entity;

public class TransferValidationResult {
    private boolean isValid;
    private String message;

    public TransferValidationResult(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }

    public boolean isValid() {
        return isValid;
    }
    public String getMessage() {
        return message;
    }

}
