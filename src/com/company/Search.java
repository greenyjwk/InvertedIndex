package com.company;

import java.util.*;

public class Search {

    // single keyword search
    public void singleKeywordSearch(HashMap<String, ArrayList<Integer>> termList, String keyword) {
        if (termList.containsKey(keyword)) System.out.println(termList.get(keyword));
    }

    public Set<Integer> twoKeywordSearch(HashMap<String, ArrayList<Integer>> termList, String keyword1, String keyword2) {
        if (termList.containsKey(keyword1) && termList.containsKey(keyword2)) {
            Set set1 = new HashSet<Integer>(termList.get(keyword1));
            Set set2 = new HashSet<Integer>(termList.get(keyword2));
            Set<Integer> intersection = new HashSet<>(set1);
            intersection.retainAll(set2);
            System.out.println(intersection);
            return intersection;
        }else{
            System.out.printf("Not existed");
            return null;
        }
    }


    public void twoKeywordSearchConditionOr(HashMap<String, ArrayList<Integer>> termList, String keyword1, String keyword2) {
        if (termList.containsKey(keyword1) || termList.containsKey(keyword2)) {
            Set set1 = new HashSet<Integer>(termList.get(keyword1));
            Set set2 = new HashSet<Integer>(termList.get(keyword2));

            Set<Integer> setMerged = new HashSet<>();

            setMerged.addAll(set1);
            setMerged.addAll(set2);
            System.out.println(setMerged);
        }else{
            System.out.printf("Not existed");
        }
    }


    public void multipleKeywordSearch(HashMap<String, ArrayList<Integer>> termList, String keywords) {
        ArrayList<String> keywordTokens = InvertedIndex.tokenizer(keywords);
        ArrayList<ArrayList<Integer>> tempList = new ArrayList<>();


        for(String keyword : keywordTokens){
            if(  termList.containsKey(keyword) ){
                tempList.add(termList.get(keyword));
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
            for(String token : keywordTokens) tokenSort.put(token, termList.get(token).size());

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
            if(e1.size() < e2.size()) return -1;
            else if(e1.size() > e2.size()) return 1;
            else return 0;
        }
    }
}
