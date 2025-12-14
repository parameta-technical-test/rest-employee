package co.parameta.technical.test.rest.controller;

import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.commons.service.ITokenBlacklistService;
import co.parameta.technical.test.rest.dto.RequestLoginDTO;
import co.parameta.technical.test.rest.service.IAutenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @Mock
    private ITokenBlacklistService tokenBlacklistService;

    @Mock
    private IAutenticationService authenticationService;

    @Test
    void loginUserSuccess() throws Exception {

        RequestLoginDTO request = new RequestLoginDTO();
        request.setEmail("uno@gmail.com");
        request.setPassword("12345");

        ResponseGeneralDTO responseDto = new ResponseGeneralDTO();
        responseDto.setStatus(HttpStatus.OK.value());
        responseDto.setMessage("Login successful");

        when(authenticationService.userLogin(any(RequestLoginDTO.class)))
                .thenReturn(responseDto);

        ResponseEntity<ResponseGeneralDTO> response =
                loginController.loginUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());
        assertEquals("Login successful", response.getBody().getMessage());
    }

    @Test
    void getUserInformationSuccess() throws Exception {

        String token = "Bearer test.jwt.token";

        ResponseGeneralDTO responseDto = new ResponseGeneralDTO();
        responseDto.setStatus(HttpStatus.OK.value());
        responseDto.setMessage("User information retrieved");

        when(authenticationService.userInformation(eq(token)))
                .thenReturn(responseDto);

        ResponseEntity<ResponseGeneralDTO> response =
                loginController.informationUser(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());
        assertEquals("User information retrieved", response.getBody().getMessage());
    }

    @Test
    void logoutSuccess() {

        String token = "Bearer test.jwt.token";

        ResponseGeneralDTO responseDto = new ResponseGeneralDTO();
        responseDto.setStatus(HttpStatus.OK.value());
        responseDto.setMessage("Logout successful");

        when(tokenBlacklistService.revokeToken(eq(token)))
                .thenReturn(responseDto);

        ResponseEntity<ResponseGeneralDTO> response =
                loginController.logOut(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());
        assertEquals("Logout successful", response.getBody().getMessage());
    }
}

