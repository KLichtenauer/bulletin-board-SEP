package de.schwarzes_brett.backing.validator_converter;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.dto.Limits;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;

/**
 * Validates the data for an image upload.
 *
 * @author Tim-Florian Feulner
 */
@Named
@Dependent
@FacesValidator(value = "imageValidator", managed = true)
public class ImageValidator implements Validator<Part> {

    @Inject
    private Dictionary dictionary;

    /**
     * Default constructor.
     */
    public ImageValidator() {}

    /**
     * Validates that a given image data is valid.
     *
     * @param context   Contains all the per-request state information related to the processing of a single request.
     * @param component The corresponding ui-component the value comes from.
     * @param value     The {@code Part} instance that contains the image data.
     * @throws ValidatorException Gets thrown if the image data is invalid.
     */
    @Override
    public void validate(FacesContext context, UIComponent component, Part value) throws ValidatorException {
        if (value.getSize() > Limits.IMAGE_MAX_SIZE) {
            throw new ValidatorException(new FacesMessage(dictionary.get("f_image_upload_too_large") + " " + Limits.IMAGE_MAX_SIZE + " Byte."));
        }
    }

}
