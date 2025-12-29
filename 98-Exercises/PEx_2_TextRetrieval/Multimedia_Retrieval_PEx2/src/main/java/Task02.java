import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.queryparser.classic.QueryParser; // TODO: resolve this issue



import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Scanner;

/* Task02.1.a:
Provide a simple interface for basic keyword-based movie search, including predicates on metadata such as release year or ratings. During development, it is best to use a small subset of the data (e.g., the first 1,000 entries) and rebuild the index from scratch each time to avoid inconsistencies from previous runs.
*/
// Based on this tutorial: 
// https://www.baeldung.com/lucene
// and with help from Chatgpt to setup the maven project with lucene and gson in it.
// and with https://www.javaspring.net/blog/read-in-json-file-java/
class Movie {
    String title;
    String cast;
    float rating;
    int year;
    String overview;
}

public class Task02 {
    // TODO:
    // Import the JSONL data into a Lucene index:
    // Create a Lucene index and implement code to read the JSONL file. Extract relevant information such as movie titles, descriptions, ratings, and other metadata, and add them to the index.
    static void index_from_json() {

        Gson gson = new Gson();

        // with help of Chatgpt for the jsonl errors to parse with a buffered reader
        // Read from jsonl into index
        try (BufferedReader br = new BufferedReader(
                new FileReader("src/main/resources/movie_dataset.jsonl"))) {

            String line;
            int count = 0;
            Directory memoryIndex = new RAMDirectory();
            StandardAnalyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(memoryIndex, indexWriterConfig);

            while ((line = br.readLine()) != null && count < 1000) {
                Movie m = gson.fromJson(line, Movie.class);
                Document document = new Document();

                document.add(new TextField("title", m.title, Field.Store.YES));
                document.add(new TextField("cast", m.cast, Field.Store.YES));
                document.add(new TextField("rating", Float.toString(m.rating), Field.Store.YES));
                document.add(new TextField("year", Integer.toString(m.year), Field.Store.YES));
                document.add(new TextField("overview", m.overview, Field.Store.YES));

                writer.addDocument(document);
                writer.close();

                count++;
            }
            // Implement a basic keyword search function:**
            // Develop a simple search function that allows users to input keywords and retrieve movies whose titles or descriptions contain those keywords.
            DirectoryReader reader = DirectoryReader.open(memoryIndex); // need close at the end
            IndexSearcher searcher = new IndexSearcher(reader); // need close at the end
            QueryParser titleParser = new QueryParser("title", analyzer);
            QueryParser overviewParser = new QueryParser("overview", analyzer);
            System.out.println("Provide a search term: ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            Query titleQuery = titleParser.parse(input);
            ScoreDoc[] hitsTitle = searcher.search(titleQuery, 10).scoreDocs;

            Query overviewQuery = overviewParser.parse(input);
            ScoreDoc[] hitsOverview = searcher.search(overviewQuery, 10).scoreDocs;

            System.out.println("Print search hits on input for title and overview");
            // Not very efficient in terms of two for loops
            for (ScoreDoc scoreDoc : hitsTitle) {
                Document hitDoc = searcher.doc(scoreDoc.doc);
                System.out.println(hitDoc.get("title"));
            }
            for (ScoreDoc scoreDoc : hitsOverview) {
                Document hitDoc = searcher.doc(scoreDoc.doc);
                System.out.println(hitDoc.get("title"));
            }


            // TODO:
            // Enhance search with filter criteria:**
            // Add support for filters based on movie ratings, release dates, or other metadata. You may also improve ranking by prioritizing keyword occurrences in specific fields (e.g., cast, title).
            // TODO: figure out where to put the filter criteria

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        // TODO: figure how to split it into methods if necessary or else just put all in main I ugess
        Task02.index_from_json();
    }
}