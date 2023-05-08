package org.example;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.FileOutputStream;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class createAbox {
    public static OntModel abox = ModelFactory.createOntologyModel();
    public static List<String[]> read_csv(String filepath, char delimiter){
        List<String[]> rows = new ArrayList<String[]>();
        try {
            CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(delimiter).withFirstRecordAsHeader();
            CSVParser csvParser = new CSVParser(new FileReader(filepath), csvFormat);

            for (CSVRecord record : csvParser) {
                String[] row = new String[record.size()];
                for (int i = 0; i < row.length; i++) {
                    row[i] = record.get(i);
                }
                rows.add(row);
            }

            csvParser.close();

        } catch(Exception e){
            System.out.println("I got an exception");
            System.out.println(e);
        }

        return rows;

    }

    public static void paper_to_abox(List<String[]> rows,HashMap<String, String> classes,
                              HashMap<String, String> properties, OntModel tbox ,
                              String paper_type){

        String paper_uri = "";
        String property_name = "";
        // case to distinguish between the paper subclasses
        switch (paper_type){
            case "short":
                paper_uri = classes.get("ShortPaper");
                property_name = "ShortAbstract";
                break;
            case "full":
                paper_uri = classes.get("FullPaper");
                property_name = "NumOfChapters";
                break;
            case "demo":
                paper_uri = classes.get("DemoPaper");
                property_name = "LinkID";
                break;
        }

        //loop through the csv data
        for (String[] row : rows) {

            // create the classes
            Individual paper = abox.createIndividual(paper_uri + "/" + row[1], tbox.getOntClass(paper_uri));
            Individual author = abox.createIndividual(classes.get("Author") + "/" + row[4], tbox.getOntClass(classes.get("Author")));
            Individual handler = abox.createIndividual(classes.get("Handler") + "/" + row[10], tbox.getOntClass(classes.get("Handler")));
            Individual journal = abox.createIndividual(classes.get("Journal") + "/" + row[8], tbox.getOntClass(classes.get("Journal")));

            //create the properties
            author.addProperty(abox.createProperty(properties.get("Name")), row[5]);
            paper.addProperty(abox.createProperty(properties.get(property_name)), row[0]);
            paper.addProperty(abox.createProperty(properties.get("writtenBy")),author);
            paper.addProperty(abox.createProperty(properties.get("Title")),row[3]);
            paper.addProperty(abox.createProperty(properties.get("appliesTo")),journal);

            journal.addProperty(abox.createProperty(properties.get("Volume")), row[9]);
            journal.addProperty(abox.createProperty(properties.get("handledBy")), handler);
            handler.addProperty(abox.createProperty(properties.get("StartDate")), row[11]);
        }

    }

    public static void reviews_to_abox(List<String[]> rows,HashMap<String, String> classes,
                                HashMap<String, String> properties, OntModel tbox){

        for (String[] row : rows) {
            // create the classes
            Individual review = abox.createIndividual(classes.get("Review") + "/" + row[0], tbox.getOntClass(classes.get("Review")));
            Individual article = abox.createIndividual(classes.get("Article") + "/" + row[3], tbox.getOntClass(classes.get("Article")));
            Individual handler = abox.createIndividual(classes.get("Handler") + "/" + row[4], tbox.getOntClass(classes.get("Handler")));
            Individual reviewer = abox.createIndividual(classes.get("Reviewer") + "/" + row[5], tbox.getOntClass(classes.get("Reviewer")));

            //create the properties
            review.addProperty(abox.createProperty(properties.get("Explanation")), row[2]);
            review.addProperty(abox.createProperty(properties.get("Decision")), row[1]);
            review.addProperty(abox.createProperty(properties.get("requestedBy")), handler);
            review.addProperty(abox.createProperty(properties.get("reviews")), article);

            reviewer.addProperty(abox.createProperty(properties.get("writesReview")), review);
            reviewer.addProperty(abox.createProperty(properties.get("HIndex")), row[6]);

        }

    }

    public static void conferences_to_abox(List<String[]> rows,HashMap<String, String> classes,
                                           HashMap<String, String> properties, OntModel tbox,
                                           String conference_type){

        //check the paper type
        String conference_uri = "";
        String c_property_name = "";

        // case to distinguish between the conference types
        switch (conference_type){
            case "workshop":
                conference_uri = classes.get("Workshop");
                c_property_name = "NumberOfDays";
                break;
            case "symposium":
                conference_uri = classes.get("Symposium");
                c_property_name = "Subject";
                break;
            case "regular":
                conference_uri = classes.get("RegularConference");
                c_property_name = "Address";
                break;
            case "expert":
                conference_uri = classes.get("ExpertGroup");
                c_property_name = "NumExperts";
                break;
        }

        // check the conference part
        for (String[] row : rows) {

            // create instances
            Individual conference = abox.createIndividual(conference_uri + "/" + row[13], tbox.getOntClass(conference_uri));
            Individual author = abox.createIndividual(classes.get("Author") + "/" + row[10], tbox.getOntClass(classes.get("Author")));
            Individual handler = abox.createIndividual(classes.get("Handler") + "/" + row[18], tbox.getOntClass(classes.get("Handler")));

            //add properties
            author.addProperty(abox.createProperty(properties.get("Name")), row[11]);
            handler.addProperty(abox.createProperty(properties.get("StartDate")), row[19]);
            conference.addProperty(abox.createProperty(properties.get(c_property_name)), row[12]);
            conference.addProperty(abox.createProperty(properties.get("VenueName")), row[15]);
            conference.addProperty(abox.createProperty(properties.get("Proceedings")), row[17]);

            //check for paper type
            Individual paper = null;
            if(!row[3].isEmpty()){
                //demo
                paper = abox.createIndividual(classes.get("DemoPaper") + "/" + row[3], tbox.getOntClass(classes.get("DemoPaper")));
                paper.addProperty(abox.createProperty(properties.get("LinkID")), row[2]);
            }else if(!row[5].isEmpty()){
                //full
                paper = abox.createIndividual(classes.get("FullPaper") + "/" + row[5], tbox.getOntClass(classes.get("FullPaper")));
                paper.addProperty(abox.createProperty(properties.get("NumOfChapters")), row[4]);
            }else if(!row[7].isEmpty()) {
                //short
                paper = abox.createIndividual(classes.get("ShortPaper") + "/" + row[7], tbox.getOntClass(classes.get("ShortPaper")));
                paper.addProperty(abox.createProperty(properties.get("ShortAbstract")), row[6]);
            } else {
                paper = abox.createIndividual(classes.get("Article") + "/" + row[8], tbox.getOntClass(classes.get("Article")));
            }

            paper.addProperty(abox.createProperty(properties.get("Title")), row[9]);

            //add relations
            paper.addProperty(abox.createProperty(properties.get("appliesTo")), conference);
            conference.addProperty(abox.createProperty(properties.get("handledBy")), handler);
            paper.addProperty(abox.createProperty(properties.get("writtenBy")), author);

            //check if poster
            if(row[1] != ""){
                Individual poster = abox.createIndividual(classes.get("Poster") + "/" + row[1], tbox.getOntClass(classes.get("Poster")));
                poster.addProperty(abox.createProperty(properties.get("Size")), row[0]);
                poster.addProperty(abox.createProperty(properties.get("madeFor")), conference);

            }

        }
    }

    public static void areas_to_abox(List<String[]> rows,HashMap<String, String> classes,
                                           HashMap<String, String> properties, OntModel tbox){

        for (String[] row : rows) {
            // create the classes
            Individual area = abox.createIndividual(classes.get("Area") + "/" + row[1], tbox.getOntClass(classes.get("Area")));
            Individual article = abox.createIndividual(classes.get("Article") + "/" + row[0], tbox.getOntClass(classes.get("Article")));

            //properties
            area.addProperty(abox.createProperty(properties.get("AreaName")), row[2]);
            article.addProperty(abox.createProperty(properties.get("relatedTo")), area);
        }

    }

    public static void main(String[] args) {

        HashMap<String, String> classes = new HashMap<String, String>();
        HashMap<String, String> properties = new HashMap<String, String>();
        List<String[]> csv_rows = new ArrayList<String[]>();

        //file pathes
        String shortPaperPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\shortpapers_journals_Lab3.csv";
        String fullPaperPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\fullpapers_journals_Lab3.csv";
        String demoPaperPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\demopaper_journals_Lab3.csv";
        String reviewPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\reviews_journals_Lab3.csv";
        String workshopPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\workshops.csv";
        String symposiumPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\symposiums.csv";
        String regconfPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\regularconferences.csv";
        String expergroupPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\expertsgroups.csv";
        String areaPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\areas.csv";

        String tboxPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\TBOXv5.owl";

        // Read the TBox file
        OntModel tbox = ModelFactory.createOntologyModel();
        tbox.read(tboxPath);

        // Iterate over the classes defined in the TBox and put the URIs to hashmap
        for (OntClass ontClass : tbox.listClasses().toList()) {
            classes.put(ontClass.getLabel(null), ontClass.getURI());
        }

        // Iterate over the object properties defined in the TBox and put URIs to hashmap
        for (OntProperty ontProperty : tbox.listObjectProperties().toList()) {
            properties.put(ontProperty.getLabel(null), ontProperty.getURI());
        }
        // add the data properties to the list
        for (OntProperty ontProperty : tbox.listDatatypeProperties().toList()) {
            properties.put(ontProperty.getLabel(null), ontProperty.getURI());
        }


        //short Papers
        csv_rows = read_csv(shortPaperPath,'\t');
        paper_to_abox(csv_rows,classes,properties,tbox,"short");
        //full Papers
        csv_rows = read_csv(fullPaperPath,',');
        paper_to_abox(csv_rows,classes,properties,tbox,"full");
        //demo Papers
        csv_rows = read_csv(demoPaperPath,',');
        paper_to_abox(csv_rows,classes,properties,tbox,"demo");
        //reviews
        csv_rows = read_csv(reviewPath,',');
        reviews_to_abox(csv_rows,classes,properties,tbox);
        //area
        csv_rows = read_csv(areaPath,',');
        areas_to_abox(csv_rows,classes,properties,tbox);

        //conferences
        //worskhop
        csv_rows = read_csv(workshopPath,',');
        conferences_to_abox(csv_rows,classes,properties,tbox,"workshop");
        //symposium
        csv_rows = read_csv(symposiumPath,',');
        conferences_to_abox(csv_rows,classes,properties,tbox,"symposium");
        //expert group
        csv_rows = read_csv(expergroupPath,',');
        conferences_to_abox(csv_rows,classes,properties,tbox,"expert");
        //regular conference
        csv_rows = read_csv(regconfPath,',');
        conferences_to_abox(csv_rows,classes,properties,tbox,"regular");

        //save abox to file
        try{
            FileOutputStream out = new FileOutputStream("abox.rdf");
            abox.write(out,"RDF/XML-ABBREV");
            out.close();

        }catch(Exception e){
            System.out.println("I got an exception");
            System.out.println(e);
        }
    }
}
