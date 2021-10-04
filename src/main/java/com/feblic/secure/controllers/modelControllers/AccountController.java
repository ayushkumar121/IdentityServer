package com.feblic.secure.controllers.modelControllers;

import com.feblic.secure.constants.user.UserActivationStatus;
import com.feblic.secure.constants.user.UserVerificationStatus;
import com.feblic.secure.models.users.UserModel;
import com.feblic.secure.services.modelServices.UsersModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccountController {

    @Autowired
    UsersModelService usersModelService;

    @GetMapping("/account/signup/complete")
    public String SignupComplete() {
        return "complete";
    }

    @GetMapping("/account/unauthorized")
    public String UnAuthorized() {
        return "unauthorized";
    }

    @GetMapping("/account/signup")
    public String Signup(
            String client_id,
            String redirect_uri,
            String scope,
            String state,
            Model model) {

        model.addAttribute("scope", scope);
        model.addAttribute("state", state);
        model.addAttribute("client_id", client_id);
        model.addAttribute("redirect_uri", redirect_uri);

        return "signup";
    }

    @PostMapping("/account/validate")
    public String ValidateCredentials(
            String name,
            String email,
            String mobile,
            String age,
            String password,
            String client_id,
            String redirect_uri,
            String scope,
            String state
            ) {
        UserModel userModel = new UserModel();

        if(usersModelService.findByEmail(email) == null)
        {
            String encoded = new BCryptPasswordEncoder().encode(password);

            userModel.setAge(Integer.parseInt(age));
            userModel.setName(name);
            userModel.setEmail(email);
            userModel.setMobile(mobile);
            userModel.setPassword(encoded);

            userModel.setUserActivationStatus(UserActivationStatus.ACTIVE);
            userModel.setUserVerificationStatus(UserVerificationStatus.UNVERIFIED);

            usersModelService.save(userModel);

            if(scope != null && state != null && client_id != null && redirect_uri != null)
                return "redirect:/oauth/authorize?client_id=" + client_id +
                    "&response_type=code" +
                    "&state=" + state +
                    "&redirect_uri=" + redirect_uri +
                    "&scope=" + scope ;
        }
        else
        {
            return "redirect:/account/unauthorized";
        }

        return "redirect:/account/signup/complete";
    }
}
