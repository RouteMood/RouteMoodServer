package ru.hse.routemood.video.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "media")
public class UploadMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String originalPath;
    private String username;
}
