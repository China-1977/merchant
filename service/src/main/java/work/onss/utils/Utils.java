package work.onss.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import work.onss.exception.ServiceException;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class Utils {

    public static void uploadFile(MultipartFile file, String filePath) throws ServiceException, IOException {
        Path path = Paths.get(filePath);
        Path parent = path.getParent();
        if (!Files.exists(parent) && !parent.toFile().mkdirs()) {
            throw new ServiceException("FAIL", "上传失败!");
        }
        if (!Files.exists(path)) {
            file.transferTo(path);
        }
    }

    public static String getFilePath(String dir, String filename, String... more) throws ServiceException {
        Path path = Paths.get(dir, more);
        path = path.resolve(filename);
        int count = Paths.get(dir).getNameCount();
        return StringUtils.cleanPath(path.subpath(count - 1, path.getNameCount()).toString());
    }

    public static String authorization(String secret, String issuer, Instant instant, String subject, String jwtId, String... audience) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTCreator.Builder jwt = JWT.create()
                .withIssuer(issuer)
                .withAudience(audience)
                .withNotBefore(Date.from(instant))
                .withIssuedAt(Date.from(instant))
                .withExpiresAt(Date.from(instant.plusSeconds(1800)));

        return jwt
                .withSubject(subject)
                .withJWTId(jwtId)
                .sign(algorithm);
    }

    public static Map<String, String> OPENTM207940503(String first, String keyword1, String keyword2, String keyword3, String remark) {
        Map<String, String> data = new HashMap<>();
        data.put("first", first);
        data.put("keyword1", keyword1);
        data.put("keyword2", keyword2);
        data.put("keyword3", keyword3);
        data.put("remark", remark);
        return data;
    }

}
