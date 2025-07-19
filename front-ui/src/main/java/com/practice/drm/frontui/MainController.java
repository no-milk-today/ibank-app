package com.practice.drm.frontui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {

    private final FrontUiService frontUiService;

    @GetMapping("/")
    public String index() {
        return "redirect:/main";
    }

    @GetMapping("/main")
    public String main(@RequestParam("login") String login, Model model) {
        log.info("Main page requested for user: {}", login);

        try {
            var mainData = frontUiService.getMainPageData(login);

            // Добавляем все данные в модель для Thymeleaf шаблона main.html
            model.addAttribute("login", mainData.login());
            model.addAttribute("name", mainData.name());
            model.addAttribute("email", mainData.email());
            model.addAttribute("birthdate", mainData.birthdate());
            model.addAttribute("accounts", mainData.accounts());
            model.addAttribute("currency", mainData.currency());
            model.addAttribute("users", mainData.users());
            model.addAttribute("passwordErrors", mainData.passwordErrors());
            model.addAttribute("userAccountsErrors", mainData.userAccountsErrors());
            model.addAttribute("cashErrors", mainData.cashErrors());
            model.addAttribute("transferErrors", mainData.transferErrors());
            model.addAttribute("transferOtherErrors", mainData.transferOtherErrors());

            return "main";
        } catch (Exception e) {
            log.error("Error fetching main page data for user: {}", login, e);
            model.addAttribute("error", "Ошибка при загрузке данных пользователя: " + e.getMessage());
            model.addAttribute("login", login);
            return "main";
        }
    }

    @PostMapping("/user/{login}/editPassword")
    public String editPassword(
            @PathVariable("login") String login,
            @RequestParam("password") String password,
            @RequestParam("confirm_password") String confirmPassword,  // здесь имя из формы
            Model model
    ) {
        List<String> errors = frontUiService.changePassword(login, password, confirmPassword);
        return redirectToMainWithErrors(login, model, "passwordErrors", errors);
    }

    private String redirectToMainWithErrors(
            String login, Model model, String field, List<String> errors
    ) {
        // Получаем свежие данные для main
        var mainData = frontUiService.getMainPageData(login);
        model.addAllAttributes(Map.of(
                "login", mainData.login(),
                "name", mainData.name(),
                "email", mainData.email(),
                "birthdate", mainData.birthdate(),
                "accounts", mainData.accounts(),
                "currency", mainData.currency(),
                "users", mainData.users(),
                field, errors
        ));
        return "main";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }
}
