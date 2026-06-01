package br.com.fiap.orbitcast.bo;

import br.com.fiap.orbitcast.exceptions.BusinessException;

import java.math.BigDecimal;
import java.util.Set;
import java.util.regex.Pattern;

final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Set<Integer> DDD_VALIDOS = Set.of(
            11, 12, 13, 14, 15, 16, 17, 18, 19,
            21, 22, 24, 27, 28,
            31, 32, 33, 34, 35, 37, 38,
            41, 42, 43, 44, 45, 46, 47, 48, 49,
            51, 53, 54, 55,
            61, 62, 63, 64, 65, 66, 67, 68, 69,
            71, 73, 74, 75, 77, 79,
            81, 82, 83, 84, 85, 86, 87, 88, 89,
            91, 92, 93, 94, 95, 96, 97, 98, 99
    );

    private ValidationUtils() {
    }

    static Long requirePositiveId(Long id, String mensagem) {
        if (id == null || id <= 0) {
            throw new BusinessException(mensagem);
        }
        return id;
    }

    static String requireText(String value, String campo, int maxLength) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            throw new BusinessException(campo + " e obrigatorio.");
        }
        if (normalized.length() > maxLength) {
            throw new BusinessException(campo + " deve ter no maximo " + maxLength + " caracteres.");
        }
        return normalized;
    }

    static String optionalText(String value, String campo, int maxLength) {
        String normalized = normalizeText(value);
        if (normalized != null && normalized.length() > maxLength) {
            throw new BusinessException(campo + " deve ter no maximo " + maxLength + " caracteres.");
        }
        return normalized;
    }

    static String requireEmail(String value) {
        String email = requireText(value, "Email", 120).toLowerCase();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException("Email deve ter formato valido.");
        }
        return email;
    }

    static String optionalPhone(String value) {
        String telefone = normalizeText(value);
        if (telefone == null) {
            return null;
        }

        String digits = onlyDigits(telefone);
        if (digits.length() != 10 && digits.length() != 11) {
            throw new BusinessException("Telefone deve conter DDD e 10 ou 11 digitos.");
        }
        if (allEqual(digits)) {
            throw new BusinessException("Telefone deve conter uma sequencia valida.");
        }

        int ddd = Integer.parseInt(digits.substring(0, 2));
        if (!DDD_VALIDOS.contains(ddd)) {
            throw new BusinessException("Telefone deve conter um DDD valido.");
        }

        return digits;
    }

    static String optionalCpfCnpj(String value) {
        String documento = normalizeText(value);
        if (documento == null) {
            return null;
        }

        String digits = onlyDigits(documento);
        if (digits.length() == 11 && isCpfValido(digits)) {
            return digits;
        }
        if (digits.length() == 14 && isCnpjValido(digits)) {
            return digits;
        }
        throw new BusinessException("Documento deve ser um CPF ou CNPJ valido.");
    }

    static BigDecimal requirePositive(BigDecimal value, String campo) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(campo + " deve ser maior que zero.");
        }
        return value;
    }

    static BigDecimal requireBetween(BigDecimal value, String campo, BigDecimal min, BigDecimal max) {
        if (value == null || value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            throw new BusinessException(campo + " deve ficar entre " + min + " e " + max + ".");
        }
        return value;
    }

    static Integer requireBetween(Integer value, String campo, int min, int max) {
        if (value == null || value < min || value > max) {
            throw new BusinessException(campo + " deve ficar entre " + min + " e " + max + ".");
        }
        return value;
    }

    static String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.strip().replaceAll("\\s+", " ");
        return normalized.isEmpty() ? null : normalized;
    }

    private static String onlyDigits(String value) {
        return value.replaceAll("\\D", "");
    }

    private static boolean isCpfValido(String cpf) {
        if (allEqual(cpf)) {
            return false;
        }

        int primeiro = calcularDigito(cpf.substring(0, 9), 10);
        int segundo = calcularDigito(cpf.substring(0, 9) + primeiro, 11);
        return cpf.equals(cpf.substring(0, 9) + primeiro + segundo);
    }

    private static boolean isCnpjValido(String cnpj) {
        if (allEqual(cnpj)) {
            return false;
        }

        int primeiro = calcularDigito(cnpj.substring(0, 12), new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        int segundo = calcularDigito(cnpj.substring(0, 12) + primeiro, new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        return cnpj.equals(cnpj.substring(0, 12) + primeiro + segundo);
    }

    private static int calcularDigito(String value, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < value.length(); i++) {
            soma += Character.getNumericValue(value.charAt(i)) * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static int calcularDigito(String value, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < value.length(); i++) {
            soma += Character.getNumericValue(value.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static boolean allEqual(String value) {
        char first = value.charAt(0);
        for (int i = 1; i < value.length(); i++) {
            if (value.charAt(i) != first) {
                return false;
            }
        }
        return true;
    }
}
