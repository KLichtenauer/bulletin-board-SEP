package de.schwarzes_brett.backing.validator_converter;

import de.schwarzes_brett.business_logic.dictionary.CompleteDictionary;
import de.schwarzes_brett.dto.PriceDTO;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Named;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Converts a price in a specified representation.
 *
 * @author michaelgruner
 */
@Named
@Dependent
@FacesConverter(value = "priceConverter", managed = true)
public class PriceConverter implements Converter<PriceDTO> {

    /**
     * Default constructor.
     */
    public PriceConverter() {
    }

    /**
     * Converts a given price into a {@code PriceDTO}.
     *
     * @param context   Contains all of the per-request state information related to the processing of a single request.
     * @param component The corresponding ui-component the value comes from.
     * @param value     The value to be converted.
     * @return An instance of {@code PriceDTO} containing the converted price.
     */
    @Override
    public PriceDTO getAsObject(FacesContext context, UIComponent component,
                                String value) {
        return null;
    }

    /**
     * @param context   Contains the per-request state information related to the processing of a single request.
     * @param component The corresponding ui-component the value comes from.
     * @param price     The price to be converted.
     * @return An instance of {@code PriceDTO} contianing the converted price.
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              PriceDTO price) {
        StringBuilder stringBuilder = new StringBuilder();
        Locale locale = context.getExternalContext().getRequestLocale();
        if (price.isHasPrice()) {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
            DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
            decimalFormat.applyPattern("#.00");
            stringBuilder.append(decimalFormat.format(price.getValue()));
            stringBuilder.append(" ");
            stringBuilder.append(price.getCurrency());
        }
        if (price.isHasPrice() && price.isBasisOfNegotiation()) {
            stringBuilder.append(" ");
        }
        if (price.isBasisOfNegotiation()) {
            stringBuilder.append(CompleteDictionary.get("f_ad_basisOfNegotiation", locale));
        }
        return stringBuilder.toString();
    }

}
