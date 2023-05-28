package banking.entity;

public interface Controller {
    void request(Command userCommand);
    void createCard();
    void closeCard();
    void loginToAccount();
    void logoutFromAccount();
    void showBalance();
    void addIncome();
    void doTransfer();
    void terminate();
    void doInvalidCommandFlow();

}
