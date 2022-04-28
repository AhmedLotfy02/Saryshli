import java.io.File;
import java.util.Hashtable;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class RemoveStopWord {
    private Hashtable<String, Boolean> RemoveWords = new Hashtable<String,Boolean>();
    RemoveStopWord(int n, String fileName)
    {
        try{
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            for(int i = 0;i<n;i++)
                RemoveWords.put(myReader.nextLine(),true);
        } catch (FileNotFoundException e)
        {
            System.out.println("An error occurred.");
            return;
        }
    }
    public Boolean isNotAStopWord(String word)
    {
        if(RemoveWords.containsKey(word))
            return false;
        return true;
    }

    public static void main(String[] args) {
        RemoveStopWord myObj = new RemoveStopWord(127,"./src/stopWords.txt");
//        System.out.println(myObj.isAStopWord("then"));
    }
}
