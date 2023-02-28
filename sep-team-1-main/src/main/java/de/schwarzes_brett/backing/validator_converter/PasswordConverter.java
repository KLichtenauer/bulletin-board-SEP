package de.schwarzes_brett.backing.validator_converter;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.business_logic.services.PasswordHashService;
import de.schwarzes_brett.business_logic.services.UserService;
import de.schwarzes_brett.dto.PasswordDTO;
import de.schwarzes_brett.dto.UserDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Converts a password into a {@link PasswordDTO} that stores a password hash and a password salt.
 *
 * @author Tim-Florian Feulner
 */
@Named
@RequestScoped
@FacesConverter(value = "passwordConverter", managed = true)
public class PasswordConverter implements Converter<PasswordDTO> {

    @Inject
    private UserService userService;

    @Inject
    private PasswordHashService passwordHashService;

    @Inject
    private Dictionary dictionary;

    /**
     * Default constructor.
     */
    public PasswordConverter() {}

    /**
     * Converts a given password into a {@code PasswordDTO}.
     *
     * @param context   Contains all the per-request state information related to the processing of a single request.
     * @param component The corresponding ui-component the value comes from.
     * @param value     The value to be converted.
     * @return An instance of {@code PasswordDTO} containing the hashed password and its salt.
     */
    @Override
    public PasswordDTO getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || "".equals(value)) {
            throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, dictionary.get("f_login_no_password"), null));
        }

        UIInput usernameField = null;
        PasswordDTO result = new PasswordDTO();
        if (component.getAttributes().get("usernameValue") != null) {
            String usernameInputId = component.getAttributes().get("usernameValue").toString();
            usernameField = (UIInput) context.getViewRoot().findComponent(usernameInputId);
        }

        if (usernameField == null) {
            // Password input is not on login page.

            // Abort if password not valid.
            if (!userService.isPasswordValid(value)) {
                throw new ConverterException(
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, dictionary.get("f_password_converter_password_invalid"), null));
            }

            result.setPwdSalt(passwordHashService.generateSalt());
        } else {
            // Password input is on login page.

            // Fetch password salt to use for hashing.
            UserDTO user = new UserDTO();
            user.getCredentials().setUsername(usernameField.getValue().toString());
            boolean userExists = userService.fetchUserByUsername(user);
            if (userExists) {
                result.setPwdSalt(user.getCredentials().getPassword().getPwdSalt());
            } else {
                // User does not exist. Return invalid PasswordDTO.
                return new PasswordDTO();
            }
        }

        result.setPasswordHash(passwordHashService.hashPassword(value, result.getPwdSalt()));

        return result;
    }

    /**
     * Returns {@code null} as this is not a supported operation.
     *
     * @param context   Contains all the per-request state information related to the processing of a single request.
     * @param component The corresponding ui-component the value comes from.
     * @param value     The DTO containing the password to be converted.
     * @return The converted password as {@code String}.
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, PasswordDTO value) {
        return null;
    }

}
