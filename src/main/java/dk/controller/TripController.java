package dk.controller;

import dk.model.entity.bt1.Trip;
import dk.model.entity.bt1.User;
import dk.model.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class TripController {

    @Autowired
    private TripService tripService;

    @GetMapping("/trips")
    public String showTrips(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(required = false) String departure,
                            @RequestParam(required = false) String destination,
                            HttpSession session,
                            Model model) {

        // Kiểm tra đăng nhập
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<Trip> trips;
        int totalPages;

        // Xử lý tìm kiếm
        if ((departure != null && !departure.trim().isEmpty()) ||
                (destination != null && !destination.trim().isEmpty())) {
            trips = tripService.searchTrips(departure, destination, page);
            totalPages = tripService.getTotalPages(departure, destination);
        } else {
            trips = tripService.getTrips(page);
            totalPages = tripService.getTotalPages();
        }

        // Thêm thông tin vào model
        model.addAttribute("user", user);
        model.addAttribute("trips", trips);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("departure", departure);
        model.addAttribute("destination", destination);

        // Tính toán phân trang
        int startPage = Math.max(1, page - 2);
        int endPage = Math.min(totalPages, page + 2);

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("hasNext", page < totalPages);
        model.addAttribute("hasPrevious", page > 1);

        return "bt1/trips";
    }
}