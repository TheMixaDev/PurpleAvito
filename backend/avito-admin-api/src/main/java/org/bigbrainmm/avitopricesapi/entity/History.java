package org.bigbrainmm.avitopricesapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity Истории в базе данных
 */
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
    /**
     * Название матрицы, на которую осуществилась замена
     */
    @Column(name = "name")
    private String name;
    @Column(name = "segment_id", nullable = true)
    private Long segmentId;
    @Column(name = "timestamp")
    private Long timestamp;

    /**
     * Instantiates a new History.
     *
     * @param name      the name
     * @param segmentId the segment id
     * @param timestamp the timestamp
     */
    public History(String name, Long segmentId, Long timestamp) {
        this.name = name;
        this.segmentId = segmentId;
        this.timestamp = timestamp;
    }

    /**
     * Instantiates a new History.
     *
     * @param name      the name
     * @param timestamp the timestamp
     */
    public History(String name, Long timestamp) {
        this.name = name;
        this.timestamp = timestamp;
    }
}
