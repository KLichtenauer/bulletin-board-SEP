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
 * Provides access to images.
 *
 * @author Tim-Florian Feulner
 */
@WebServlet("/image")
public class ImageServlet extends HttpServlet {

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
    public ImageServlet() {}

    /**
     * Handles the GET request.
     *
     * @param request  The request for the image upload.
     * @param response The response for the image upload.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        Long imageId = extractImageId(request, response);

        if (imageId != null) {
            logger.fine("Requested image with id " + imageId + ".");
            response.setHeader("Cache-Control", "max-age=604800");
            ImageDTO image = createImageDTO(imageId, response);

            boolean success = imageService.fetchImage(image);
            if (!success) {
                ServletUtil.sendNotFoundResponse("Invalid image " + imageId + " requested.", response);
            }
        }

        logger.finest("Image request handled.");
    }

    private Long extractImageId(HttpServletRequest request, HttpServletResponse response) {
        Long imageId = null;
        String imageIdString = request.getParameter("id");

        try {
            imageId = Long.parseLong(imageIdString);
        } catch (NumberFormatException e) {
            ServletUtil.sendNotFoundResponse("Invalid image id " + imageIdString + " requested.", response);
        }

        return imageId;
    }

    private ImageDTO createImageDTO(long imageId, HttpServletResponse response) {
        OutputStream resultStream;
        try {
            resultStream = response.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ImageDTO image = new ImageDTO();
        image.setRetrieveStream(resultStream);
        image.setId(imageId);
        return image;
    }

}
