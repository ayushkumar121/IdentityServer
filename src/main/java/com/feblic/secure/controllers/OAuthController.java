package com.feblic.secure.controllers;

import com.feblic.secure.constants.jwt.TokenType;
import com.feblic.secure.dto.TokensDTO;
import com.feblic.secure.dto.TokensRequestDTO;
import com.feblic.secure.exceptions.FeblicBadRequestException;
import com.feblic.secure.exceptions.FeblicUnauthorizedException;
import com.feblic.secure.models.auth.AuthClient;
import com.feblic.secure.models.auth.AuthCode;
import com.feblic.secure.models.auth.AuthToken;
import com.feblic.secure.models.users.UserModel;
import com.feblic.secure.services.auth.AuthClientService;
import com.feblic.secure.services.auth.AuthCodeService;
import com.feblic.secure.services.auth.AuthTokenService;
import com.feblic.secure.services.modelServices.UsersModelService;
import com.feblic.secure.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.UUID;

@Controller
public class OAuthController {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UsersModelService usersModelService;

    @Autowired
    AuthCodeService authCodeService;

    @Autowired
    AuthClientService authClientService;

    @Autowired
    AuthTokenService authTokenService;

    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @GetMapping("/oauth/unauthorized")
    public String UnAuthorized() {
        return "unauthorized";
    }

    @GetMapping("/oauth/authorize")
    public String Authorize(
            String response_type,
            String client_id,
            String redirect_uri,
            String scope,
            String state,
            HttpServletRequest request,
            Model model) {

        if(!response_type.equals("code") || authClientService.findByClientIdAndRedirectUri(client_id, redirect_uri) == null)
            return "redirect:/oauth/unauthorized";

        HttpSession session = request.getSession(false);
        if(session != null && session.getAttribute("user-id") != null) {
            String uuid = UUID.randomUUID().toString();

            AuthCode authCode = new AuthCode();
            authCode.setUserId((Long) session.getAttribute("user-id"));
            authCode.setAuthCode(uuid);
            authCode.setAuthClient(authClientService.findByClientIdAndRedirectUri(client_id, redirect_uri));
            authCodeService.save(authCode);

            return  "redirect:" + redirect_uri + "?code=" + uuid + "&state=" + state;
        }

        model.addAttribute("scope", scope);
        model.addAttribute("state", state);
        model.addAttribute("client_id", client_id);
        model.addAttribute("redirect_uri", redirect_uri);

        return "authorize";
    }

    @PostMapping("/oauth/validate")
    public String ValidateCredentials(
            String username,
            String password,
            String state,
            String client_id,
            String redirect_uri,
            HttpServletRequest request) {

        UserModel user = usersModelService.findByEmail(username);

        if (user != null && new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            String uuid = UUID.randomUUID().toString();

            AuthCode authCode = new AuthCode();
            authCode.setUserId(user.getId());
            authCode.setAuthCode(uuid);
            authCode.setAuthClient(authClientService.findByClientIdAndRedirectUri(client_id, redirect_uri));
            authCodeService.save(authCode);

            HttpSession session = request.getSession(true);
            session.setAttribute("user-id", user.getId());

            return  "redirect:" + redirect_uri + "?code=" + uuid + "&state=" + state;
        } else {
            return "redirect:/oauth/unauthorized";
        }
    }

    @PostMapping("/oauth/token")
    public ResponseEntity<TokensDTO> Token(@Valid @RequestBody TokensRequestDTO tokensRequestDTO) {
        try{
            UserModel user = null;
            AuthClient authClient = null;

            if (tokensRequestDTO.getGrantType().equals("code")) {
                AuthCode authCode = authCodeService.findByAuthCode(tokensRequestDTO.getCode());

                if(authCode != null) {
                    authClient = authCode.getAuthClient();
                    authCodeService.permanentDelete(authCode.getId());
                }

                if(authClient != null && authClient.getClientId().equals(tokensRequestDTO.getClientId())
                        && authClient.getClientSecret().equals(tokensRequestDTO.getClientSecret())) {
                    user = usersModelService.find(authCode.getUserId());
                }
            }
            else if (tokensRequestDTO.getGrantType().equals("refresh_token") && jwtUtil.validateToken(tokensRequestDTO.getRefreshToken(), TokenType.REFRESH_TOKEN)) {
                AuthToken refreshToken = authTokenService.findByToken(tokensRequestDTO.getRefreshToken());
                authClient = refreshToken != null ? refreshToken.getAuthClient() : null;

                if(refreshToken != null) {
                    authClient = refreshToken.getAuthClient();
                    authTokenService.permanentDelete(refreshToken.getId());
                }

                if(authClient != null && authClient.getClientId().equals(tokensRequestDTO.getClientId())
                        && authClient.getClientSecret().equals(tokensRequestDTO.getClientSecret())) {
                    user = usersModelService.find(refreshToken.getUserId());
                }
            }
            else if (tokensRequestDTO.getGrantType().equals("password")) {
                UserModel entity = usersModelService.findByEmail(tokensRequestDTO.getUsername());
                authClient = authClientService.findByClientIdAndClientSecret(tokensRequestDTO.getClientId(), tokensRequestDTO.getClientSecret());

                if(authClient != null && entity != null && new BCryptPasswordEncoder().matches(tokensRequestDTO.getPassword(), entity.getPassword())) {
                    user = entity;
                }
            }

            if(user == null) {
                throw new FeblicUnauthorizedException("Invalid grant");
            }

            TokensDTO tokens = new TokensDTO(
                    user.getId(),
                    jwtUtil.generateToken(user.getId(), TokenType.ACCESS_TOKEN),
                    jwtUtil.generateToken(user.getId(), TokenType.REFRESH_TOKEN),
                    new Date(System.currentTimeMillis() + jwtUtil.accessTokenValidity)
            );

            AuthToken accessToken = new AuthToken();
            accessToken.setTokenType(TokenType.ACCESS_TOKEN);
            accessToken.setUserId(user.getId());
            accessToken.setClientId(tokensRequestDTO.getClientId());
            accessToken.setAuthClient(authClient);
            accessToken.setValidity(new Date(System.currentTimeMillis() + jwtUtil.accessTokenValidity));
            accessToken.setToken(tokens.getAccessToken());

            authTokenService.save(accessToken);

            AuthToken refreshToken = new AuthToken();
            refreshToken.setTokenType(TokenType.REFRESH_TOKEN);
            refreshToken.setUserId(user.getId());
            refreshToken.setClientId(tokensRequestDTO.getClientId());
            refreshToken.setAuthClient(authClient);
            refreshToken.setValidity(new Date(System.currentTimeMillis() + jwtUtil.refreshTokenValidity));
            refreshToken.setToken(tokens.getRefreshToken());

            authTokenService.save(refreshToken);
            return new ResponseEntity<>(tokens, HttpStatus.OK);
        } catch(Exception exception) {
            throw new FeblicBadRequestException(exception.getMessage());
        }
    }
}
