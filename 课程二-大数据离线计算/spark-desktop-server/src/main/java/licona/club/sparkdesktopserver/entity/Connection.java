package licona.club.sparkdesktopserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author licona
 * @since 2021-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Connection implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long uid;

    private String connectionName;

    private String connectionHost;

    private String connectionPort;

    private String connectionDatabaseName;

    private String connectionUsername;

    private String connectionPassword;


}
