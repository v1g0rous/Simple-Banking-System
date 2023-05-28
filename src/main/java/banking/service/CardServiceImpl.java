package banking.service;

import banking.DAO.CardDAO;
import banking.entity.Card;
import banking.entity.CardValidationResult;
import banking.entity.Log;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// responsibility: execute business logic
public class CardServiceImpl implements CardService {
    public static final String CARD_BIN = "400000";


    public static final String INVALID_CARD_NUMBER_PROVIDED_ERROR = "Probably you made a mistake in the card number. Please try again!\n";
    public static final String INVALID_CARD_NUMBER_FORMAT = "Invalid card number format, please try again\n";

    public static final String CARD_DOES_NOT_EXIST_IN_DB_ERROR = "Such a card does not exist.\n";
    private CardDAO cardDAO;

    public CardServiceImpl(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
    }

    public Card createCard() throws Exception {

        String accountNumber = generateAccountNumber();
        String pinCode = generatePinCode();
        String checksum = generateCheckSum(accountNumber);
        String cardNumber = generateCardNumber(accountNumber,checksum); // throws exception if cardNumber failed to generate

        Card card = new Card(cardNumber, pinCode);
        cardDAO.insertCard(card);

        return card;
    }

    private String generateCardNumber(String accountNumber, String checksum) throws Exception {

        if (CARD_BIN == null || accountNumber == null || checksum == null) {
            Exception exception = new RuntimeException("Failed to create card number - " +  CARD_BIN + accountNumber + checksum );
            new Log(
                    "Card can't be generated for " +
                            "BIN="+CARD_BIN+","+
                            "accNumber="+accountNumber+","+
                            "checkSum="+checksum, exception);
            throw exception;

        }
        return CARD_BIN + accountNumber + checksum;
    }

    private String generatePinCode() {
        StringBuilder pinCode = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            pinCode.append(random.nextInt(10));
        }

        return pinCode.toString();
    }

    private String generateCheckSum(String accountNumber) {
        int result = generateCheckSumByLuhnAlgo(accountNumber);
        return String.valueOf(result);
    }

    private int generateCheckSumByLuhnAlgo(String accountNumber) {
        int checksum;

        String cardBinWithAccNumber = CARD_BIN + accountNumber;

        int accountNumberDigitsSum = getAccountNumberDigitsSumByLuhnAlgo(cardBinWithAccNumber);

        checksum = accountNumberDigitsSum % 10 == 0 ? 0 : 10 - accountNumberDigitsSum % 10;

        return checksum;
    }

    private int getAccountNumberDigitsSumByLuhnAlgo(String cardBinWithAccNumber) {
        int accountNumberDigitsSum = 0;

        try {
            for (int i = 0; i < cardBinWithAccNumber.length(); i++) {
                int digit = Character.getNumericValue(cardBinWithAccNumber.charAt(i));

                int indexOffset = (i + 1); // offset to prepare index for Luhn algo, which starts with 1

                if (indexOffset % 2 != 0) { // Step1: multiply odd indexes by 2
                    digit *= 2;
                }

                if (digit > 9) { // Step2: subtract 9 to numbers over 9
                    digit -= 9;
                }
                accountNumberDigitsSum += digit; // Step3: sum all numbers
            }
        } catch (Exception e) {
            new Log("Failed to calculate accountNumberDigitsSum by Luhn algo", e);
            throw new RuntimeException(e);
        }

        return accountNumberDigitsSum;
    }



    public Card getCardByCardNumber(String authorizedCardNumber) {
        return cardDAO.getCard(authorizedCardNumber);
    }


    public CardValidationResult validateCardByNumber(String cardNumber) {

        boolean cardNumberFormatIsValid = isCardNumberValidByFormat(cardNumber);
        if (!cardNumberFormatIsValid) {
            return new CardValidationResult(false, INVALID_CARD_NUMBER_FORMAT);
        }

        boolean isCardNumberValidByLuhnAlgo = checkCardNumberByLuhnAlgo(cardNumber);
        if (!isCardNumberValidByLuhnAlgo) {
            return new CardValidationResult(false, INVALID_CARD_NUMBER_PROVIDED_ERROR);
        }

        boolean cardExistsInDB = isCardExistingInDB(cardNumber);
        if (!cardExistsInDB) {
            return new CardValidationResult(false, CARD_DOES_NOT_EXIST_IN_DB_ERROR);
        }

        return new CardValidationResult(true, "");
    }

    public boolean isCardExistingInDB(String cardNumber) {
        Card card = this.getCardByCardNumber(cardNumber);
        return card != null;
    }

    private static boolean isCardNumberValidByFormat(String userCardNumber) {
        if (userCardNumber == null || userCardNumber.length() < 16 || !isCardNumberDigitsOnly(userCardNumber)) {
            return false;
        }

        return true;
    }

    private static boolean isCardNumberDigitsOnly(String userCardNumber) {
        boolean cardNumberIsDigital = false;

        String regex = "^[0-9]+$";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(userCardNumber);
        if (matcher.find()) {
            cardNumberIsDigital = true;
        }

        return cardNumberIsDigital;
    }

    private boolean checkCardNumberByLuhnAlgo(String userCardNumber) {
        boolean checkedByLuhnAlgo = false;

        try {
            String cardBinWithAccNumber = userCardNumber.substring(0, userCardNumber.length() - 1); // get first 15 digits

            int checkSum = Character.getNumericValue(userCardNumber.charAt(userCardNumber.length() - 1)); // get the last digit

            int accountNumberDigitsSum = getAccountNumberDigitsSumByLuhnAlgo(cardBinWithAccNumber);

            return (accountNumberDigitsSum + checkSum) % 10 == 0;
        } catch (Exception e) {
            new Log("Failed to check by Luhn algo", e);
        }

        return checkedByLuhnAlgo;
    }

    public void closeCard(String cardNumber) {
        cardDAO.closeCardByCardNumber(cardNumber);
    }

    public boolean isAccountNumberUnique(String accountNumber) {
        return cardDAO.isAccountNumberUnique(accountNumber);
    }

    public String generateAccountNumber() {
        StringBuilder accountNumber = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            accountNumber.append(random.nextInt(10));
        }

        String accountNumberStr = accountNumber.toString();

        if (isAccountNumberUnique(accountNumberStr)) {
            return accountNumberStr;
        } else {
            return generateAccountNumber();
        }
    }
}
