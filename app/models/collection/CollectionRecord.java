package models.collection;

import play.data.validation.Constraints.Required;
import play.data.validation.Constraints.MinLength;

import java.util.List;

/**
 * Created by Josh on 4/28/16.
 */
public class CollectionRecord {
    @Required
    protected String uuid;
    @Required
    protected String title;
    @Required
    protected String type;
    protected List<String> languages;
    @Required
    protected String description;
    @Required
    protected List<String> subjects;
    protected List<String> hopeTags;
    @Required
    protected List<String> coverages;
    @Required
    protected List<String> spatialCoverages;

    public CollectionRecord()
    {}

    public CollectionRecord(String uuid) {
        this.setUuid(uuid);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public List<String> getHopeTags() {
        return hopeTags;
    }

    public void setHopeTags(List<String> hopeTags) {
        this.hopeTags = hopeTags;
    }

    public List<String> getCoverages() {
        return coverages;
    }

    public void setCoverages(List<String> coverages) {
        this.coverages = coverages;
    }

    public List<String> getSpatialCoverages() {
        return spatialCoverages;
    }

    public void setSpatialCoverages(List<String> spatialCoverages) {
        this.spatialCoverages = spatialCoverages;
    }

    public List<String> getSubject() {
        return subjects;
    }

    public void setSubject(List<String> subjects) {
        this.subjects = subjects;
    }
}
