import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/* Task02.1.a:
Provide a simple interface for basic keyword-based movie search, including predicates on metadata such as release year or ratings. During development, it is best to use a small subset of the data (e.g., the first 1,000 entries) and rebuild the index from scratch each time to avoid inconsistencies from previous runs.
*/
// Based on this tutorial: 
// https://www.baeldung.com/lucene
public class Task02 {

// lucene path /home/giovi/Applications_Giovi/lucene-10.2.2/modules
// javac -cp "/home/giovi/Applications_Giovi/lucene-10.2.2/modules/*" Task02.java

// set classpath in cmd-line:
// export CLASSPATH=/home/giovi/Applications_Giovi/lucene-10.2.2/modules/*
    // TODO:
    // Import the JSONL data into a Lucene index:
    // Create a Lucene index and implement code to read the JSONL file. Extract relevant information such as movie titles, descriptions, ratings, and other metadata, and add them to the index.

    Directory memoryIndex = new RAMDirectory();
    StandardAnalyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
    IndexWriter writter = new IndexWriter(memoryIndex, indexWriterConfig);

    //TODO: while loop for all entries up to 1000 of the jsonl file
    //apperently needs fasterxml or jackson to read and map to a java object.
    // TODO: pull one of the libraries to get the json file lines
    // TODO: create while loop for the first 1000 entries and fill the index with the code below
    Document document = new Document();

    document.add(new TextField("title", title, Field.Store.YES));
    document.add(new TextField("cast", cast, Field.Store.YES));
    document.add(new TextField("rating", rating, Field.Store.YES));
    document.add(new TextField("year", year, Field.Store.YES));
    document.add(new TextField("overview", overview, Field.Store.YES));

    writter.addDocument(document);
    writter.close();

    


    // TODO:
    // Implement a basic keyword search function:**
    // Develop a simple search function that allows users to input keywords and retrieve movies whose titles or descriptions contain those keywords.
    // TODO: figure out how to implement it if I shall use reader for CLI input

    // TODO:
    // Enhance search with filter criteria:**
    // Add support for filters based on movie ratings, release dates, or other metadata. You may also improve ranking by prioritizing keyword occurrences in specific fields (e.g., cast, title).
    // TODO: figure out where to put the filter criteria
    

    public static void main(String[] args) {
        // TODO: figure how to split it into methods if necessary or else just put all in main I ugess
    }
}