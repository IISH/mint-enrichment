package enrichment;

import org.w3c.dom.Document;

/**
 * Enricher to enrich XML content.
 */
interface Enricher {

    /**
     * Enriches the given XML document.
     *
     * @param xml The xml to be enriched.
     */
    void enrich(Document xml);
}
