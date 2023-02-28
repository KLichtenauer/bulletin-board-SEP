package de.schwarzes_brett.backing.validator_converter;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.business_logic.services.AdService;
import de.schwarzes_brett.data_access.exception.AdDoesNotExistException;
import de.schwarzes_brett.dto.AdDTO;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validates if an ad ID is valid. This is done by checking if the ad id saved in the url matches an id of a stored ad.
 *
 * @author Jonas Elsper
 */
@Named
@Dependent
@FacesValidator(value = "adIDValidator", managed = true)
public class AdIDValidator implements Validator<Integer> {

    @Inject
    private Dictionary dict;

    @Inject
    private AdService service;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public AdIDValidator() {
    }

    /**
     * Validates if an ad ID is valid.
     *
     * @param facesContext Contains all the per-request state information related to the processing of a single request.
     * @param uiComponent  The corresponding ui-component the value comes from.
     * @param integer      The ID of the ad to be validated.
     * @throws ValidatorException Gets thrown if the given adID is invalid.
     */
    @Override
    public void validate(final FacesContext facesContext, final UIComponent uiComponent, final Integer integer)
            throws ValidatorException {
        AdDTO ad = new AdDTO();
        ad.setId(integer);
        boolean isValid = service.isIdValid(ad);
        if (!isValid) {
            final String msg = dict.get("f_ad_doesNotExist");
            logger.log(Level.WARNING, "View Param ad id could not be validated.");
            throw new AdDoesNotExistException(msg);
        }
    }

}
