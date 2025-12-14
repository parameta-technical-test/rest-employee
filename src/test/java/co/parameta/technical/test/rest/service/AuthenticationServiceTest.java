package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.commons.dto.AdministratorUserDTO;
import co.parameta.technical.test.commons.entity.AdministratorUserEntity;
import co.parameta.technical.test.commons.repository.BlacklistTokenRepository;
import co.parameta.technical.test.commons.service.IJwtService;
import co.parameta.technical.test.commons.service.ITokenBlacklistService;
import co.parameta.technical.test.commons.util.exception.MensajePersonalizadoException;
import co.parameta.technical.test.commons.util.mapper.AdministratorUserMapper;
import co.parameta.technical.test.rest.dto.AuthResponseDTO;
import co.parameta.technical.test.rest.dto.RequestLoginDTO;
import co.parameta.technical.test.rest.repository.AdministratorUserRepository;
import co.parameta.technical.test.rest.service.impl.AutenticationService;
import co.parameta.technical.test.rest.util.constant.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AutenticationService authenticationService;

    @Mock
    private IJwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ITokenBlacklistService tokenBlacklistService;

    @Mock
    private BlacklistTokenRepository blacklistTokenRepository;

    @Mock
    private AdministratorUserRepository administratorUserRepository;

    @Mock
    private AdministratorUserMapper administratorUserMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                authenticationService,
                "secretKey",
                "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWYwMTIzNDU2Nzg5YWJjZGVm"
        );
    }

    @Test
    void loginSuccess() {
        RequestLoginDTO request = new RequestLoginDTO();
        request.setEmail("uno@gmail.com");
        request.setPassword("12345");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        AdministratorUserEntity entity = new AdministratorUserEntity();
        entity.setEmail("uno@gmail.com");

        AdministratorUserDTO dto = new AdministratorUserDTO();
        dto.setEmail("uno@gmail.com");
        dto.setCode("U001");

        when(administratorUserRepository.findByEmail(eq("uno@gmail.com")))
                .thenReturn(Optional.of(entity));

        when(administratorUserMapper.toDto(eq(entity)))
                .thenReturn(dto);

        when(jwtService.getToken(any()))
                .thenReturn("jwt.token.value");

        when(jwtService.getTimeRemainingMillis(eq("jwt.token.value")))
                .thenReturn(60000L);

        var response = authenticationService.userLogin(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Constants.MSG_LOGIN_SUCCESS, response.getMessage());
        assertNotNull(response.getData());
        assertTrue(response.getData() instanceof AuthResponseDTO);

        AuthResponseDTO data = (AuthResponseDTO) response.getData();
        assertEquals("jwt.token.value", data.getToken());
        assertEquals(60000L, data.getExpirationTime());
        assertEquals("uno@gmail.com", data.getUser());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(administratorUserRepository, times(1))
                .findByEmail("uno@gmail.com");
        verify(jwtService, times(1))
                .getToken(any());
        verify(jwtService, times(1))
                .getTimeRemainingMillis("jwt.token.value");
    }

    @Test
    void loginInvalidCredentialsThrowsBadCredentials() {
        RequestLoginDTO request = new RequestLoginDTO();
        request.setEmail("uno@gmail.com");
        request.setPassword("bad");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authenticationService.userLogin(request));
    }

    @Test
    void userInfoEmptyHeaderThrowsCustomMessage() {
        MensajePersonalizadoException ex = assertThrows(
                MensajePersonalizadoException.class,
                () -> authenticationService.userInformation("")
        );
        assertEquals(Constants.ERR_TOKEN_MISSING, ex.getMessage());
    }

    @Test
    void userInfoHeaderWithoutBearerThrowsCustomMessage() {
        MensajePersonalizadoException ex = assertThrows(
                MensajePersonalizadoException.class,
                () -> authenticationService.userInformation("Token xyz")
        );
        assertEquals(Constants.ERR_AUTH_HEADER_FORMAT, ex.getMessage());
    }

    @Test
    void userInfoBlacklistedTokenThrowsCustomMessage() {
        String jwt = "abc.def.ghi";
        String header = "Bearer " + jwt;

        when(blacklistTokenRepository.existsByToken(eq(jwt)))
                .thenReturn(true);

        MensajePersonalizadoException ex = assertThrows(
                MensajePersonalizadoException.class,
                () -> authenticationService.userInformation(header)
        );
        assertEquals(Constants.ERR_TOKEN_REVOKED, ex.getMessage());
    }
}