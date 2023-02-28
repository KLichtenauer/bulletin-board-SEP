package de.schwarzes_brett.dto;


import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Contains the data of a price.
 *
 * @author Valentin Damjantschitsch.
 */
public class PriceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The cents of the price.
     */
    private BigDecimal value;

    /**
     * The currency of the price.
     */
    private Currency currency;

    /**
     * The hasPrice of the price.
     */
    private boolean hasPrice;

    /**
     * The basis of negotiation of the price.
     */
    private boolean basisOfNegotiation;


    /**
     * Default constructor.
     */
    public PriceDTO() {
    }

    /**
     * Getter for the cents of the price.
     *
     * @return The cents of the price.
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Setter for the cents of the price.
     *
     * @param value The cents to be set.
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Getter for the currency of the price.
     *
     * @return The currency of the price.
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Setter for the currency of the price.
     *
     * @param currency The currency to be set.
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * Get if a price available.
     *
     * @return True if a price is available.
     */
    public boolean isHasPrice() {
        return hasPrice;
    }

    /**
     * Set if a price is available.
     *
     * @param hasPrice True if a price is available.
     */
    public void setHasPrice(boolean hasPrice) {
        this.hasPrice = hasPrice;
    }

    /**
     * Returns if the price is on basis of negotiation.
     *
     * @return True if a price is basis of negotiation.
     */
    public boolean isBasisOfNegotiation() {
        return basisOfNegotiation;
    }

    /**
     * Set if a price is basis of negotiation.
     *
     * @param basisOfNegotiation True if the price is basis of negotiation.
     */
    public void setBasisOfNegotiation(boolean basisOfNegotiation) {
        this.basisOfNegotiation = basisOfNegotiation;
    }
}
