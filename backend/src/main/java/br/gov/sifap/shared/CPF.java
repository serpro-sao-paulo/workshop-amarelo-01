package br.gov.sifap.shared;

import java.util.Objects;

/**
 * CPF value object.
 *
 * <p>Validates the check digits using the modulo-11 algorithm confirmed in
 * REQ-002 ({@code CADBENEF.NSN#L224-L269}) and rejects invalid numbers
 * (REQ-001/003). Exposes a single masked representation for safe display and
 * logging (LGPD).
 */
public final class CPF {

    private final String digits;

    private CPF(String digits) {
        this.digits = digits;
    }

    /**
     * Builds a CPF from raw input, stripping formatting.
     *
     * @throws InvalidCpfException when the value is not a valid CPF (REQ-003)
     */
    public static CPF of(String raw) {
        Objects.requireNonNull(raw, "cpf");
        String digits = raw.replaceAll("\\D", "");
        if (!isValid(digits)) {
            throw new InvalidCpfException();
        }
        return new CPF(digits);
    }

    public static boolean isValid(String digits) {
        if (digits == null || digits.length() != 11 || !digits.chars().allMatch(Character::isDigit)) {
            return false;
        }
        // Reject sequences with all identical digits (e.g. 11111111111).
        if (digits.chars().distinct().count() == 1) {
            return false;
        }
        int firstCheck = checkDigit(digits, 9, 10);
        int secondCheck = checkDigit(digits, 10, 11);
        return firstCheck == (digits.charAt(9) - '0') && secondCheck == (digits.charAt(10) - '0');
    }

    private static int checkDigit(String digits, int length, int startWeight) {
        int sum = 0;
        int weight = startWeight;
        for (int i = 0; i < length; i++) {
            sum += (digits.charAt(i) - '0') * weight--;
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    /** Unmasked digits — use only inside the domain, never in logs or responses. */
    public String unmasked() {
        return digits;
    }

    /** Masked representation safe for display/logging: {@code ***.***.789-**}. */
    public String masked() {
        return "***.***." + digits.substring(6, 9) + "-**";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof CPF other && digits.equals(other.digits);
    }

    @Override
    public int hashCode() {
        return digits.hashCode();
    }

    @Override
    public String toString() {
        return masked();
    }
}
