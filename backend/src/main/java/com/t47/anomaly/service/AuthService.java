package com.t47.anomaly.service;

import com.t47.anomaly.dto.LoginDTO;
import com.t47.anomaly.dto.RegisterDTO;
import com.t47.anomaly.vo.LoginVO;
import com.t47.anomaly.vo.UserVO;

/**
 * 认证服务：注册、登录、当前用户。
 */
public interface AuthService {

    LoginVO login(LoginDTO dto);

    UserVO register(RegisterDTO dto);

    UserVO currentUser();
}
