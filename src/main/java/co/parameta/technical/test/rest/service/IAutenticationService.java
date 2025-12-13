package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.rest.dto.RequestLoginDTO;

public interface IAutenticationService {

    ResponseGeneralDTO userLogin(RequestLoginDTO request) throws Exception;

    ResponseGeneralDTO userInformation(String tokenHeader);


}
