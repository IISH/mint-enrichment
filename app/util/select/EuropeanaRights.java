package util.select;

import play.libs.Scala;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Josh on 5/2/16.
 */
public enum EuropeanaRights {
    OOCNC("http://www.europeana.eu/rights/out-of-copyright-non-commercial/"),
    CC0("http://creativecommons.org/publicdomain/zero/1.0/"),
    BY("http://creativecommons.org/licenses/by/4.0/"),
    BYSA("http://creativecommons.org/licenses/by-sa/4.0/"),
    BYND("http://creativecommons.org/licenses/by-nd/4.0/"),
    BYNC("http://creativecommons.org/licenses/by-nc/4.0/"),
    BYNCSA("http://creativecommons.org/licenses/by-nc-sa/4.0/"),
    BYNCND("http://creativecommons.org/licenses/by-nc-nd/4.0/"),
    RRFA("http://www.europeana.eu/rights/rr-f/"),
    RRPA("http://www.europeana.eu/rights/rr-p/"),
    OW("http://www.europeana.eu/rights/orphan-work-eu/"),
    UNKNOWN("http://www.europeana.eu/rights/unknown/");

    private final String key;

    private EuropeanaRights(String key) {
        this.key = key;
    }

    public static List<Tuple2<String, String>> options() {
        List<Tuple2<String, String>> options = new ArrayList<>();
        for (EuropeanaRights et : EuropeanaRights.values()) {
            options.add(Scala.Tuple(et.key, et.key));
        }
        return options;
    }

}
