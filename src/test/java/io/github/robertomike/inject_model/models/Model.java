package io.github.robertomike.inject_model.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Model {
    @Id
    @Column()
    private Long id;

    public Model(Long id) {
        this.id = id;
    }

    public Model() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
