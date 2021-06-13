package licona.club.sparkdesktopserver.mapper;

import licona.club.sparkdesktopserver.entity.Connection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author licona
 * @since 2021-06-09
 */
public interface ConnectionMapper extends BaseMapper<Connection> {
    int putConnection(Connection connection);
    List<Connection> queryUserConnections(String userId);
}
