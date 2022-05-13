public class ResultStructure {
    public String url;
    public String title;
    public String plaintext;
    public ResultStructure(String url,String title,String pt){
        this.url=url;
        this.plaintext=pt;
        this.title=title;
    }
    public String getTitle(){
        return title;
    }
    public String getUrl(){
        return url;
    }
    public String getPlaintext(){
        return plaintext;
    }

}
