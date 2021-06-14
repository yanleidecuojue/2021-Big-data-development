package licona.club.sparkdesktopserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import licona.club.sparkdesktopserver.entity.Connection;
import licona.club.sparkdesktopserver.entity.User;
import licona.club.sparkdesktopserver.mapper.ConnectionMapper;
import licona.club.sparkdesktopserver.service.ConnectionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author licona
 * @since 2021-06-09
 */
@Service
public class ConnectionServiceImpl extends ServiceImpl<ConnectionMapper, Connection> implements ConnectionService {

    @Override
    public int putConnection(Connection connection) {
        Connection connection1 = baseMapper.selectOne(new QueryWrapper<Connection>().eq("connection_name", connection.getConnectionName()));
        if(connection1 != null) {
            return -1;
        }
        return baseMapper.putConnection(connection);
    }

    @Override
    public List<Connection> queryUserConnections(String userId) {
        return baseMapper.queryUserConnections(userId);
    }
}
