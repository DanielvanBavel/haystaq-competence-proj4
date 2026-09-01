package nl.haystaq.huisjacht.shared.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Bedrag in euro's. Vraagprijzen zijn altijd positief. */
public record Money(BigDecimal amount) implements Comparable<Money> {

    public Money {
        BusinessRuleViolation.require(amount != null, "money.missing", "Bedrag ontbreekt.");
        BusinessRuleViolation.require(amount.signum() >= 0, "money.negative", "Bedrag kan niet negatief zijn.");
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(long euros) {
        return new Money(BigDecimal.valueOf(euros));
    }

    public boolean isAtLeast(Money other) {
        return amount.compareTo(other.amount) >= 0;
    }

    @Override
    public int compareTo(Money other) {
        return amount.compareTo(other.amount);
    }

    @Converter(autoApply = true)
    public static class JpaConverter implements AttributeConverter<Money, BigDecimal> {
        @Override
        public BigDecimal convertToDatabaseColumn(Money attribute) {
            return attribute == null ? null : attribute.amount();
        }

        @Override
        public Money convertToEntityAttribute(BigDecimal dbData) {
            return dbData == null ? null : new Money(dbData);
        }
    }
}
