package util.select;

import scala.Tuple2;
import util.rdf.SKOSThesaurus;

import java.util.List;

/**
 * Created by Josh on 5/2/16.
 */
public class HopeTags {

    static String repository = "http://mint-sparql.socialhistoryportal.org/HopeThemes/sparql";
    static String conceptScheme = "http://www.socialhistoryportal.org/themes#HopeThemes";

    public static List<Tuple2<String, String>> options() {
        SKOSValues skosValues = new SKOSValues(repository, conceptScheme);
        List <Tuple2<String, String>> options = skosValues.getOptions();
        return options;
    }

}
