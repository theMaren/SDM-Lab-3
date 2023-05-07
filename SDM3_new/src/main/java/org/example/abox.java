package org.example;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.VCARD;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class abox {
    public static void main(String[] args) {

        HashMap<String, String> classes = new HashMap<String, String>();
        HashMap<String, String> properties = new HashMap<String, String>();

        String tboxPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\TBOXv4.owl";

        // Read the TBox file
        OntModel tbox = ModelFactory.createOntologyModel();
        tbox.read(tboxPath);

        // Create the ABox file
        OntModel abox = ModelFactory.createOntologyModel();
        String baseUri = "http://www.gra.fo/schema/untitled-ekg#";

        // Iterate over the classes defined in the TBox and put the URIs to hashmap
        for (OntClass ontClass : tbox.listClasses().toList()) {
            classes.put(ontClass.getLabel(null), ontClass.getURI());
            //OntClass aboxClass = abox.createClass(baseUri + ontClass.getLocalName());
        }

        // Iterate over the object properties defined in the TBox and put URIs to hashmap
        for (OntProperty ontProperty : tbox.listObjectProperties().toList()) {
            //OntProperty aboxProperty = abox.createObjectProperty(baseUri + ontProperty.getLocalName());
            properties.put(ontProperty.getLabel(null), ontProperty.getURI());
        }

        try{

            //read the short papers journals csv file
            CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter('\t').withFirstRecordAsHeader();
            String csvPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\shortpapers_journals_Lab3.csv";

            CSVParser csvParser_spj = new CSVParser(new FileReader(csvPath), csvFormat);
            List<String[]> rows_spj = new ArrayList<String[]>();

            // put the short papers journals csv rows to list
            for (CSVRecord record : csvParser_spj) {
                String[] row = new String[record.size()];
                for (int i = 0; i < row.length; i++) {
                    row[i] = record.get(i);
                }
                rows_spj.add(row);
            }

            csvParser_spj.close();


            //read the full papers journals
            csvPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\fullpapers_journals_Lab3.csv";
            csvFormat = CSVFormat.DEFAULT.withDelimiter(',').withFirstRecordAsHeader();

            CSVParser csvParser_fpj = new CSVParser(new FileReader(csvPath), csvFormat);
            List<String[]> rows_fpj = new ArrayList<String[]>();

            for (CSVRecord record : csvParser_fpj) {
                String[] row = new String[record.size()];
                for (int i = 0; i < row.length; i++) {
                    row[i] = record.get(i);
                }
                rows_fpj.add(row);
            }

            csvParser_fpj.close();

            //read the demopapers
            csvPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\demopaper_journals_Lab3.csv";

            CSVParser csvParser_dpj = new CSVParser(new FileReader(csvPath), csvFormat);
            List<String[]> rows_dpj = new ArrayList<String[]>();

            for (CSVRecord record : csvParser_dpj) {
                String[] row = new String[record.size()];
                for (int i = 0; i < row.length; i++) {
                    row[i] = record.get(i);
                }
                rows_dpj.add(row);
            }

            csvParser_dpj.close();

            //read the reviews
            csvPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\reviews_journals_Lab3.csv";

            CSVParser csvParser_rj = new CSVParser(new FileReader(csvPath), csvFormat);
            List<String[]> rows_rj = new ArrayList<String[]>();

            for (CSVRecord record : csvParser_rj) {
                String[] row = new String[record.size()];
                for (int i = 0; i < row.length; i++) {
                    row[i] = record.get(i);
                }
                rows_rj.add(row);
            }

            csvParser_rj.close();


            // loop through the short papers journals csv
            for (String[] row : rows_spj) {
                // create the classes
                Individual author = abox.createIndividual(classes.get("Author") + "/" + row[4], tbox.getOntClass(classes.get("Author")));
                Individual shortPaper = abox.createIndividual(classes.get("ShortPaper") + "/" + row[1], tbox.getOntClass(classes.get("ShortPaper")));
                Individual handler = abox.createIndividual(classes.get("Handler") + "/" + row[10], tbox.getOntClass(classes.get("Handler")));
                Individual journal = abox.createIndividual(classes.get("Journal") + "/" + row[8], tbox.getOntClass(classes.get("Journal")));

                //create the properties
                author.addProperty(abox.createProperty(properties.get("named")), row[5]);

                shortPaper.addProperty(abox.createProperty(properties.get("summarizedBy")), row[0]);
                shortPaper.addProperty(abox.createProperty(properties.get("writes")),author);
                shortPaper.addProperty(abox.createProperty(properties.get("hasWords")),row[3]);
                shortPaper.addProperty(abox.createProperty(properties.get("appliesTo")),journal);

                journal.addProperty(abox.createProperty(properties.get("hasVolume")), row[9]);
                journal.addProperty(abox.createProperty(properties.get("handledBy")), handler);
                handler.addProperty(abox.createProperty(properties.get("since")), row[11]);


            }

            // loop through the full papers journals csv
            for (String[] row : rows_fpj) {
                // create the classes
                Individual author = abox.createIndividual(classes.get("Author") + "/" + row[4], tbox.getOntClass(classes.get("Author")));
                Individual fullPaper = abox.createIndividual(classes.get("FullPaper") + "/" + row[1], tbox.getOntClass(classes.get("FullPaper")));
                Individual handler = abox.createIndividual(classes.get("Handler") + "/" + row[10], tbox.getOntClass(classes.get("Handler")));
                Individual journal = abox.createIndividual(classes.get("Journal") + "/" + row[8], tbox.getOntClass(classes.get("Journal")));

                //create the properties
                author.addProperty(abox.createProperty(properties.get("named")), row[5]);

                fullPaper.addProperty(abox.createProperty(properties.get("indexedBy")), row[0]);
                fullPaper.addProperty(abox.createProperty(properties.get("writes")),author);
                fullPaper.addProperty(abox.createProperty(properties.get("hasWords")),row[3]);
                fullPaper.addProperty(abox.createProperty(properties.get("appliesTo")),journal);

                journal.addProperty(abox.createProperty(properties.get("hasVolume")), row[9]);
                journal.addProperty(abox.createProperty(properties.get("handledBy")), handler);
                handler.addProperty(abox.createProperty(properties.get("since")), row[11]);
            }

            // loop through the demopapers
            for (String[] row : rows_dpj) {
                // create the classes
                Individual author = abox.createIndividual(classes.get("Author") + "/" + row[4], tbox.getOntClass(classes.get("Author")));
                Individual demoPaper = abox.createIndividual(classes.get("DemoPaper") + "/" + row[1], tbox.getOntClass(classes.get("DemoPaper")));
                Individual handler = abox.createIndividual(classes.get("Handler") + "/" + row[10], tbox.getOntClass(classes.get("Handler")));
                Individual journal = abox.createIndividual(classes.get("Journal") + "/" + row[8], tbox.getOntClass(classes.get("Journal")));

                //create the properties
                author.addProperty(abox.createProperty(properties.get("named")), row[5]);

                demoPaper.addProperty(abox.createProperty(properties.get("hasLink")), row[0]);
                demoPaper.addProperty(abox.createProperty(properties.get("writes")),author);
                demoPaper.addProperty(abox.createProperty(properties.get("hasWords")),row[3]);
                demoPaper.addProperty(abox.createProperty(properties.get("appliesTo")),journal);

                journal.addProperty(abox.createProperty(properties.get("hasVolume")), row[9]);
                journal.addProperty(abox.createProperty(properties.get("handledBy")), handler);
                handler.addProperty(abox.createProperty(properties.get("since")), row[11]);
            }

            //loop through the reviews
            for (String[] row : rows_dpj) {
                // create the classes
                Individual review = abox.createIndividual(classes.get("Review") + "/" + row[0], tbox.getOntClass(classes.get("Review")));
                Individual article = abox.createIndividual(classes.get("Article") + "/" + row[3], tbox.getOntClass(classes.get("Article")));
                Individual handler = abox.createIndividual(classes.get("Handler") + "/" + row[4], tbox.getOntClass(classes.get("Handler")));
                Individual reviewer = abox.createIndividual(classes.get("Reviewer") + "/" + row[5], tbox.getOntClass(classes.get("Reviewer")));

                //create the properties
                review.addProperty(abox.createProperty(properties.get("explainedWith")), row[2]);
                review.addProperty(abox.createProperty(properties.get("states")), row[1]);
                review.addProperty(abox.createProperty(properties.get("requestedBy")), handler);
                review.addProperty(abox.createProperty(properties.get("reviews")), article);

                reviewer.addProperty(abox.createProperty(properties.get("writesReview")), review);
                reviewer.addProperty(abox.createProperty(properties.get("hasIndexH")), row[6]);

            }

            //output the abox to file
            FileOutputStream out = new FileOutputStream("abox.rdf");
            abox.write(out,"RDF/XML-ABBREV");
            out.close();

        } catch(Exception e) {
            System.out.println("I got an exception");
            System.out.println(e);
        }

    }

}
