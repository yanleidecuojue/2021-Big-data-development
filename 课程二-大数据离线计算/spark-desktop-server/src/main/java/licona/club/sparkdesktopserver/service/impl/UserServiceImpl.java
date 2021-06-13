package licona.club.sparkdesktopserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import licona.club.sparkdesktopserver.entity.User;
import licona.club.sparkdesktopserver.mapper.UserMapper;
import licona.club.sparkdesktopserver.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author licona
 * @since 2021-06-09
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public User findUserById(String userId) {
        return baseMapper.selectOne(new QueryWrapper<User>().eq("id", userId));
    }

    @Override
    public User findUserByUsername(String username) {
        return baseMapper.selectOne(new QueryWrapper<User>().eq("username", username));
    }
}
