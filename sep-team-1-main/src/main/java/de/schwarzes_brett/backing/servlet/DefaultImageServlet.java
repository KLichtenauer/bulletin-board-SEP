package de.schwarzes_brett.backing.servlet;

import de.schwarzes_brett.business_logic.services.ImageService;
import de.schwarzes_brett.dto.ImageDTO;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serial;
import java.util.logging.Logger;

/**
 * Provides access to default images.
 *
 * @author Tim-Florian Feulner
 */
@WebServlet("/image-default")
public class DefaultImageServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * The instance of the image service.
     */
    @Inject
    private ImageService imageService;

    /**
     * Default constructor.
     */
    public DefaultImageServlet() {}

    /**
     * Handles the GET request.
     *
     * @param request  The request for the image upload.
     * @param response The response for the image upload.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        ImageDTO.DefaultImageType imageType = extractImageType(request, response);

        if (imageType != null) {
            logger.fine("Requested default image with type " + imageType + ".");
            response.setHeader("Cache-Control", "max-age=604800");
            ImageDTO image = createImageDTO(imageType, response);

            imageService.fetchDefaultImage(image);
        }

        logger.finest("Default image request handled.");
    }

    private ImageDTO.DefaultImageType extractImageType(HttpServletRequest request, HttpServletResponse response) {
        String imageTypeString = request.getParameter("id");

        for (ImageDTO.DefaultImageType type : ImageDTO.DefaultImageType.values()) {
            if (type.toString().toLowerCase().equals(imageTypeString)) {
                return type;
            }
        }

        ServletUtil.sendNotFoundResponse("Invalid default image " + imageTypeString + " requested.", response);
        return null;
    }

    private ImageDTO createImageDTO(ImageDTO.DefaultImageType imageType, HttpServletResponse response) {
        OutputStream resultStream;
        try {
            resultStream = response.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ImageDTO image = new ImageDTO();
        image.setRetrieveStream(resultStream);
        image.setDefaultImageType(imageType);
        return image;
    }

}
