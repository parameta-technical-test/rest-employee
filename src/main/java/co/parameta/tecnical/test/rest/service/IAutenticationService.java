package co.parameta.tecnical.test.rest.service;

import co.parameta.tecnical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.tecnical.test.rest.dto.RequestLoginDTO;

public interface IAutenticationService {

    ResponseGeneralDTO userLogin(RequestLoginDTO request) throws Exception;

    ResponseGeneralDTO userInformation(String tokenHeader);


}
