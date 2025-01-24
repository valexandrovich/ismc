package ua.com.valexa.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.com.valexa.apigateway.model.AccessRule;
import ua.com.valexa.apigateway.repository.AccessRuleRepository;
import ua.com.valexa.apigateway.utils.HashUtils;

import java.util.List;

@SpringBootTest
class ApiGatewayApplicationTests {

    @Autowired
    AccessRuleRepository accessRuleRepository;

    @Test
    void contextLoads() {

        String ju = "JesusChrist";
        System.out.println(HashUtils.hashString(ju));

    }

}
