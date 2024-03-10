package org.bigbrainmm.avitopricesapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "current_baseline_matrix")
public class CurrentBaselineMatrix {
    @Id
    private Long id;
    @Column(name = "name", unique = true)
    private String name;
}
