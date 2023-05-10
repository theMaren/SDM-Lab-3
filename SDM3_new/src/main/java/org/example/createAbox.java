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

    //read csv file and put data to array list
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

    //put journals and all the articles related to them to the abox
    public static void journal_to_abox(List<String[]> rows,HashMap<String, String> classes,
                              HashMap<String, String> properties, OntModel tbox ,
                              String paper_type){

        String paper_uri = "";
        String property_name = "";
        // case to distinguish between the article subclasses and their different properties
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
            default:
                paper_uri = classes.get("Article");
                break;
        }

        //loop through the csv data
        for (String[] row : rows) {

            // create instances
            Individual paper = abox.createIndividual(paper_uri + "/" + row[1], tbox.getOntClass(paper_uri));
            Individual author = abox.createIndividual(classes.get("Author") + "/" + row[4], tbox.getOntClass(classes.get("Author")));
            Individual handler = abox.createIndividual(classes.get("Handler") + "/" + row[10], tbox.getOntClass(classes.get("Handler")));
            Individual journal = abox.createIndividual(classes.get("Journal") + "/" + row[8], tbox.getOntClass(classes.get("Journal")));

            //add the data properties
            if (property_name != "") {
                paper.addProperty(abox.createProperty(properties.get(property_name)), row[0]);
            }
            author.addProperty(abox.createProperty(properties.get("Name")), row[5]);
            paper.addProperty(abox.createProperty(properties.get("Title")),row[3]);
            journal.addProperty(abox.createProperty(properties.get("Volume")), row[9]);
            handler.addProperty(abox.createProperty(properties.get("StartDate")), row[11]);

            //add the object properties
            paper.addProperty(abox.createProperty(properties.get("writtenBy")),author);
            paper.addProperty(abox.createProperty(properties.get("appliesTo")),journal);
            journal.addProperty(abox.createProperty(properties.get("handledBy")), handler);
        }

    }

    //put the reviews and their reviewers and related articles to the abox
    public static void reviews_to_abox(List<String[]> rows,HashMap<String, String> classes,
                                HashMap<String, String> properties, OntModel tbox){

        String paper_uri = "";

        for (String[] row : rows) {

            //check if reviewed paper is an article subclass
            if (row[3].toLowerCase().contains("short")) {
                paper_uri = classes.get("ShortPaper");
            } else if (row[3].toLowerCase().contains("demo")) {
                paper_uri = classes.get("DemoPaper");
            } else if (row[3].toLowerCase().contains("full")) {
                paper_uri = classes.get("FullPaper");
            } else if (row[3].toLowerCase().contains("poster")) {
                paper_uri = classes.get("Poster");
            }
            else {
                paper_uri = classes.get("Article");
            }

            // create instances
            Individual paper = abox.createIndividual(paper_uri + "/" + row[0], tbox.getOntClass(paper_uri));
            Individual review = abox.createIndividual(classes.get("Review") + "/" + row[0], tbox.getOntClass(classes.get("Review")));
            Individual handler = abox.createIndividual(classes.get("Handler") + "/" + row[4], tbox.getOntClass(classes.get("Handler")));
            Individual reviewer = abox.createIndividual(classes.get("Reviewer") + "/" + row[5], tbox.getOntClass(classes.get("Reviewer")));

            //add the data properties
            review.addProperty(abox.createProperty(properties.get("Explanation")), row[2]);
            review.addProperty(abox.createProperty(properties.get("Decision")), row[1]);
            reviewer.addProperty(abox.createProperty(properties.get("HIndex")), row[6]);

            // add the object properties
            reviewer.addProperty(abox.createProperty(properties.get("writesReview")), review);
            review.addProperty(abox.createProperty(properties.get("requestedBy")), handler);
            review.addProperty(abox.createProperty(properties.get("reviews")), paper);

        }

    }

    // put the conferences and their related papers to the abox
    public static void conferences_to_abox(List<String[]> rows,HashMap<String, String> classes,
                                           HashMap<String, String> properties, OntModel tbox,
                                           String conference_type){

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
            default:
                conference_uri = classes.get("Conference");
                break;
        }


        for (String[] row : rows) {

            // create instances
            Individual conference = abox.createIndividual(conference_uri + "/" + row[13], tbox.getOntClass(conference_uri));
            Individual author = abox.createIndividual(classes.get("Author") + "/" + row[10], tbox.getOntClass(classes.get("Author")));
            Individual handler = abox.createIndividual(classes.get("Handler") + "/" + row[18], tbox.getOntClass(classes.get("Handler")));

            //add data properties
            if (c_property_name != "") {
                conference.addProperty(abox.createProperty(properties.get(c_property_name)), row[12]);
            }
            author.addProperty(abox.createProperty(properties.get("Name")), row[11]);
            handler.addProperty(abox.createProperty(properties.get("StartDate")), row[19]);
            conference.addProperty(abox.createProperty(properties.get("VenueName")), row[15]);
            conference.addProperty(abox.createProperty(properties.get("Proceedings")), row[17]);

            //check the article type and create instance
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

            //add title property
            paper.addProperty(abox.createProperty(properties.get("Title")), row[9]);

            //add object properties
            paper.addProperty(abox.createProperty(properties.get("appliesTo")), conference);
            conference.addProperty(abox.createProperty(properties.get("handledBy")), handler);
            paper.addProperty(abox.createProperty(properties.get("writtenBy")), author);

            //check if poster related to the conference exists
            if(row[1] != ""){
                Individual poster = abox.createIndividual(classes.get("Poster") + "/" + row[1], tbox.getOntClass(classes.get("Poster")));
                //add poster object and data property
                poster.addProperty(abox.createProperty(properties.get("Size")), row[0]);
                poster.addProperty(abox.createProperty(properties.get("madeFor")), conference);

            }

        }
    }

    //put the areas to the tbox
    public static void areas_to_abox(List<String[]> rows,HashMap<String, String> classes,
                                           HashMap<String, String> properties, OntModel tbox){

        //check the article type
        String paper_uri = "";
        for (String[] row : rows) {

            if (row[0].toLowerCase().contains("short")) {
                paper_uri = classes.get("ShortPaper");
            } else if (row[0].toLowerCase().contains("demo")) {
                paper_uri = classes.get("DemoPaper");
            } else if (row[0].toLowerCase().contains("full")) {
                paper_uri = classes.get("FullPaper");
            } else if (row[0].toLowerCase().contains("poster")){
                paper_uri = classes.get("Poster");
            }
            else{
                paper_uri = classes.get("Article");
            }

            // create the instances
            Individual paper = abox.createIndividual(paper_uri + "/" + row[0], tbox.getOntClass(paper_uri));
            Individual area = abox.createIndividual(classes.get("Area") + "/" + row[2], tbox.getOntClass(classes.get("Area")));

            //add data and object property
            area.addProperty(abox.createProperty(properties.get("AreaName")), row[3]);
            paper.addProperty(abox.createProperty(properties.get("relatedTo")), area);
        }

    }

    public static void main(String[] args) {

        HashMap<String, String> classes = new HashMap<String, String>();
        HashMap<String, String> properties = new HashMap<String, String>();
        List<String[]> csv_rows = new ArrayList<String[]>();
        OntModel tbox = ModelFactory.createOntologyModel();

        //file paths to csv data and tbox
        String shortPaperPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\shortpapers_journals_Lab3.csv";
        String fullPaperPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\fullpapers_journals_Lab3.csv";
        String demoPaperPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\demopaper_journals_Lab3.csv";
        String reviewPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\reviews_Lab3.csv";
        String workshopPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\workshops.csv";
        String symposiumPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\symposiums.csv";
        String regconfPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\regularconferences.csv";
        String expergroupPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\expertsgroups.csv";
        String areaPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\data\\areas.csv";
        String tboxPath = "D:\\OneDrive - Université Libre de Bruxelles\\UPC\\Semantic Database Mangement\\Lab3\\TBOXv6.owl";

        // Read the TBox file
        tbox.read(tboxPath);

        // Iterate over the classes defined in the TBox and put the URIs to hashmap
        for (OntClass ontClass : tbox.listClasses().toList()) {
            classes.put(ontClass.getLabel(null), ontClass.getURI());
        }

        // Iterate over the object properties defined in the TBox and put URIs to hashmap
        for (OntProperty ontProperty : tbox.listObjectProperties().toList()) {
            properties.put(ontProperty.getLabel(null), ontProperty.getURI());
        }
        // add the data properties to the properties hashmap
        for (OntProperty ontProperty : tbox.listDatatypeProperties().toList()) {
            properties.put(ontProperty.getLabel(null), ontProperty.getURI());
        }


        //short papers to abox
        csv_rows = read_csv(shortPaperPath,'\t');
        journal_to_abox(csv_rows,classes,properties,tbox,"short");
        //full papers to abox
        csv_rows = read_csv(fullPaperPath,',');
        journal_to_abox(csv_rows,classes,properties,tbox,"full");
        //demo papers to abox
        csv_rows = read_csv(demoPaperPath,',');
        journal_to_abox(csv_rows,classes,properties,tbox,"demo");
        //reviews to abox
        csv_rows = read_csv(reviewPath,',');
        reviews_to_abox(csv_rows,classes,properties,tbox);
        //areas to abox
        csv_rows = read_csv(areaPath,',');
        areas_to_abox(csv_rows,classes,properties,tbox);

        //conferences to abox
        //workshops
        csv_rows = read_csv(workshopPath,',');
        conferences_to_abox(csv_rows,classes,properties,tbox,"workshop");
        //symposiums
        csv_rows = read_csv(symposiumPath,',');
        conferences_to_abox(csv_rows,classes,properties,tbox,"symposium");
        //expert groups
        csv_rows = read_csv(expergroupPath,',');
        conferences_to_abox(csv_rows,classes,properties,tbox,"expert");
        //regular conferences
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
