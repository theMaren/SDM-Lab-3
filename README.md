# SDM-Lab-3 Knowledge Graphs

## Table of Contents
1. [General Info](#general-info)
2. [Set up and requirements](#Set-up-and-requirements)
3. [Data](#data)
4. [Create ABOX](#create-abox)

### General Info
***
This git repository is part of the lab assignment 3 (knowledge graphs) implemented for the course Semantic Data Managment at UPC barcelona. The java code creates an ABOX that can be importat together with a TBOX to GraphDB to create a knowledge Graph.

## Set up and requirements
***
We used Java Version 1.8.0_351 <br />
For the ABOX creation the Apache Jena API is used the java project was set up as an Maeven Project so that all dependecies specified in the pom.xml file were build automatically.

## Data

The non semantic csv data we used to create the instances and properties of the ABOX can be found in the /data directory. The TBOX.rdf file which provides the onthology for the ABOX creation was created with [Grafo](https://app.gra.fo/dashboard) and exported from there.

## Create ABOX
***
The java class createAbox.java includes all the code necessary to create an ABOX from our nonsemantic csv data based on the onthology provided by the TBOX

