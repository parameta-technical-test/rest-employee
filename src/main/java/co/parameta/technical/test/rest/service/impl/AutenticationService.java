package co.parameta.technical.test.rest.service.impl;

import co.parameta.technical.test.commons.dto.AdministratorUserDTO;
import co.parameta.technical.test.commons.dto.AdministratorUserSecurityDTO;
import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.commons.entity.AdministratorUserEntity;
import co.parameta.technical.test.commons.repository.BlacklistTokenRepository;
import co.parameta.technical.test.commons.service.IJwtService;
import co.parameta.technical.test.commons.service.ITokenBlacklistService;
import co.parameta.technical.test.commons.util.exception.MensajePersonalizadoException;
import co.parameta.technical.test.commons.util.mapper.AdministratorUserMapper;
import co.parameta.technical.test.rest.dto.AuthResponseDTO;
import co.parameta.technical.test.rest.dto.RequestLoginDTO;
import co.parameta.technical.test.rest.repository.AdministratorUserRepository;
import co.parameta.technical.test.rest.service.IAutenticationService;
import co.parameta.technical.test.rest.util.constant.Constants;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation responsible for user authentication and authorization logic.
 * <p>
 * This service handles:
 * <ul>
 *     <li>User authentication using Spring Security</li>
 *     <li>JWT token generation and validation</li>
 *     <li>Token revocation validation using a blacklist</li>
 *     <li>Retrieval of authenticated user information</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AutenticationService implements IAutenticationService {

    /**
     * JWT service used to generate and validate tokens.
     */
    private final IJwtService iJwtService;

    /**
     * Spring Security authentication manager.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Repository used to validate revoked tokens.
     */
    private final BlacklistTokenRepository blacklistTokenRepository;

    /**
     * Repository used to retrieve administrator user information.
     */
    private final AdministratorUserRepository administratorUserRepository;

    /**
     * Mapper used to convert administrator entities to DTOs.
     */
    private final AdministratorUserMapper administratorUserMapper;

    /**
     * Secret key used to validate JWT signatures.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param request the login request containing email and password
     * @return a {@link ResponseGeneralDTO} containing authentication result and token data
     * @throws BadCredentialsException if authentication fails
     */
    @Override
    public ResponseGeneralDTO userLogin(RequestLoginDTO request) {
        ResponseGeneralDTO response = new ResponseGeneralDTO();

        authenticateUser(request);

        AdministratorUserDTO user = getUser(request.getEmail());
        String token = generateToken(user);

        response.setData(
                AuthResponseDTO.builder()
                        .token(token)
                        .expirationTime(iJwtService.getTimeRemainingMillis(token))
                        .user(user.getEmail())
                        .build()
        );

        response.setMessage(Constants.MSG_LOGIN_SUCCESS);
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    /**
     * Retrieves authenticated user information based on a JWT authorization header.
     * <p>
     * The token is validated against expiration, format, signature,
     * and blacklist status.
     * </p>
     *
     * @param tokenHeader the HTTP Authorization header containing the JWT
     * @return a {@link ResponseGeneralDTO} with user information
     * @throws MensajePersonalizadoException if the token is missing, invalid, revoked, or expired
     */
    @Override
    public ResponseGeneralDTO userInformation(String tokenHeader) {

        if (!StringUtils.hasText(tokenHeader)) {
            throw new MensajePersonalizadoException(Constants.ERR_TOKEN_MISSING);
        }

        final String jwt = extractBearerToken(tokenHeader);
        if (!StringUtils.hasText(jwt)) {
            throw new MensajePersonalizadoException(Constants.ERR_TOKEN_INVALID);
        }

        if (blacklistTokenRepository.existsByToken(jwt)) {
            throw new MensajePersonalizadoException(Constants.ERR_TOKEN_REVOKED);
        }

        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt);

            final String userCode = iJwtService.getCodeFromToken(jwt);

            Optional<AdministratorUserEntity> optUser =
                    administratorUserRepository.findByCode(userCode);

            if (optUser.isEmpty()) {
                throw new MensajePersonalizadoException(Constants.ERR_USER_NOT_FOUND);
            }

            final AdministratorUserDTO user =
                    administratorUserMapper.toDto(optUser.get());

            ResponseGeneralDTO response = new ResponseGeneralDTO();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(Constants.MSG_OK);
            response.setData(
                    AuthResponseDTO.builder()
                            .expirationTime(iJwtService.getTimeRemainingMillis(jwt))
                            .user(user.getEmail())
                            .build()
            );
            return response;

        } catch (ExpiredJwtException ex) {
            log.error(Constants.LOG_JWT_EXPIRED, ex.getMessage());
            throw new MensajePersonalizadoException(Constants.ERR_TOKEN_REVOKED);
        } catch (JwtException | IllegalArgumentException ex) {
            log.error(Constants.LOG_JWT_INVALID, ex.getMessage());
            throw new MensajePersonalizadoException(Constants.ERR_TOKEN_INVALID);
        }
    }

    /**
     * Authenticates the user credentials using Spring Security.
     *
     * @param requestLoginDTO login request containing email and password
     * @throws BadCredentialsException if authentication fails
     */
    private void authenticateUser(RequestLoginDTO requestLoginDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestLoginDTO.getEmail(),
                            requestLoginDTO.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new BadCredentialsException(Constants.ERR_AUTHENTICATION_FAILED, e);
        }
    }

    /**
     * Retrieves administrator user information by email.
     *
     * @param email the user email
     * @return the administrator user DTO
     * @throws IllegalStateException if the user is not found
     */
    private AdministratorUserDTO getUser(String email) {
        AdministratorUserDTO userDto = administratorUserMapper.toDto(
                administratorUserRepository.findByEmail(email).orElse(null)
        );
        if (userDto == null) {
            throw new IllegalStateException(Constants.ERR_STUDENT_NOT_FOUND);
        }
        return userDto;
    }

    /**
     * Generates a JWT token for the given administrator user.
     *
     * @param administratorUser the authenticated administrator user
     * @return the generated JWT token
     */
    private String generateToken(AdministratorUserDTO administratorUser) {
        UserDetails user =
                new AdministratorUserSecurityDTO(administratorUser, List.of());
        return iJwtService.getToken(user);
    }

    /**
     * Extracts the JWT token from the Authorization header.
     *
     * @param header the Authorization header value
     * @return the extracted JWT token
     * @throws MensajePersonalizadoException if the header format is invalid
     */
    private String extractBearerToken(String header) {
        String value = header.trim();
        if (value.regionMatches(true, 0,
                Constants.BEARER_PREFIX, 0,
                Constants.BEARER_PREFIX.length())) {
            return value.substring(Constants.BEARER_PREFIX.length()).trim();
        }
        throw new MensajePersonalizadoException(Constants.ERR_AUTH_HEADER_FORMAT);
    }
}
