package models;

import com.avaje.ebean.Model;

import javax.persistence.*;

@Entity(name = "organization")
@Table(name = "organization")
public class Organization extends Model {
    @Id
    @Column(name = "organization_id")
    private Integer id;

    @Column(name = "short_name")
    private String shortName;

    @ManyToOne
    @JoinColumn(name = "parental_organization_id")
    private Organization parentOrganization;

    public static Model.Finder<Integer, Organization> find = new Model.Finder<>(Organization.class);

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Organization getParentOrganization() {
        return parentOrganization;
    }

    public void setParentOrganization(Organization parentOrganization) {
        this.parentOrganization = parentOrganization;
    }
}
