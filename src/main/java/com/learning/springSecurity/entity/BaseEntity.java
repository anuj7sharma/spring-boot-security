package com.learning.springSecurity.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@EqualsAndHashCode
@NoArgsConstructor
@Data
@MappedSuperclass
public class BaseEntity {
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @PreUpdate
    public void onPreUpdate() {
        this.setUpdatedOn(LocalDateTime.now(ZoneOffset.UTC));
    }

    @PrePersist
    public void onPrePersist() {
        this.setCreatedOn(LocalDateTime.now(ZoneOffset.UTC));
    }
}
