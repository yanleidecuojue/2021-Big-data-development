package licona.club.sparkdesktopserver.service;

import licona.club.sparkdesktopserver.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author licona
 * @since 2021-06-09
 */
public interface UserService extends IService<User> {
    User findUserById(String userId);
    User findUserByUsername(String username);
}
