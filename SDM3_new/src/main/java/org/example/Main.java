package org.example;
import java.util.List;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.VCARD;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Main {

    public static void main(String[] args) {

        String csvPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\shortpapers_journals_Lab3.csv";
        String tboxPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\TBOXv5.owl";

        //read the tbox
        OntModel tbox = ModelFactory.createOntologyModel();
        tbox.read(tboxPath);

        try {
            //Model model = ModelFactory.createDefaultModel();
            //read the csv file
            CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter('\t').withFirstRecordAsHeader();
            CSVParser csvParser = new CSVParser(new FileReader(csvPath), csvFormat);
            List<String[]> rows = new ArrayList<String[]>();

            // read csv to list
            for (CSVRecord record : csvParser) {
                String[] row = new String[record.size()];
                for (int i = 0; i < row.length; i++) {
                    row[i] = record.get(i);
                }
                rows.add(row);
            }

            //create an abox
            OntModel abox = ModelFactory.createOntologyModel();
            String baseUri = "http://www.gra.fo/schema/untitled-ekg#";

            // Read class types from the TBox file
            //OntClass articleClass = tbox.getOntClass("http://www.gra.fo/schema/untitled-ekg#Article");
            OntClass authorClass = tbox.getOntClass("http://www.gra.fo/schema/untitled-ekg#Author");
            OntClass shortPaperClass = tbox.getOntClass("http://www.gra.fo/schema/untitled-ekg#Shortpaper");
            OntClass journalClass = tbox.getOntClass("http://www.gra.fo/schema/untitled-ekg#Journal");
            OntClass handlerClass = tbox.getOntClass("http://www.gra.fo/schema/untitled-ekg#Handler");
            // Read object properties from the TBox file

            DatatypeProperty hasWordsProperty = tbox.getDatatypeProperty("http://www.gra.fo/schema/untitled-ekg#title");
            DatatypeProperty summarizedByProperty = tbox.getDatatypeProperty("http://www.gra.fo/schema/untitled-ekg#shortabstract");
            DatatypeProperty namedProperty = tbox.getDatatypeProperty("http://www.gra.fo/schema/untitled-ekg#name");
            DatatypeProperty sinceProperty = tbox.getDatatypeProperty("http://www.gra.fo/schema/untitled-ekg#startdate");
            DatatypeProperty hasvolumeProperty = tbox.getDatatypeProperty("http://www.gra.fo/schema/untitled-ekg#volume");

            ObjectProperty writesProperty = tbox.getObjectProperty("http://www.gra.fo/schema/untitled-ekg#writtenby");
            ObjectProperty appliesToProperty = tbox.getObjectProperty("http://www.gra.fo/schema/untitled-ekg#appliesto");
            ObjectProperty handledByProperty = tbox.getObjectProperty("http://www.gra.fo/schema/untitled-ekg#handledby");

            for (String[] row : rows) {

                String authorUri = baseUri + "Author/" + row[4];
                String shortPaperUri = baseUri + "Shortpaper/" + row[1];
                String handlerUri = baseUri + "Handler/" + row[10];
                String journalUri = baseUri + "Journal/" + row[8];

                Individual author = abox.createIndividual(authorUri, authorClass);
                Individual shortPaper = abox.createIndividual(shortPaperUri, shortPaperClass);
                Individual handler = abox.createIndividual(handlerUri, handlerClass);
                Individual journal = abox.createIndividual(journalUri, journalClass);

                //String propertyUri = hasWordsProperty.getURI(); // Use the hasWords object property read from TBox
                //individual.addProperty(abox.createProperty(propertyUri), row[3]);
                author.addProperty(abox.createProperty(namedProperty.getURI()), row[5]);

                shortPaper.addProperty(abox.createProperty(summarizedByProperty.getURI()), row[0]);
                shortPaper.addProperty(abox.createProperty(writesProperty.getURI()),author);
                shortPaper.addProperty(abox.createProperty(hasWordsProperty.getURI()),row[3]);
                shortPaper.addProperty(abox.createProperty(appliesToProperty.getURI()),journal);

                journal.addProperty(abox.createProperty(hasvolumeProperty.getURI()), row[9]);
                journal.addProperty(abox.createProperty(handledByProperty.getURI()), handler);
                handler.addProperty(abox.createProperty(sinceProperty.getURI()), row[11]);



            }

            //Closetheparser
            csvParser.close();
            //writethemodeltoanRDFfile
            FileOutputStream out = new FileOutputStream("abox.rdf");
            abox.write(out,"RDF/XML-ABBREV");
            out.close();

        } catch(Exception e){

            System.out.println("I got an exception");
            System.out.println(e);
        }
    }
}