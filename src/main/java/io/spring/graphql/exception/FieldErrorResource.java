package io.spring.graphql.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)//在写入和读取 JSON 时，将忽略带注释的文件。将这个注解写在类上之后，就会忽略类中不存在的字段
@Getter
@AllArgsConstructor
public class FieldErrorResource {
    private String resource;
    private String field;
    private String code;
    private String message;

}


