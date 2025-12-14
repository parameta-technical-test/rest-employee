package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.rest.dto.RequestLoginDTO;

/**
 * Service interface that defines authentication-related operations.
 * <p>
 * Handles user login, JWT generation, and retrieval of authenticated
 * user information based on the provided authorization token.
 * </p>
 */
public interface IAutenticationService {

    /**
     * Authenticates a user using the provided credentials.
     * <p>
     * On successful authentication, a JWT token is generated and returned
     * inside the response object.
     * </p>
     *
     * @param request the login request containing user credentials
     * @return a {@link ResponseGeneralDTO} containing authentication result and token data
     */
    ResponseGeneralDTO userLogin(RequestLoginDTO request);

    /**
     * Retrieves authenticated user information based on the JWT authorization header.
     * <p>
     * The token must follow the {@code Bearer <token>} format.
     * </p>
     *
     * @param tokenHeader the HTTP Authorization header containing the JWT token
     * @return a {@link ResponseGeneralDTO} containing user information
     */
    ResponseGeneralDTO userInformation(String tokenHeader);

}
