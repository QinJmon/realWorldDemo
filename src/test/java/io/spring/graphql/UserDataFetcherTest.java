package io.spring.graphql;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {DgsAutoConfiguration.class,UserMutation.class})
class UserDataFetcherTest {

    @Autowired
    DgsQueryExecutor dgsQueryExecutor;

    @Test
    void testCreateUser() {
        Object result = dgsQueryExecutor.executeAndExtractJsonPath(
                "mutation{\n" +
                        "  createUser(input:{\n" +
                        "      username:\"username-test\",\n" +
                        "      password:\"111\",\n" +
                        "      email:\"2656589898@qq.com\"})\n" +
                        "      {\n" +
                        "        ... on UserPayload{\n" +
                        "            user{\n" +
                        "                username,\n" +
                        "                email\n" +
                        "            }\n" +
                        "        }\n" +
                        "        ... on Error{\n" +
                        "            message,\n" +
                        "            errors{\n" +
                        "                key,\n" +
                        "                value\n" +
                        "            }\n" +
                        "           \n" +
                        "        } \n" +
                        "    }\n" +
                        "}", "data.createUser"
        );
    }
}