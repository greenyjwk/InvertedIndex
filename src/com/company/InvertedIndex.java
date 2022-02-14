package com.company;

import java.io.*;
import java.util.*;
import java.util.StringTokenizer;

public class InvertedIndex {

    private HashSet<String> stopwordsList;
    private File[] fileList;
    private HashMap<String, ArrayList<Integer>> termList;    //dictionary

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
                while ((line = br.readLine()) != null) {
                    singleDoc += line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // ********* Tokenizing *********
            ArrayList<String> tokenList = tokenizer(singleDoc);
            // ********* Tokenizing *********


            // ********* Removing stop words *********
            for (int j = 0; j < tokenList.size(); j++)
                if (this.stopwordsList.contains(tokenList.get(j))) tokenList.remove(j);
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
        System.out.println(this.termList);
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


    public ArrayList<String> tokenizer(String doc) {
        StringTokenizer st1 = new StringTokenizer(doc, " ,.:;?![]()'%$#!/+-*\"\'");
        ArrayList<String> tokens = new ArrayList<>();
        while (st1.hasMoreTokens()) tokens.add(st1.nextToken());
        return tokens;
    }


    // single keyword search
    public void singleKeywordSearch(String keyword) {
        if (this.termList.containsKey(keyword)) System.out.println(this.termList.get(keyword));
    }

    public Set<Integer> twoKeywordSearch(String keyword1, String keyword2) {
        if (this.termList.containsKey(keyword1) && this.termList.containsKey(keyword2)) {
            Set set1 = new HashSet<Integer>(this.termList.get(keyword1));
            Set set2 = new HashSet<Integer>(this.termList.get(keyword2));
            Set<Integer> intersection = new HashSet<>(set1);
            intersection.retainAll(set2);
            System.out.println(intersection);
            return intersection;
        }else{
            System.out.printf("No existed");
            return null;
        }
    }


    public void twoKeywordSearchConditionOr(String keyword1, String keyword2) {
        if (this.termList.containsKey(keyword1) || this.termList.containsKey(keyword2)) {
            Set set1 = new HashSet<Integer>(this.termList.get(keyword1));
            Set set2 = new HashSet<Integer>(this.termList.get(keyword2));

            Set<Integer> setMerged = new HashSet<>();

            setMerged.addAll(set1);
            setMerged.addAll(set2);
            System.out.println(setMerged);
        }else{
            System.out.printf("No existed");
        }
    }


    public void multipleKeywordSearch(String keywords) {
        ArrayList<String> keywordTokens = tokenizer(keywords);
        ArrayList<ArrayList<Integer>> tempList = new ArrayList<>();
        
        
        for(String keyword : keywordTokens){
            if(  this.termList.containsKey(keyword) ){
                tempList.add(this.termList.get(keyword));
            }else{
                System.out.println("Missing keyword");
                return;
            }
        }
        Collections.sort(tempList, new docListComp() );

        Set<String> interQrels = new HashSet(tempList.get(0));
        for(int i = 1 ;  i < tempList.size(); i++) interQrels.retainAll( new HashSet(tempList.get(i)));


        if(interQrels.size() >= 0){
            Hashtable<String, Integer> tokenSort = new Hashtable<String, Integer>();
            for(String token : keywordTokens) tokenSort.put(token, this.termList.get(token).size());

            //move all entries from the hashtable and to a List
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(tokenSort.entrySet());

            //sort the entries based on the value by custom Comparator
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>(){
                public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                    return entry1.getValue().compareTo( entry2.getValue() );
                }
            });

            Map<String, Integer> mapSortedByValues = new LinkedHashMap<String, Integer>();

            //put all sorted entries in LinkedHashMap
            for( Map.Entry<String, Integer> entry : list  ) mapSortedByValues.put(entry.getKey(), entry.getValue());

            int i = 1;
            System.out.println();
            for( String str : mapSortedByValues.keySet()) {
                System.out.println( i+ ": " + str);
                i++;
            }
            System.out.println();
        }else{
            System.out.println("Missing keyword");
        }
    }
    
    
    class docListComp implements Comparator<ArrayList<Integer>>{
        @Override
        public int compare(ArrayList<Integer> e1, ArrayList<Integer> e2) {
            if(e1.size() < e2.size()){
                return -1;
            } else if(e1.size() > e2.size()){
                return 1;
            }else{
                return 0;
            }
        }
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
        InvertedIndex index = new InvertedIndex(filesList, stopFilesList[0]);


        //Test Cases
        System.out.println();
        System.out.println();
        System.out.println("Task2 Q1_One keyword Query");
        index.singleKeywordSearch("come");
        index.singleKeywordSearch("in");

        System.out.println();
        System.out.println();

        System.out.println("Task2 Q2_Two keywords Query(AND)");
        index.twoKeywordSearch("come", "get");
        index.twoKeywordSearch("star", "stori");

        System.out.println();
        System.out.println();

        System.out.println("Task2 Q3_Two keywords Query(OR)");
        index.twoKeywordSearchConditionOr("so", "star");
        index.twoKeywordSearchConditionOr("up", "hi");

        System.out.println();
        System.out.println();

        System.out.println("Task2 Q4_Multiple keywords Query(OR)");
        index.multipleKeywordSearch("here onli from");
        index.multipleKeywordSearch("come seem like cool");
    }
}