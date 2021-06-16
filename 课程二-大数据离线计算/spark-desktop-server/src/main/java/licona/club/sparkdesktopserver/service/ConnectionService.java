package licona.club.sparkdesktopserver.service;

import licona.club.sparkdesktopserver.entity.Connection;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author licona
 * @since 2021-06-09
 */
public interface ConnectionService extends IService<Connection> {
    int putConnection(Connection connection);
    List<Connection> queryUserConnections(String userId);
    Connection findConnectionByCname(String connectionName);
    int updateConnection(Connection connection);
}
