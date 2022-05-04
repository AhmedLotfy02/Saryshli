import java.util.*;
import java.util.function.Consumer;

import static java.lang.System.*;

class singleURL {

    public String url;
    public int weight;
    //    public int location;
    singleURL(String u , int w)
    {
        url = u;
        weight = w;
    }
}

class wordInfo {

    Integer count;
    Integer weight;
    wordInfo() {
        count = 0;
        weight = 0;
    }
}

public class Ranker {

    public HashMap<String, ArrayList<singleURL>> allData;
    String searchStr = "hello Iam Omar fareed".toLowerCase(Locale.ROOT);
    public HashMap<String, wordInfo> mostURLS;
    public void filterSearchStr(){}
    Ranker() {
        mostURLS = new HashMap<>();
        allData = new HashMap<>();
//        sortBy();
        allData.put("hello" , new ArrayList<>());
        allData.put("omar" , new ArrayList<>());
        allData.put("fareed" , new ArrayList<>());
        allData.get("hello").add(new singleURL("https://www.google.com" , 12));
        allData.get("hello").add(new singleURL("https://www.facebook.com" , 15));
        allData.get("hello").add(new singleURL("https://www.linkedin.com" , 10));
        allData.get("hello").add(new singleURL("https://www.aref.com" , 8));
        allData.get("omar").add(new singleURL("https://www.aref.com" , 8));
        allData.get("omar").add(new singleURL("https://www.linkedin.com" , 10));
        allData.get("fareed").add(new singleURL("https://www.aref.com" , 8));

        filterToGetMostCommonWords();
    }

    Consumer<singleURL> addSingleDataInfo = data -> {
        wordInfo word = mostURLS.get(data.url);
        if(word == null) {
            word = new wordInfo();
            mostURLS.put(data.url,word);
        }
        word.count += 1;
        word.weight += data.weight;
    };
    Consumer<String> allDataOperations =  s -> {
        if(allData.get(s) == null) {
            return;
        };
        out.println(s);
        allData.get(s).forEach(addSingleDataInfo);
    };
    Comparator<Map.Entry<String , wordInfo>> sortingComparator = new Comparator<Map.Entry<String, wordInfo>>() {
        @Override
        public int compare(Map.Entry<String, wordInfo> o1, Map.Entry<String, wordInfo> o2) {
            if(o1.getValue().count == o2.getValue().count)
                return o2.getValue().weight - o1.getValue().weight;
            return o2.getValue().count - o1.getValue().count;
        }
    };
    void filterToGetMostCommonWords() {
        Arrays.stream(searchStr.split(" ")).forEach(allDataOperations);
        mostURLS.entrySet().stream().sorted(sortingComparator).map(s -> s.getKey()).forEach(s-> out.println(s));
    }
}
