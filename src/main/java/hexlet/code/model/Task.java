package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.Date;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Lob
    private String description;

    @NotNull
    @ManyToOne
    private TaskStatus taskStatus;

    @NotNull
    @ManyToOne
    private User author;

    @ManyToOne
    private User executor;

    @CreationTimestamp
    @Temporal(TIMESTAMP)
    private Date createdAt;

    @ManyToMany
    @JoinTable(name = "tasks_labels",
            joinColumns = @JoinColumn(name = "tasks_id"),
            inverseJoinColumns = @JoinColumn(name = "labels_id"))
    private Set<Label> labels;
}
