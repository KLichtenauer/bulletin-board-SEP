package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.dto.ErrorDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.ProjectStage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing Bean for the error page.
 *
 * @author Daniel Lipp
 */
@Named
@RequestScoped
public class ErrorBean {

    private ErrorDTO error;

    @Inject
    private transient Dictionary dictionary;

    private boolean renderError;

    /**
     * Default constructor.
     */
    public ErrorBean() {
        error = new ErrorDTO();
        error.setMessage("");
        renderError = FacesContext.getCurrentInstance().isProjectStage(ProjectStage.Development);
    }

    /**
     * Getter for an error.
     *
     * @return An instance of {@code ErrorDTO} containing information about the occured error.
     */
    public ErrorDTO getError() {
        return error;
    }

    /**
     * Sets the error of this page.
     *
     * @param error error for this page.
     */
    public void setError(ErrorDTO error) {
        this.error = error;
    }

    /**
     * Getter if errors are be displayed.
     *
     * @return {@code true} if error are displayed.
     */
    public boolean isRenderError() {
        return renderError;
    }

    /**
     * Sets if errors should be displayed.
     *
     * @param renderError {@code true} if error should be displayed
     */
    public void setRenderError(boolean renderError) {
        this.renderError = renderError;
    }

    /**
     * Translates the message to the correct language if possible.
     */
    public void translateMessage() {
        if (error.getMessage() != null && !error.getMessage().equals("")) {
            error.setMessage(dictionary.get(error.getMessage()));
        }
    }
}
