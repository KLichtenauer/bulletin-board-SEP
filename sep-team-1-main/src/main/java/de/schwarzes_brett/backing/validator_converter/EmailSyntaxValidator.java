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

/**
 * Validates if an e-Mail is valid by checking if the pattern matches the given email.
 *
 * @author Daniel Lipp
 */
@Named
@Dependent
@FacesValidator(value = "emailSyntaxValidator", managed = true)
public class EmailSyntaxValidator implements Validator<String> {

    @Inject
    private Dictionary dictionary;

    @Inject
    private UserService service;

    /**
     * Default constructor.
     */
    public EmailSyntaxValidator() {
    }

    /**
     * Validates if a e-Mail is valid.
     *
     * @param context   Contains all of the per-request state information related to the processing of a single request.
     * @param component The corresponding ui-component the value comes from.
     * @param value     The e-mail to be validated.
     * @throws ValidatorException Gets thrown if the given e-mail is invalid.
     */
    @Override
    public void validate(FacesContext context, UIComponent component,
                         String value) throws ValidatorException {
        if (!service.isEmailValid(value)) {
            final FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, dictionary.get("v_emailValidator_message"), null);
            throw new ValidatorException(msg);
        }
    }

}
