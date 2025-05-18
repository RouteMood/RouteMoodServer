package ru.hse.routemood.image.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.hse.routemood.image.models.Image;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageSaveResponse {

    private UUID id;

    public ImageSaveResponse(Image image) {
        this.id = image.getId();
    }
}
