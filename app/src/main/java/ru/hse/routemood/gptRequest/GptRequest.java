package ru.hse.routemood.gptRequest;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GptRequest {
    private String request;
    private Double longitude;
    private Double latitude;
}
