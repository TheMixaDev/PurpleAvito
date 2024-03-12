package org.bigbrainmm.avitopricesapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "history")
public class History {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "segment_id", nullable = true)
    private Long segmentId;
    @Column(name = "timestamp")
    private Long timestamp;

    public History(String name, Long segmentId, Long timestamp) {
        this.name = name;
        this.segmentId = segmentId;
        this.timestamp = timestamp;
    }

    public History(String name, Long timestamp) {
        this.name = name;
        this.timestamp = timestamp;
    }
}
