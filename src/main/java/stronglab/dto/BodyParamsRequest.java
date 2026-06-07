package stronglab.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BodyParamsRequest {
    private Float weight;
    private Float height;

    public BodyParamsRequest() {
    }

    public BodyParamsRequest(Float weight, Float height) {
        this.weight = weight;
        this.height = height;
    }
}