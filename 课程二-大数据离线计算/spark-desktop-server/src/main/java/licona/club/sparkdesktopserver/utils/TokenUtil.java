package licona.club.sparkdesktopserver.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import licona.club.sparkdesktopserver.entity.User;

public class TokenUtil {
    public static String getToken(User user) {
        String token = "";
        token = JWT.create().withAudience(user.getId().toString())
                .sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }

    public static String decodeToken(String token) {
        String userId = JWT.decode(token).getAudience().get(0);
        return userId;
    }

}
