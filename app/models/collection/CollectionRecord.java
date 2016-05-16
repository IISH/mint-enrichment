package models.collection;

import play.data.validation.Constraints.Required;
import play.data.validation.Constraints.MinLength;
import play.data.validation.ValidationError;

import java.util.ArrayList;
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
    protected List<String> languages;
    @Required
    protected String type;
    @Required
    protected String description;

    protected List<String> hopeTags;
    protected List<String> subjects;
    protected List<String> coverages;
    protected List<String> spatialCoverages;

    protected List<String> contributors;
    protected List<String> creators;
    protected String date;
    protected String format;
    protected String publisher;
    protected String source;
    protected String alternative;
    protected String extent;
    protected String provenance;
    protected String medium;
    protected String tableOfContents;

    @Required
    protected String dataProvider;
    @Required
    protected String provider;
    @Required
    protected String rights;
    @Required
    protected String isShownAt;
    protected String isShownBy;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        /* At least one of the following is needed */
        if (hopeTags.size() > 0 &&
            subjects.size() > 0 &&
            coverages.size() > 0 &&
            spatialCoverages.size() > 0) {
            if (hopeTags.get(0).equals("") &&
                    subjects.get(0).equals("") &&
                    coverages.get(0).equals("") &&
                    spatialCoverages.get(0).equals("")) {
                errors.add(new ValidationError("hopeTags", "Hope tag, Subject, Coverage or Spatial Coverage is needed."));
            }
        }

        return errors.isEmpty() ? null : errors;
    }

    public CollectionRecord()
    {}

    public CollectionRecord(String uuid) {
        this.provider = "HOPE - Heritage of Peoples Europe";
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

    public List<String> getContributors() {
        return contributors;
    }

    public void setContributors(List<String> contributors) {
        this.contributors = contributors;
    }

    public List<String> getCreators() {
        return creators;
    }

    public void setCreators(List<String> creators) {
        this.creators = creators;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAlternative() {
        return alternative;
    }

    public void setAlternative(String alternative) {
        this.alternative = alternative;
    }

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getTableOfContents() {
        return tableOfContents;
    }

    public void setTableOfContents(String tableOfContents) {
        this.tableOfContents = tableOfContents;
    }

    public String getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(String dataProvider) {
        this.dataProvider = dataProvider;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getIsShownAt() {
        return isShownAt;
    }

    public void setIsShownAt(String isShownAt) {
        this.isShownAt = isShownAt;
    }

    public String getIsShownBy() {
        return isShownBy;
    }

    public void setIsShownBy(String isShownBy) {
        this.isShownBy = isShownBy;
    }
}
