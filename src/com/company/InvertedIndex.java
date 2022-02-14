package com.company;

import java.io.*;
import java.util.*;
import java.util.StringTokenizer;


public class InvertedIndex {

    private HashSet<String> stopwordsList;
    private File[] fileList;
    private String[] myDocs;               //input docs
//    private ArrayList<String> termList;    //dictionary
    private HashMap< String, ArrayList<Integer>> termList;    //dictionary
    private HashMap< Integer, ArrayList<String>> docsContainer;    //dictionary
    private ArrayList<String> singleDoc;    //dictionary

    private ArrayList<ArrayList<Integer>> docLists;

    public InvertedIndex(File[] fileListParam, File stopwordsFile) {
        this.fileList = fileListParam;
        this.stopwordsList = stopListCreater(stopwordsFile);
        this.termList = new HashMap< String, ArrayList<Integer>>();
//        System.out.println(this.stopwordsList);
        int docId;
        for(int i = 0; i < this.fileList.length; i++) {
            docId = i;
            // ********* Read single file *********
            String singleDoc = new String();
            try (BufferedReader br = new BufferedReader(new FileReader(fileListParam[i]))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // process the line.
                    singleDoc += line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // ********* Tokenizing *********
            ArrayList<String> tokenList = tokenizer(singleDoc);
            // ********* Tokenizing *********

            // ********* Removing stop words *********
            for(int j = 0 ; j < tokenList.size(); j++) if(this.stopwordsList.contains(tokenList.get(j))) tokenList.remove(j);
            // ********* Removing stop words *********

            // ********* Porter's Stemmer *********
            ArrayList<String> tokensAfterStemmed = PortersStemmer(tokenList);

            // Removing nulls
            while (tokensAfterStemmed.remove(null)) {}
//            System.out.println(tokensAfterStemmed);
            // ********* Porter's Stemmer *********



            for(int tokenIndex=0; tokenIndex< tokensAfterStemmed.size(); tokenIndex++){
                String word = tokensAfterStemmed.get(tokenIndex);
                if(!this.termList.containsKey(word)) {
                    ArrayList<Integer> docIdList = new ArrayList<Integer>();

                    //Adding a docID Number
                    docIdList.add(docId);
                    termList.put(word ,docIdList);
                } else {
                    //if the docID is not in list of the docIdList
                    if(!termList.get(word).contains(docId)){
                        termList.get(word).add(docId); // Adding a docID from the list
                    }
                }
            }
            System.out.println(termList);
//            break;
        }
    }


    public ArrayList<String> PortersStemmer(ArrayList<String> tokenList){
        Stemmer stemmer = new Stemmer();
        ArrayList<String> tokensAfterStemmed = new ArrayList<>();
        for( String token: tokenList){
            char[] charArray = token.toCharArray();
            stemmer.add( charArray, token.length());
            stemmer.stem();
            String temp = stemmer.toString();
            tokensAfterStemmed.add(temp);
        }
        return tokensAfterStemmed;
    }

    public HashSet<String> stopListCreater(File stopwordsFile){
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


    public ArrayList<String> tokenizer(String doc){
        StringTokenizer st1 = new StringTokenizer(doc, " ,.:;?![]()'%$#!/+-*\"\'");
        ArrayList<String> tokens = new ArrayList<>();
        while (st1.hasMoreTokens()) tokens.add(st1.nextToken());
        return tokens;
    }


    public static void main(String args[]) throws IOException {
        //Creating a File object for directory
        File directoryPath = new File("/Users/jiwoongkim/Documents/RIT/Spring_2022/ISTE-612/Lab01/Lab1_Data");
        File stopwordsPath = new File("/Users/jiwoongkim/Documents/RIT/Spring_2022/ISTE-612/Lab01/stopwords");


        // ********* Stemmer Test *********
        System.out.println("-------------this is stemmer test");

//        String test = "coworked";
//        char[] charArray = test.toCharArray();
//        Stemmer stemmer = new Stemmer();
//        stemmer.add( charArray, test.length());
//        stemmer.stem();
//        String dd = stemmer.toString();
//        System.out.println(dd);

        System.out.println("-------------this is stemmer test");
        // ********* Stemmer Test *********

        //List of all files and directories
        File filesList[] = directoryPath.listFiles();
        System.out.println("List of files and directories in the specified directory:");
        for(File file : filesList) {
            System.out.println(file.toString());
        }



        // Stop List Process
        File stopFilesList[] = stopwordsPath.listFiles();
        System.out.println("List of files and directories in the specified directory:");
        for(File file : stopFilesList) {
            System.out.println(file);
        }



        //Create inverted Index instance
        InvertedIndex index = new InvertedIndex(filesList, stopFilesList[0]);
//        index.test();
    }
}