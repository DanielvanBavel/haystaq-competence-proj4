package nl.haystaq.huisjacht.shared.domain;

/**
 * Domeinfout met een code en een leesbare melding. Anders dan in opdracht 1 komt
 * de melding hier wel bij de client terecht: in dit project draait het om de UI,
 * niet om raadselachtige foutcodes.
 */
public class BusinessRuleViolation extends RuntimeException {

    public enum Kind {
        INVALID_INPUT,
        CONFLICT,
        NOT_FOUND
    }

    private final Kind kind;
    private final String code;

    private BusinessRuleViolation(Kind kind, String code, String message) {
        super(message);
        this.kind = kind;
        this.code = code;
    }

    public static BusinessRuleViolation invalid(String code, String message) {
        return new BusinessRuleViolation(Kind.INVALID_INPUT, code, message);
    }

    public static BusinessRuleViolation conflict(String code, String message) {
        return new BusinessRuleViolation(Kind.CONFLICT, code, message);
    }

    public static BusinessRuleViolation notFound(String code, String message) {
        return new BusinessRuleViolation(Kind.NOT_FOUND, code, message);
    }

    public static void require(boolean condition, String code, String message) {
        if (!condition) {
            throw invalid(code, message);
        }
    }

    public static void requireState(boolean condition, String code, String message) {
        if (!condition) {
            throw conflict(code, message);
        }
    }

    public Kind kind() {
        return kind;
    }

    public String code() {
        return code;
    }
}
