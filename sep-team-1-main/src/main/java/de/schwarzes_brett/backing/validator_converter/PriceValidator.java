package de.schwarzes_brett.backing.validator_converter;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.business_logic.services.UserService;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.math.BigDecimal;

/**
 * Checks if a price is valid.
 *
 * @author Daniel Lipp
 */
@Named
@Dependent
@FacesValidator(value = "priceValidator", managed = true)
public class PriceValidator implements Validator<BigDecimal> {

    @Inject
    private Dictionary dictionary;

    @Inject
    private UserService service;

    /**
     * Default constructor.
     */
    public PriceValidator() {
    }

    /**
     * Checks if a username exists.
     *
     * @param context   Contains all of the per-request state information related to the processing of a single request.
     * @param component The corresponding ui-component the value comes from.
     * @param value     The value to be validated.
     * @throws ValidatorException Gets thrown if given username does not exist.
     */
    @Override
    public void validate(FacesContext context, UIComponent component,
                         BigDecimal value) throws ValidatorException {
        if (!service.isPriceValid(value)) {
            final FacesMessage msg =
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            dictionary.get("v_priceValidator_message"),
                            null);
            throw new ValidatorException(msg);
        }
    }

}
