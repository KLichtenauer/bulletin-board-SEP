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
 * Checks that a username does not exist, i.e. that the input username is unique.
 *
 * @author Tim-Florian Feulner
 */
@Named
@Dependent
@FacesValidator(value = "usernameNotExistsValidator", managed = true)
public class UsernameNotExistsValidator implements Validator<String> {

    @Inject
    private Dictionary dictionary;

    @Inject
    private UserService userService;

    /**
     * Default constructor.
     */
    public UsernameNotExistsValidator() {}

    /**
     * Checks that a username does not exist.
     *
     * @param context   Contains all the per-request state information related to the processing of a single request.
     * @param component The corresponding ui-component the value comes from.
     * @param value     The value to be validated.
     * @throws ValidatorException Gets thrown if given username does not exist.
     */
    @Override
    public void validate(FacesContext context, UIComponent component, String value) throws ValidatorException {

        boolean performCheck = true;

        if (component.getAttributes().get("oldUserName") != null) {
            // Inside profile.
            String oldUserName = component.getAttributes().get("oldUserName").toString();

            if (value.equals(oldUserName)) {
                // Username was not changed.
                performCheck = false;
            }
        }

        if (performCheck && userService.doesUsernameExist(value)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, dictionary.get("v_username_not_exists_error"), null));
        }
    }

}
