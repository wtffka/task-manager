package hexlet.code.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "task_statuses")
public class TaskStatus {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 1_000)
    @Column(unique = true)
    private String name;

    @CreationTimestamp
    @Temporal(TIMESTAMP)
    private Date createdAt;

    public TaskStatus(final Long id) {
        this.id = id;
    }
}
