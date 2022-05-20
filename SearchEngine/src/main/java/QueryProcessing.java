import java.util.LinkedList;

public class QueryProcessing {
    RemoveStopWord StopWords;
    Stemming Stemmer;
    class Sentence_List
    {
        LinkedList<String> list;
        String stemmedSentence;
        public Sentence_List()
        {
            list = new LinkedList<>();
            stemmedSentence="";
        }
    }
    public QueryProcessing()
    {
        StopWords = new RemoveStopWord(127,"./src/stopWords.txt");
        Stemmer = new Stemming();
    }
    private void processWord(String word,Sentence_List processed_data,char lastChar)
    {
        if(word != ""&& StopWords.isNotAStopWord(word))
        {
            processed_data.list.add(Stemmer.getStemmedString(word));
            processed_data.stemmedSentence += (word);
            if(lastChar == '"')
                processed_data.stemmedSentence += lastChar;
            processed_data.stemmedSentence += " ";
            return;
        }
        if(lastChar == '"')
            processed_data.stemmedSentence += lastChar;
    }
    public Sentence_List process(String query)
    {
        query = query.trim();
        int n = query.length();
        Sentence_List processed_data = new Sentence_List();
        String word;
        char temp;
        for (int i = 0;i< n;i++){
            word = "";
            temp = query.charAt(i);
            while(temp != ' ' && temp != '"'){
                word = word + temp;
                i++;
                if(i < n)
                    temp = query.charAt(i);
                else break;
            }
            processWord(word,processed_data,temp);
        }
        processed_data.stemmedSentence = processed_data.stemmedSentence.trim();
        System.out.println(processed_data.stemmedSentence);
        return processed_data;
    }
    public static void main(String[] args)
    {
        QueryProcessing qp = new QueryProcessing();
        qp.process("\"learn english fast\"  h\" ");
    }

//    public static void main(String[] args)
//    {
//        QueryProcessing qp = new QueryProcessing();
//        qp.process("how to learn english fast");
//    }
}