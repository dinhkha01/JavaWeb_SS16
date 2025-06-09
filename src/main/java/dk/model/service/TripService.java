package dk.model.service;

import dk.model.dao.bt1.TripDao;
import dk.model.entity.bt1.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripService {

    @Autowired
    private TripDao tripDao;

    private static final int PAGE_SIZE = 6; // Số chuyến bay trên mỗi trang

    // Lấy danh sách chuyến bay với phân trang
    public List<Trip> getTrips(int page) {
        int offset = (page - 1) * PAGE_SIZE;
        return tripDao.getTrips(offset, PAGE_SIZE);
    }

    // Tìm kiếm chuyến bay với phân trang
    public List<Trip> searchTrips(String departure, String destination, int page) {
        int offset = (page - 1) * PAGE_SIZE;
        return tripDao.searchTrips(departure, destination, offset, PAGE_SIZE);
    }

    // Lấy tổng số trang
    public int getTotalPages() {
        int totalTrips = tripDao.getTripsCount();
        return (int) Math.ceil((double) totalTrips / PAGE_SIZE);
    }

    // Lấy tổng số trang theo điều kiện tìm kiếm
    public int getTotalPages(String departure, String destination) {
        int totalTrips = tripDao.getTripsCount(departure, destination);
        return (int) Math.ceil((double) totalTrips / PAGE_SIZE);
    }

    // Lấy size trang
    public int getPageSize() {
        return PAGE_SIZE;
    }
}