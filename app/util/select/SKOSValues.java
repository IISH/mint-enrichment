package util.select;

import util.rdf.SKOSThesaurus;
import play.libs.Scala;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Josh on 5/2/16.
 */
public class SKOSValues {

    private String repository;
    private String conceptScheme;

    private final String[] collections = null;
    private final String[] graphs = null;
    private final String language = "en";
    private final boolean defaultIncluded = true;

    private final int limit = 0;
    private final int offset = 0;
    private final String like = null;

    public SKOSValues(String repository, String conceptScheme) {
        this.repository = repository;
        this.conceptScheme = conceptScheme;
    }

    public List<Tuple2<String, String>> getOptions() {
        List<Tuple2<String, String>> options = new ArrayList<>();
        String label = null;
        String concept = null;

        SKOSThesaurus skos = new SKOSThesaurus(this.repository,
                this.collections,
                this.graphs,
                this.language,
                this.conceptScheme,
                this.defaultIncluded);
        skos.setLike(this.like);
        skos.setLimit(this.limit);
        skos.setOffset(this.offset);

        List<Map<String, String>> concepts = skos.getConcepts();

        for (Map<String, String> map : concepts) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                if (key.equals("label")) {
                    label = entry.getValue();
                } else if (key.equals("concept")) {
                    concept = entry.getValue();
                }
            }
            options.add(Scala.Tuple(concept, label));
        }

        return options;
    }

}
