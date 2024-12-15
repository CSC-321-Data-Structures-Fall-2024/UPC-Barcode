package HW1;

import java.util.Scanner;

/**
 * HW1Driver
 * 
 * This program validates Universal Product Codes (UPCs) and barcodes.
 * It continually asks the user for a UPC or barcode and then tells the user
 * whether or not it is a valid UPC. The program ends when the user inputs "quit".
 * 
 * Author: Jerome Bustarga
 * ID: JHB09808
 * 
 * Author: Kasey Broick
 * ID: KTB50496
 * 
 * Usage:
 * Enter the UPC or Barcode to check or type quit to exit.
 * 036000291452
 * The UPC 036000291452 is valid.
 * 
 * Enter the UPC or Barcode to check or type quit to exit.
 * 10100011010111101010111100011010001101000110101010110110011101001100110101110010011101101100101
 * That is a valid barcode representing the UPC 036000291452.
 * The UPC 036000291452 is valid.
 * 
 * Enter the UPC or Barcode to check or type quit to exit.
 * quit
 */

public class HW1Driver {

    public static void main(String[] args) {
        UPCValidator validator = new UPCValidator();
        validator.run();
    }
}

class UPCValidator {
    private Scanner scanner;

    public UPCValidator() {
        scanner = new Scanner(System.in);
    }

    /**
     * Runs the UPC validation program.
     */
    public void run() {
        String input;

        while (true) {
            System.out.println("Enter the UPC or Barcode to check or type quit to exit.");
            input = scanner.nextLine();

            if (input.equalsIgnoreCase("quit")) {
                break;
            }

            try {
                if (input.matches("\\d{12}")) {
                    // Input is a UPC
                    if (isValidUPC(input)) {
                        System.out.println("The UPC " + input + " is valid.");
                    } else {
                        System.out.println("The UPC " + input + " is not valid.");
                    }
                } else if (input.matches("[01]{95}")) {
                    // Input is a Barcode
                    String upc = decodeBarcode(input);
                    if (upc != null && isValidUPC(upc)) {
                        System.out.println("That is a valid barcode representing the UPC " + upc + ".");
                        System.out.println("The UPC " + upc + " is valid.");
                    } else if (upc != null) {
                        System.out.println("That is a valid barcode representing the UPC " + upc + ".");
                        System.out.println("The UPC " + upc + " is not valid.");
                    } else {
                        System.out.println("That is an invalid barcode/UPC.");
                    }
                } else {
                    System.out.println("Invalid input format.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number format.");
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }

        scanner.close();
    }

    /**
     * Validates a UPC using the check digit algorithm.
     * 
     * @param upc The UPC to validate.
     * @return true if the UPC is valid, false otherwise.
     */
    private boolean isValidUPC(String upc) {
        int sumOdd = 0, sumEven = 0;

        for (int i = 0; i < upc.length() - 1; i++) {
            int digit = Character.getNumericValue(upc.charAt(i));
            if (i % 2 == 0) {
                sumOdd += digit;
            } else {
                sumEven += digit;
            }
        }

        int total = (sumOdd * 3) + sumEven;
        int checkDigit = (10 - (total % 10)) % 10;

        return checkDigit == Character.getNumericValue(upc.charAt(11));
    }

    /**
     * Decodes a barcode into a UPC.
     * 
     * @param barcode The barcode to decode.
     * @return The decoded UPC, or null if the barcode is invalid.
     */
    private String decodeBarcode(String barcode) {
        if (!barcode.startsWith("101") || !barcode.endsWith("101") || !barcode.substring(45, 50).equals("01010")) {
            return null;
        }

        StringBuilder upc = new StringBuilder();
        boolean backwards = true;

        // Decode left side
        for (int i = 3; i < 45; i += 7) {
            String digit = barcode.substring(i, i + 7);
            String decodedDigit = decodeLeftDigit(digit);
            if (decodedDigit == null) {
                decodedDigit = decodeBackRightDigit(digit);
                if (decodedDigit == null) {
                    return null;
                } else {
                    backwards = false;
                }
            }
            upc.append(decodedDigit);
        }

        // Decode right side
        for (int i = 50; i < 92; i += 7) {
            String digit = barcode.substring(i, i + 7);
            String decodedDigit = decodeRightDigit(digit);
            if (decodedDigit == null) {
                decodedDigit = decodeBackLeftDigit(digit);
                if (decodedDigit == null) {
                    return null;
                } else {
                    backwards = false;
                }
            }
            upc.append(decodedDigit);
        }

        if (!backwards) {
            upc.reverse();
        }

        return upc.toString();
    }

    /**
     * Decodes a left-side digit of a barcode.
     * 
     * @param digit The digit to decode.
     * @return The decoded digit, or null if the digit is invalid.
     */
    private String decodeLeftDigit(String digit) {
        switch (digit) {
            case "0001101": return "0";
            case "0011001": return "1";
            case "0010011": return "2";
            case "0111101": return "3";
            case "0100011": return "4";
            case "0110001": return "5";
            case "0101111": return "6";
            case "0111011": return "7";
            case "0110111": return "8";
            case "0001011": return "9";
            default: return null;
        }
    }

    /**
     * Decodes a right-side digit of a barcode.
     * 
     * @param digit The digit to decode.
     * @return The decoded digit, or null if the digit is invalid.
     */
    private String decodeRightDigit(String digit) {
        switch (digit) {
            case "1110010": return "0";
            case "1100110": return "1";
            case "1101100": return "2";
            case "1000010": return "3";
            case "1011100": return "4";
            case "1001110": return "5";
            case "1010000": return "6";
            case "1000100": return "7";
            case "1001000": return "8";
            case "1110100": return "9";
            default: return null;
        }
    }

    /**
     * Decodes a left-side digit of a barcode that is backwards.
     * 
     * @param digit The digit to decode.
     * @return The decoded digit, or null if the digit is invalid.
     */
    private String decodeBackLeftDigit(String digit) {
        switch (digit) {
            case "1011000": return "0";
            case "1001100": return "1";
            case "1100100": return "2";
            case "1011110": return "3";
            case "1100010": return "4";
            case "1000110": return "5";
            case "1111010": return "6";
            case "1101110": return "7";
            case "1110110": return "8";
            case "1101000": return "9";
            default: return null;
        }
    }

    /**
     * Decodes a right-side digit of a barcode that is backwards.
     * 
     * @param digit The digit to decode.
     * @return The decoded digit, or null if the digit is invalid.
     */
    private String decodeBackRightDigit(String digit) {
        switch (digit) {
            case "0100111": return "0";
            case "0110011": return "1";
            case "0011011": return "2";
            case "0100001": return "3";
            case "0011101": return "4";
            case "0111001": return "5";
            case "0000101": return "6";
            case "0010001": return "7";
            case "0001001": return "8";
            case "0010111": return "9";
            default: return null;
        }
    }
}
