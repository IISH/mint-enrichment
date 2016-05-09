package util.select;

import scala.Tuple2;

import java.util.List;

/**
 * Created by Josh on 5/2/16.
 */
public class Countries {

    static String repository = "http://mint-sparql.socialhistoryportal.org/HopeLanguagesAndCountries/sparql";
    static String conceptScheme = "http://mint.image.ece.ntua.gr/Vocabularies/Countries/CountriesThesaurus";

    public static List<Tuple2<String, String>> options() {
        SKOSValues skosValues = new SKOSValues(repository, conceptScheme);
        List <Tuple2<String, String>> options = skosValues.getOptions();
        return options;
    }

}
