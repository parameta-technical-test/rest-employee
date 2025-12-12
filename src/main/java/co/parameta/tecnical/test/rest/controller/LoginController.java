package co.parameta.tecnical.test.rest.controller;

import co.parameta.tecnical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.tecnical.test.commons.service.ITokenBlacklistService;
import co.parameta.tecnical.test.rest.dto.RequestLoginDTO;
import co.parameta.tecnical.test.rest.service.IAutenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/login")
public class LoginController {

    private final ITokenBlacklistService iTokenBlacklistService;

    private final IAutenticationService iAutenticationService;

    @PostMapping("/autentication")
    public ResponseEntity<ResponseGeneralDTO> loginUsuario(
            @RequestBody RequestLoginDTO request
    ) throws Exception {
        ResponseGeneralDTO respuestaGeneralDTO = iAutenticationService.userLogin(request);
        return ResponseEntity.status(respuestaGeneralDTO.getStatus()).body(respuestaGeneralDTO);
    }

    @GetMapping("/information")
    public ResponseEntity<ResponseGeneralDTO> informationUser(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) throws Exception {
        ResponseGeneralDTO respuestaGeneralDTO = iAutenticationService.userInformation(authorizationHeader);
        return ResponseEntity.status(respuestaGeneralDTO.getStatus()).body(respuestaGeneralDTO);
    }


    @GetMapping("/log-out")
    public ResponseEntity<ResponseGeneralDTO> logOut(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader
    )  {
        return ResponseEntity.ok( iTokenBlacklistService.revokeToken(authorizationHeader));
    }


}
