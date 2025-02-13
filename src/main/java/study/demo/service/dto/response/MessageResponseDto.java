package study.demo.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MessageResponseDto {

    private String message;
    
    private Integer statusCode;
    
    private String messageCode;

}
