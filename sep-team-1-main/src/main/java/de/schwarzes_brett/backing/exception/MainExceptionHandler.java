package de.schwarzes_brett.backing.exception;

import de.schwarzes_brett.backing.backing_beans.ErrorBean;
import de.schwarzes_brett.business_logic.exception.HashingUnavailableException;
import de.schwarzes_brett.data_access.exception.DataStorageAccessException;
import de.schwarzes_brett.data_access.exception.DataStorageUnavailableException;
import de.schwarzes_brett.data_access.exception.DoesNotExistException;
import de.schwarzes_brett.dto.ErrorDTO;
import jakarta.el.ELException;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.FacesException;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerFactory;
import jakarta.faces.context.ExceptionHandlerWrapper;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ExceptionQueuedEvent;

import java.util.Iterator;

/**
 * Handles all exceptions occurring in the backing layer, by forwarding to error page where the user gets context of the error. Reasons for needed
 * handling can be loss of database connection or severe race conditions.
 *
 * @author Daniel Lipp
 */
public class MainExceptionHandler extends ExceptionHandlerWrapper {

    private static final String BASE_PATH = "/WEB-INF/errorpages/";

    /**
     * Creates a handler for unchecked exceptions which got iterated to the backing layer.
     *
     * @param wrapped Instance of {@code ExceptionHandler} to be wrapped.
     */
    public MainExceptionHandler(ExceptionHandler wrapped) {
        super(wrapped);
    }

    /**
     * Handles given exceptions by forwarding to error page where the user gets context of the error.
     */
    @Override
    public void handle() {
        handleExceptions(FacesContext.getCurrentInstance());
        getWrapped().handle();
    }

    /**
     * Handels exceptions that were thrown and not handled directly.
     * Exceptions that do not need to be handled are:
     * <ul>
     *     <li>MailUnavailableException: is checked and won't reach this handler.</li>
     *     <li>DuplicateContentException: is checked and won't reach this handler.</li>
     *     <li>ConfigUnavailableException: when this exception occurs,
     *     the application can not start.</li>
     * </ul>
     *
     * @param context context of the Request.
     */
    protected void handleExceptions(FacesContext context) {
        Iterator<ExceptionQueuedEvent> unhandledExceptionQueuedEvents =
                getUnhandledExceptionQueuedEvents().iterator();
        if (context == null || !unhandledExceptionQueuedEvents.hasNext()) {
            return;
        }
        Throwable exception = unhandledExceptionQueuedEvents.next().getContext().getException();
        while (exception.getCause() != null
               && (exception instanceof FacesException
                   || exception instanceof ELException)) {
            exception = exception.getCause();
        }
        if (exception instanceof DataStorageAccessException) {
            String message = "f_error_ds_access";
            forwardToErrorPage(context, exception, message);
        } else if (exception instanceof DataStorageUnavailableException) {
            String message = "f_error_ds_unavailable";
            forwardToErrorPage(context, exception, message);
        } else if (exception instanceof DoesNotExistException
                   || exception instanceof UnauthorisedAccessException) {
            forwardTo404Page(context, exception);
        } else if (exception instanceof HashingUnavailableException) {
            String message = "f_error_no_hashing";
            forwardToErrorPage(context, exception, message);
        } else if (exception instanceof FacesException) {
            return;
        } else if (exception instanceof RuntimeException) {
            forwardToErrorPage(context, exception, "");
        } else {
            return;
        }

        unhandledExceptionQueuedEvents.remove();
        while (unhandledExceptionQueuedEvents.hasNext()) {
            unhandledExceptionQueuedEvents.next();
            unhandledExceptionQueuedEvents.remove();
        }
    }

    private void fillErrorBean(Throwable exception, String message) {
        ErrorBean bean = CDI.current().select(ErrorBean.class).get();
        ErrorDTO error = new ErrorDTO();
        error.setException((Exception) exception);
        error.setMessage(message);
        bean.setError(error);
        bean.translateMessage();
    }

    private void forwardToPage(FacesContext context, String page) {
        context.setViewRoot(context.getApplication().getViewHandler().createView(context, page));
        context.getPartialViewContext().setRenderAll(true);
        context.getApplication().getViewHandler();
        context.renderResponse();
    }

    private void forwardTo404Page(FacesContext context, Throwable exception) {
        fillErrorBean(exception, "");
        forwardToPage(context, BASE_PATH + "error404.xhtml");
    }

    private void forwardToErrorPage(FacesContext context, Throwable exception, String message) {
        fillErrorBean(exception, message);
        forwardToPage(context, BASE_PATH + "error.xhtml");
    }

    /**
     * Factory for creating a {@code MainExceptionHandler} instance.
     */
    public static class Factory extends ExceptionHandlerFactory {

        /**
         * Creates a factory.
         *
         * @param wrapped Instance of {@code ExceptionHandlerFactory} to be wrapped.
         */
        public Factory(ExceptionHandlerFactory wrapped) {
            super(wrapped);
        }

        /**
         * Creates a needed ExceptionHandler.
         *
         * @return Instance of {@code MainExceptionHandler}.
         */
        @Override
        public MainExceptionHandler getExceptionHandler() {
            return new MainExceptionHandler(getWrapped().getExceptionHandler());
        }
    }
}
