package io.spring.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import io.spring.application.UserQueryService;
import io.spring.application.data.UserData;
import io.spring.application.data.UserWithToken;
import io.spring.core.service.JwtService;
import io.spring.graphql.exception.ResourceNotFoundException;
import io.spring.graphql.types.User;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestHeader;


@DgsComponent
@AllArgsConstructor
public class MeDatafetcher {
    private UserQueryService userQueryService;

    private JwtService jwtService;

    @DgsData(parentType =DgsConstants.QUERY_TYPE,field = DgsConstants.QUERY.Me)
    public DataFetcherResult<User> getMe(@RequestHeader(value = "Authorization")String authorization,
            DataFetchingEnvironment dataFetchingEnvironment){
        //得到authentication进行判断
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof AnonymousAuthenticationToken || authentication.getPrincipal()==null){
            return null;
        }
        io.spring.core.user.User user = (io.spring.core.user.User) authentication.getPrincipal();
        //找不到交给自定义的异常
        UserData userData=userQueryService.findById(user.getId()).orElseThrow(ResourceNotFoundException::new);
        //用户携带者token
        UserWithToken userWithToken=new UserWithToken(userData,authorization.split(" ")[1]);
        //构建data
        User result = User.newBuilder()
                .email(userWithToken.getEmail())
                .username(userWithToken.getUsername())
                .token(userWithToken.getToken())
                .build();
        return DataFetcherResult.<User>newResult()
                .data(result)
                //.localContext(userData)
                .localContext(user)
                .build();


    }

   @DgsData(parentType = DgsConstants.USERPAYLOAD.TYPE_NAME,field = DgsConstants.USERPAYLOAD.User)
    public DataFetcherResult<User> getUserPayloadUser(DataFetchingEnvironment dataFetchingEnvironment){
        //获得应用程序上下文
       io.spring.core.user.User user = dataFetchingEnvironment.getLocalContext();
       //构建结果
       User result = User.newBuilder()
               .email(user.getEmail())
               .username(user.getUsername())
               .token(jwtService.toToken(user))
               .build();
       //返回结果
       return DataFetcherResult.<User>newResult()
               .data(result)
               .localContext(user)
               .build();
    }


}
