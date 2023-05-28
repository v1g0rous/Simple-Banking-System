package banking.entity;

public class CardValidationResult {
    private boolean isValid;
    private String message;

    public CardValidationResult(boolean isValid, String message) {
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
