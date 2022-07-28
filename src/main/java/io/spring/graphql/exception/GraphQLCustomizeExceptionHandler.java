package io.spring.graphql.exception;

import com.netflix.graphql.dgs.exceptions.DefaultDataFetcherExceptionHandler;
import com.netflix.graphql.types.errors.ErrorType;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import io.spring.graphql.types.Error;
import io.spring.graphql.types.ErrorItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.stereotype.Component;

/**
 * 自定义异常处理
 * 当验证失败时，我们希望向客户端返回有意义的错误消息。
 * 为了使客户端能够显示有用的错误消息，我们应该返回一个数据结构，其中包含每个验证失败的错误消息。
 */
@Component
public class GraphQLCustomizeExceptionHandler implements DataFetcherExceptionHandler {

    private final DataFetcherExceptionHandler defaultHandler=
            new DefaultDataFetcherExceptionHandler();



   @Override
    public DataFetcherExceptionHandlerResult onException(
            DataFetcherExceptionHandlerParameters handlerParameters) {
       //如果是身份认证异常
        if (handlerParameters.getException() instanceof InvalidAuthenticationException) {
            GraphQLError graphqlError =
                    TypedGraphQLError.newBuilder()
                            .errorType(ErrorType.UNAUTHENTICATED)
                            .message(handlerParameters.getException().getMessage())
                            .path(handlerParameters.getPath())
                            .build();
            return DataFetcherExceptionHandlerResult.newResult().error(graphqlError).build();
        } else if (handlerParameters.getException() instanceof ConstraintViolationException) {
            //如果是自定义注解校验异常，遍历注解异常将其封装到FieldErrorResource中，最后将其添加到集合中
            List<FieldErrorResource> errors = new ArrayList<>();
            for (ConstraintViolation<?> violation :
                    ((ConstraintViolationException) handlerParameters.getException())
                            .getConstraintViolations()) {
                FieldErrorResource fieldErrorResource =
                        new FieldErrorResource(
                                violation.getRootBeanClass().getName(),
                                getParam(violation.getPropertyPath().toString()),
                                violation
                                        .getConstraintDescriptor()
                                        .getAnnotation()
                                        .annotationType()
                                        .getSimpleName(),
                                violation.getMessage());
                errors.add(fieldErrorResource);
            }
            GraphQLError graphqlError =
                    TypedGraphQLError.newBadRequestBuilder()
                            .message(handlerParameters.getException().getMessage())
                            .path(handlerParameters.getPath())
                            .extensions(errorsToMap(errors))
                            .build();
            return DataFetcherExceptionHandlerResult.newResult().error(graphqlError).build();
        } else {//如果是其他的异常直接返回
            return defaultHandler.onException(handlerParameters);
        }
    }



    /**
     * 从异常ConstraintViolationException cve中读取有关违规的信息，
     * 并将它们转换为我们的 Error（String message，List<ErrorItem> 其中 Error的数据结构为String key,List<String> value） 数据结构。
     * @param cve
     * @return
     */
    public static Error getErrorsAsData(ConstraintViolationException cve) {
        //创建错误的list集合
        List<FieldErrorResource> errors=new ArrayList<FieldErrorResource>();
        //遍历cve验证过程中报告的违反约束条件的集合
        for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
            //封装
            FieldErrorResource fieldErrorResource=new FieldErrorResource(
                    violation.getRootBeanClass().getName(),
                    //返回rootBean的值的属性路径
                    getParam(violation.getPropertyPath().toString()),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                    violation.getMessage()
            );
            //将这些字段错误资源添加到list集合中
            errors.add(fieldErrorResource);
        }

        //创建map集合，key为String，value为字符串类型的list集合
        Map<String,List<String>> errorMap=new HashMap<>();
        //遍历错误字段资源的list集合
        for (FieldErrorResource fieldErrorResource : errors) {
            //如果map的key中没有包含该错误的字段，则将其字段添加到map集合中
            if(!errorMap.containsKey(fieldErrorResource)){
                errorMap.put(fieldErrorResource.getField(), new ArrayList<>());
            }
            //如果map的key中存在对应的错误字段，则将该字段对应的错误信息添加到map对应的value集合中
            errorMap.get(fieldErrorResource.getField()).add(fieldErrorResource.getMessage());
        }
        //将map集合转为ErrorItem对应的list集合
        List<ErrorItem> errorItems = errorMap.entrySet().stream()
                .map(kv -> ErrorItem.newBuilder().key(kv.getKey()).value(kv.getValue()).build())
                .collect(Collectors.toList());
        //返回
        return Error.newBuilder().message("BAD_REQUEST").errors(errorItems).build();

    }

    private static String getParam(String s) {
        String[] splits = s.split("\\.");
        if(splits.length==1){
            return s;
        }else{
            /**
             * 参数。
             * delimiter - 分隔每个元素的分隔符
             * elements - 要连接在一起的元素。
             * 返回一个新的字符串，由分界符分隔的元素组成。
             */
            return String.join(".", Arrays.copyOfRange(splits,2,splits.length));
        }
    }


    private static Map<String, Object> errorsToMap(List<FieldErrorResource> errors) {
        Map<String, Object> json = new HashMap<>();
        for (FieldErrorResource fieldErrorResource : errors) {
            if (!json.containsKey(fieldErrorResource.getField())) {
                json.put(fieldErrorResource.getField(), new ArrayList<>());
            }
            ((List) json.get(fieldErrorResource.getField())).add(fieldErrorResource.getMessage());
        }
        return json;
    }

}
