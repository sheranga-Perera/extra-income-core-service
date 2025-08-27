package com.phx.ei.core.service;

import com.phx.ei.common.dto.request.LoginRequest;
import com.phx.ei.common.dto.request.RegisterRequest;

public interface AuthService {

    /**
     * 
     * @param request
     * @return
     */
    public String register(RegisterRequest request);

    /**
     * 
     * @param request
     * @return
     */
    public String login(LoginRequest request);

}
