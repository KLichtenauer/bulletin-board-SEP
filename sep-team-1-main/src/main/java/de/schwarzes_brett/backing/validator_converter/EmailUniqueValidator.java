package de.schwarzes_brett.backing.validator_converter;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.business_logic.services.UserService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;

import java.util.logging.Logger;

/**
 * Validator for the uniqueness of an email.
 *
 * @author michaelgruner
 */
@FacesValidator(value = "emailUniqueValidator", managed = true)
public class EmailUniqueValidator implements Validator<String> {

    @Inject
    private transient Logger logger;

    @Inject
    private UserService service;

    @Inject
    private Dictionary dictionary;

    /**
     * Default Constructor.
     */
    public EmailUniqueValidator() {}


    /**
     * Validates if the given email is unique.
     *
     * @param facesContext Contains the per-request state information related to the processing of a single request.
     * @param uiComponent  The corresponding ui-component the value comes from.
     * @param email        The email that should be unique.
     * @throws ValidatorException If the email is not unique.
     */
    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, String email) throws ValidatorException {

        boolean performCheck = true;

        if (uiComponent.getAttributes().get("oldEmail") != null) {
            // Inside profile.
            String oldEmail = uiComponent.getAttributes().get("oldEmail").toString();

            if (email.equals(oldEmail)) {
                // Email was not changed.
                performCheck = false;
            }
        }

        logger.fine("Validating if the following email is unique: " + email);
        if (performCheck && !service.isEmailUnique(email)) {
            logger.fine("Password are not equal");
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "E-Mail is not unique.", dictionary.get("m_emailNotUniqueMessage"));
            throw new ValidatorException(message);
        }
    }
}
