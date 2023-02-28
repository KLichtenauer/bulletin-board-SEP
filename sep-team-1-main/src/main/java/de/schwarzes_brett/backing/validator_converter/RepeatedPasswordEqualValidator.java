package de.schwarzes_brett.backing.validator_converter;

import de.schwarzes_brett.backing.session.UserSession;
import de.schwarzes_brett.business_logic.dictionary.CompleteDictionary;
import de.schwarzes_brett.business_logic.services.UserService;
import de.schwarzes_brett.dto.PasswordDTO;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;

import java.util.logging.Logger;

/**
 * Validates if a repeated password is equal to the first password.
 *
 * @author michaelgruner
 */
@FacesValidator(value = "repeatedPasswordEqualValidator", managed = true)
public class RepeatedPasswordEqualValidator implements Validator<String> {

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    @Inject
    private UserService service;

    @Inject
    private UserSession userSession;

    /**
     * Default constructor.
     */
    public RepeatedPasswordEqualValidator() {
    }

    /**
     * Validates if the repeated password is the same as the first password.
     *
     * @param context   Contains the per-request state information related to the processing of a single request.
     * @param component The corresponding ui-component the value comes from.
     * @param repeated  The repeated password to be validated.
     * @throws ValidatorException Gets thrown if the given adID is invalid.
     */
    @Override
    public void validate(FacesContext context, UIComponent component, String repeated) throws ValidatorException {
        logger.finest("Starting validation of the repeated password.");
        String firstPasswordFieldName = (String) component.getAttributes().get("passwordId");
        UIInput passwordField = (UIInput) context.getViewRoot().findComponent(firstPasswordFieldName);
        if (passwordField == null) {
            throw new IllegalArgumentException("Unable to find component.");
        }
        PasswordDTO password = (PasswordDTO) passwordField.getValue();
        if (!service.arePasswordsEqual(password, repeated)) {
            logger.finest("Password are not equal");
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                    "Passwords do not match!",
                                                    CompleteDictionary.get("m_passwordNotEqualMessage", userSession.getLocale()));
            throw new ValidatorException(message);
        }
        logger.finest("Passwords are equal");
    }
}
