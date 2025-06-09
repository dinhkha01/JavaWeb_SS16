package dk.model.service;

import dk.model.dao.bt1.AuthDao;
import dk.model.entity.bt1.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private AuthDao authDao;


    public boolean registerUser(User user) {
        // Kiểm tra username đã tồn tại
        if (authDao.isUsernameExists(user.getUsername())) {
            return false;
        }

        // Kiểm tra email đã tồn tại
        if (authDao.isEmailExists(user.getEmail())) {
            return false;
        }

        // Thực hiện đăng ký
        return authDao.registerUser(user);
    }

    // Đăng nhập
    public User authenticateUser(String username, String password) {
        return authDao.authenticateUser(username, password);
    }

    // Kiểm tra username có tồn tại không
    public boolean isUsernameExists(String username) {
        return authDao.isUsernameExists(username);
    }

    // Kiểm tra email có tồn tại không
    public boolean isEmailExists(String email) {
        return authDao.isEmailExists(email);
    }
}