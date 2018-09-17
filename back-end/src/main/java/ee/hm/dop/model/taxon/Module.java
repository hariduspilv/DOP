package ee.hm.dop.model.taxon;

import static javax.persistence.FetchType.EAGER;

import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;

@Entity
@DiscriminatorValue("MODULE")
public class Module extends Taxon {

    @OneToMany(fetch = EAGER, mappedBy = "module")
    @Where(clause = "used = 1")
    private Set<Topic> topics;

    @JsonIgnore
    @Column(nullable = false, insertable = false)
    private boolean used;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "specialization", nullable = false)
    private Specialization specialization;

    @JsonIgnore
    @Override
    public String getSolrLevel() {
        return "module";
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    @JsonIgnore
    @Override
    public Taxon getParent() {
        return getSpecialization();
    }

    @JsonIgnore
    @Override
    public Set<? extends Taxon> getChildren() {
        return getTopics();
    }

    @Override
    public boolean isUsed() {
        return used;
    }

    @Override
    public void setUsed(boolean used) {
        this.used = used;
    }
}
