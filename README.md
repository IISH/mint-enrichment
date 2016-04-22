# mint-enrichment

## Workflow

1. User logs in. 
2. User can see the datasets existing in oai server belonging to his organisation. (Query to mongo db 'ialhi-mint', collection 'ialhi' group by 'datasetId')
3. User is able to browse xml records in a dataset. 
4. User is able to create collection description to a dataset.
5. The collection description is created via an HTML form which reflects the field from an EDM record.
6. After form submission an EDM xml will be created and validated against the EDM schema.
7. If the record is valid, it will be ingested to the mongodb. The only thing will change is the value of the namespace
prefix. We decided that we will use 'edm' as the metadataPrefix.
(NOTE: After some code inspection, we decided that collection description and enriched content records 
will be ingested to the same mongodb collection with a different namespace prefix. This way we can 
ensure, that the OAI server will pick up the records automatically and the MintPublisher will remove 
these records as well in case of unpublish. Without any extra programming need.)
8. Then a message will be sent to the message queue to start the item update procedure. It will contain the 
datasetId and the Id of the newly created collection record.
9. The listener will listen to the queue and if there is a request to update items, it iterates through 
the entire collection reads the xmlRecord field from the mongodb record and with the submitted ID adds 
a dcterms:isPartOf field to the edm:providedCHO section and creates a new mongodb record with the 
updated xmlRecord field and with namespace prefix edm.
