package com.company;

import java.io.*;
import java.util.*;
import java.util.StringTokenizer;

public class InvertedIndex {

    private HashSet<String> stopwordsList;
    private File[] fileList;
    private HashMap<String, ArrayList<Integer>> termList;


    public InvertedIndex(File[] fileListParam, File stopwordsFile) {
        this.fileList = fileListParam;
        this.stopwordsList = stopListCreater(stopwordsFile);
        this.termList = new HashMap<String, ArrayList<Integer>>();
        int docId;

        for (int i = 0; i < this.fileList.length; i++) {
            docId = i;

            // ********* Read single file *********
            String singleDoc = new String();
            try (BufferedReader br = new BufferedReader(new FileReader(fileListParam[i]))) {
                String line;
                while ((line = br.readLine()) != null) singleDoc += line;
            } catch (IOException e) {
                e.printStackTrace();
            }

            // ********* Tokenizing *********
            ArrayList<String> tokenList = tokenizer(singleDoc);
            // ********* Tokenizing *********


            // ********* Removing stop words *********
            for (int j = 0; j < tokenList.size(); j++) if (this.stopwordsList.contains(tokenList.get(j))) tokenList.remove(j);
            // ********* Removing stop words *********


            // ********* Porter's Stemmer *********
            ArrayList<String> tokensAfterStemmed = PortersStemmer(tokenList);

            // Removing nulls
            while (tokensAfterStemmed.remove(null)) {
            }
            // ********* Porter's Stemmer *********


            for (int tokenIndex = 0; tokenIndex < tokensAfterStemmed.size(); tokenIndex++) {
                String word = tokensAfterStemmed.get(tokenIndex);
                if (!this.termList.containsKey(word)) {
                    ArrayList<Integer> docIdList = new ArrayList<Integer>();

                    //Adding a docID Number
                    docIdList.add(docId);
                    termList.put(word, docIdList);
                } else {
                    //if the docID is not in list of the docIdList
                    if (!termList.get(word).contains(docId)) termList.get(word).add(docId); // Adding a docID from the list
                }
            }
        }
    }


    public ArrayList<String> PortersStemmer(ArrayList<String> tokenList) {
        Stemmer stemmer = new Stemmer();
        ArrayList<String> tokensAfterStemmed = new ArrayList<>();
        for (String token : tokenList) {
            char[] charArray = token.toCharArray();
            stemmer.add(charArray, token.length());
            stemmer.stem();
            String temp = stemmer.toString();
            tokensAfterStemmed.add(temp);
        }
        return tokensAfterStemmed;
    }

    public HashSet<String> stopListCreater(File stopwordsFile) {
        HashSet<String> stopList = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(stopwordsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                stopList.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopList;
    }


    public static ArrayList<String> tokenizer(String doc) {
        StringTokenizer st1 = new StringTokenizer(doc, " ,.:;?![]()'%$#!/+-*\"\'");
        ArrayList<String> tokens = new ArrayList<>();
        while (st1.hasMoreTokens()) tokens.add(st1.nextToken());
        return tokens;
    }


    public static void main(String args[]) throws IOException {
        //Creating a File object for directory
        File directoryPath = new File("./././Lab1_Data");
        File stopwordsPath = new File("./././stopwords");

        //List of all files and directories
        File filesList[] = directoryPath.listFiles();

        // Stop List Process
        File stopFilesList[] = stopwordsPath.listFiles();


        //Create inverted Index instance
        InvertedIndex invertedIndex = new InvertedIndex(filesList, stopFilesList[0]);

        //Create Search instance
        Search search = new Search();

        //Test Cases
        System.out.println("Task2 Q1 _ One Keyword Query");
        search.singleKeywordSearch( invertedIndex.termList, "come");
        search.singleKeywordSearch(invertedIndex.termList, "in");

        System.out.println();
        System.out.println();

        System.out.println("Task2 Q2 _ Two Keywords Query(AND)");
        search.twoKeywordSearch(invertedIndex.termList, "come", "get");
        search.twoKeywordSearch(invertedIndex.termList, "star", "stori");

        System.out.println();
        System.out.println();

        System.out.println("Task2 Q3 _ Two Keywords Query(OR)");
        search.twoKeywordSearchConditionOr(invertedIndex.termList, "so", "star");
        search.twoKeywordSearchConditionOr(invertedIndex.termList, "up", "hi");

        System.out.println();
        System.out.println();

        System.out.println("Task2 Q4 _ Multiple Keywords Query");
        search.multipleKeywordSearch(invertedIndex.termList, "here onli from");
        search.multipleKeywordSearch(invertedIndex.termList, "come seem like cool");
    }
}