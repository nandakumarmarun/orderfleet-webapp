package com.orderfleet.webapp.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tbl_attributes")
public class Attributes implements Serializable,Cloneable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "seq_attributes_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "seq_attributes_id") })
    @GeneratedValue(generator = "seq_attributes_id_GEN")
    @Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_attributes_id')")
    private Long id;

    @NotNull
    @Column(name = "pid", unique = true, nullable = false, updatable = false)
    private String pid;

    @Column(name = "questions")
    private String questions;

    @Column(name = "type")
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attributes attributes = (Attributes) o;
        if (attributes.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, attributes.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Attributes{" +
                "id=" + id +
                ", pid='" + pid + '\'' +
                ", questions='" + questions + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
