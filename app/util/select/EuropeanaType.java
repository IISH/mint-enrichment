package util.select;

import play.libs.Scala;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Josh on 5/2/16.
 */
public enum EuropeanaType {
    TEXT("TEXT"),
    IMAGE("IMAGE"),
    VIDEO("VIDEO"),
    SOUND("SOUND"),
    THREED("3D");

    private final String key;

    private EuropeanaType(String key) {
        this.key = key;
    }

    public static List<Tuple2<String, String>> options() {
        List<Tuple2<String, String>> options = new ArrayList<>();
        for (EuropeanaType et : EuropeanaType.values()) {
            options.add(Scala.Tuple(et.key, et.key));
        }
        return options;
    }

}
