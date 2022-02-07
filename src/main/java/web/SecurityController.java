package web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import util.Message;

import java.util.Locale;


@Controller
@RequestMapping("/home")
public class SecurityController {
    private final Logger logger = LoggerFactory.getLogger(SecurityController.class);

    @Autowired
    private MessageSource messageSource;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public String home(Model model, Locale locale) {
        logger.info("Home");
        model.addAttribute("home", new Message("message", messageSource.getMessage("welcome_titlepane", new Object[]{"Home page"}, locale)));
        return "home";
    }
}
