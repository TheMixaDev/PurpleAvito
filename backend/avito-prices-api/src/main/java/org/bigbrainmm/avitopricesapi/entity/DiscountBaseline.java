package org.bigbrainmm.avitopricesapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity скидочной матирцы в базе данных
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "discount_baseline")
public class DiscountBaseline {
    /**
     * Id сущности
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Название скидочной матрицы
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
     * Кэширована ли матрица
     * Иными словами: Подсчитаны ли в этой матрице результаты для microcategory_id и location_id заранее
     */
    @Column(name="is_cached", columnDefinition = "boolean default false", nullable = false)
    private Boolean isCached;

    /**
     * Instantiates a new Discount baseline.
     *
     * @param name  the name
     * @param ready the ready
     */
    public DiscountBaseline(String name, Boolean ready) {
        this.name = name;
        this.ready = ready;
    }
}
