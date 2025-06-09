package dk.controller;

import dk.model.entity.bt1.User;
import dk.model.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private AuthenticationService authService;

    // Hiển thị form đăng ký
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "bt1/register";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute User user,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        // Kiểm tra validation errors
        if (result.hasErrors()) {
            return "bt1/register";
        }

        // Kiểm tra username đã tồn tại
        if (authService.isUsernameExists(user.getUsername())) {
            model.addAttribute("usernameError", "Username đã tồn tại!");
            return "bt1/register";
        }

        // Kiểm tra email đã tồn tại
        if (authService.isEmailExists(user.getEmail())) {
            model.addAttribute("emailError", "Email đã được sử dụng!");
            return "bt1/register";
        }

        // Thực hiện đăng ký
        boolean success = authService.registerUser(user);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } else {
            model.addAttribute("errorMessage", "Đăng ký thất bại! Vui lòng thử lại.");
            return "bt1/register";
        }
    }

    // Hiển thị form đăng nhập
    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("user", new User());
        return "bt1/login";
    }

    // Xử lý đăng nhập
    @PostMapping("/login")
    public String login(@ModelAttribute User user,
                        Model model,
                        HttpSession session,
                        BindingResult result) {
        if(result.hasErrors()) {
            return "bt1/login";
        }
        // Xác thực người dùng
        User authenticatedUser = authService.authenticateUser(user.getUsername(), user.getPassword());

        if (authenticatedUser != null) {
            // Lưu thông tin user vào session
            session.setAttribute("loggedInUser", authenticatedUser);
            session.setAttribute("username", authenticatedUser.getUsername());
            session.setAttribute("role", authenticatedUser.getRole());

            // Kiểm tra role và chuyển hướng
            if ("ADMIN".equals(authenticatedUser.getRole())) {
                return "redirect:/admin";
            } else {
                return "redirect:/home";
            }
        } else {
            model.addAttribute("errorMessage", "Thông tin đăng nhập không chính xác!");
            return "bt1/login";
        }
    }


    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "bt1/trips";
    }


    @GetMapping("/admin")
    public String admin(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "bt1/admin";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "Đăng xuất thành công!");
        return "redirect:/login";
    }
}