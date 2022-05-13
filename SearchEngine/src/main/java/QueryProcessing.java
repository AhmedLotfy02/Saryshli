import java.util.LinkedList;

public class QueryProcessing {
    RemoveStopWord StopWords;
    Stemming Stemmer;

    public QueryProcessing()
    {
        StopWords = new RemoveStopWord(127,"./src/stopWords.txt");
        Stemmer = new Stemming();
    }
    private void processWord(String word,LinkedList<String> result)
    {
        if(StopWords.isNotAStopWord(word))
            result.add(Stemmer.getStemmedString(word));
    }
    public Boolean process(String query)
    {
        int n = query.length();
        LinkedList<String> result = new LinkedList<String>();
        String word;
        char temp;
        for (int i = 0;i< n;i++){
            word = "";
            temp = query.charAt(i);
            while(temp != ' '){
                word = word + temp;
                i++;
                if(i < n)
                    temp = query.charAt(i);
                else break;
            }
            processWord(word,result);
        }
        System.out.println(result);
        return true;
    }

    public static void main(String[] args)
    {
        QueryProcessing qp = new QueryProcessing();
        qp.process("how to learn english fast");
    }
}