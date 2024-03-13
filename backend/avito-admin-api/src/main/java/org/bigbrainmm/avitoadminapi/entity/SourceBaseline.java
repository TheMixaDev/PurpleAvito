package org.bigbrainmm.avitoadminapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity основной ценовой матирцы в базе данных
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "source_baseline")
public class SourceBaseline {
    /**
     * Id сущности
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Название основной матрицы
     */
    @Column(name = "name")
    private String name;
    /**
     * Готова ли эта матрица к установке
     * Устаналивается в true, только после успешной загрузки данных
     * Флаг добавлен во избежании конфликтов, когда матрица лишь частично загружена, а уже используется
     */
    @Column(name="ready")
    private Boolean ready;

    /**
     * Instantiates a new Source baseline.
     *
     * @param name  the name
     * @param ready the ready
     */
    public SourceBaseline(String name, Boolean ready) {
        this.name = name;
        this.ready = ready;
    }
}
