package de.schwarzes_brett.backing.validator_converter;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.backing.session.UserSession;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Converts a price in a specified representation.
 *
 * @author Daniel Lipp
 */
@Named
@Dependent
@FacesConverter(value = "priceValueConverter", managed = true)
public class PriceValueConverter implements Converter<BigDecimal> {

    @Inject
    private Dictionary dictionary;

    /**
     * The session that contains the locale.
     */
    @Inject
    private UserSession session;

    /**
     * Default constructor.
     */
    public PriceValueConverter() {
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
    public BigDecimal getAsObject(FacesContext context, UIComponent component,
                                  String value) {
        if (value == null || value.equals("")) {
            return null;
        }
        try {
            Number ret = NumberFormat.getNumberInstance(session.getLocale()).parse(value);
            return BigDecimal.valueOf(ret.doubleValue());
        } catch (ParseException e) {
            final FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, dictionary.get("c_priceConvertor_message"), null);
            throw new ConverterException(msg);
        }

    }

    /**
     * @param context   Contains all of the per-request state information related to the processing of a single request.
     * @param component The corresponding ui-component the value comes from.
     * @param value     The value to be converted.
     * @return An instance of {@code PriceDTO} contianing the converted price.
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component,
                              BigDecimal value) {
        if (value == null) {
            return "";
        } else {
            return NumberFormat.getNumberInstance(session.getLocale()).format(value.doubleValue());
        }
    }

}
