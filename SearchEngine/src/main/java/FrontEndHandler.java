import java.io.IOException;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import javax.servlet.*;
import javax.servlet.http.*;
public class FrontEndHandler extends HttpServlet{
    private QueryProcessing queryprocessor;

    public FrontEndHandler(){
        this.queryprocessor=new QueryProcessing();
    }
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String sentence=request.getParameter("SearchSentenceInput");
        System.out.println(sentence);

        LinkedList<String>QueryProcessorresult= this.queryprocessor.process(sentence);


        //query processor
        ArrayList<ResultStructure> results=new ArrayList<>();
        ResultStructure r1=new ResultStructure("www.aref.com","How to be a zalabia","jaskhfjkdsfhskdjfhsdkjfhskdfhskdfjhsf");
        ResultStructure r2=new ResultStructure("www.lotfy.com","Hasdasadasdto be a zalabia","jaskhfjkdsfhskdjfhsdkjfhskdfhskdfjhsf");

        ResultStructure r3=new ResultStructure("www.assad.com.lof","Hasdasdto be a zalabia","jaskhfjkdsfhskdjfhsdkjfhskdfhskdfjhsf");
        results.add(r1);
        results.add(r2);
        results.add(r3);

        response.setContentType ("text/html");

        String page = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "    <!-- for the browser icon -->\n" +
                "    <link rel=\"icon\" href=\"https://cdn2.iconfinder.com/data/icons/social-icons-33/128/Google-512.png\">\n" +
                "    <!-- for bootstrap -->\n" +
                "    <meta charset=\"utf-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\">\n" +
                "  <link rel=\"stylesheet\" href=\"https://use.fontawesome.com/releases/v5.6.3/css/all.css\" integrity=\"sha384-UHRtZLI+pbxtHCWp1t77Bi1L4ZtiqrqD80Kn4Z8NTSRyMA2Fd33n5dQ8lWUE00s/\" crossorigin=\"anonymous\"></head>\n" +
                "  <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>\n" +
                "  <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js\"></script>\n" +
                "  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js\"></script>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            padding: 0%;\n" +
                "        }\n" +
                "\n" +
                "        .upper-part{\n" +
                "            padding-right: 5%;\n" +
                "            padding-left: 5%;\n" +
                "        }\n" +
                "\n" +
                "        .list-results{\n" +
                "            padding: 5%;\n" +
                "            padding-top: 0%;\n" +
                "        }\n" +
                "\n" +
                "        .result-item{\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .left-part{ \n" +
                "            float: left;\n" +
                "            width: 20%;\n" +
                "        }\n" +
                "\n" +
                "        .right-part{\n" +
                "            padding-top: 20px;\n" +
                "        }\n" +
                "\n" +
                "        #google-logo{\n" +
                "            float: left;\n" +
                "            width: 120px;\n" +
                "            padding: 20px;\n" +
                "            padding-left: 0;\n" +
                "        }\n" +
                "    </style>\n" +
                "    \n" +
                "    <title>" + sentence + "</title>\n" +
                "    </head>\n" +
                "\n" +
                "    <body>\n" +
                "        <div class=\"upper-part\">\n" +
                "            <!-- left part -->\n" +
                "            <div class=\"left-image\">\n" +
                "                <img id=\"google-logo\" src=\"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\">\n" +
                "            </div>\n" +
                "\n" +
                "<!-- right part -->\n" +
                "            <div class=\"right-part\">\n" +
                "                <!-- search bar -->\n" +
                "                <div class=\"search-bar\">\n" +
                "                    <form action=\"SearchSentence\" method=\"GET\" id=\"SearchSentence\" class=\"form-inline\">\n" +
                "                        <div class=\"form-group mx-sm-3 mb-2\">\n" +
                "                          <label for=\"inputText2\" class=\"sr-only\">Search</label>\n" +
                "                          <input type=\"text\" class=\"form-control shadows\" id=\"inputText2\" placeholder=\"\" name=\"SearchSentenceInput\" style=\"width: 1000px;\" value=\"" + sentence + "\">\n" +
                "                          <div class=\"input-group-prepend\">\n" +
                "                          </div>\n" +
                "                        </div>\n" +
                "                        <input type=\"submit\" class=\"btn btn-primary mb-2\" name=\"SearchInput\" id=\"SearchInput\" value=\"Search\" />\n" +
                "\n" +
                "                        <!-- voice recognition button -->\n" +
                "                        <button type=\"submit\" class=\"btn btn-primary mb-2\" id=\"voiceButton\" title=\"requires chrome and mic access\" style=\"margin-left: 2px;\">Voice Search</button>\n" +
                "                        <!-- options buttons -->\n" +
                "                        <div class=\"options-buttons\">\n" +
                "                            <input type=\"submit\" class=\"btn btn-link\" name=\"AllInput\" id=\"AllInput\" value='All'/>\n" +
                "                            <input type=\"submit\" class=\"btn btn-link\" name=\"ImagesInput\" id=\"ImagesInput\" value='Images'/>\n" +
                "                            <button type=\"button\" class=\"btn btn-link\"><i class=\"fas fa-caret-right-square\"></i> Videos</button>\n" +
                "                            <button type=\"button\" class=\"btn btn-link\"><i class=\"fas fa-newspaper\"></i> News</button>\n" +
                "                            <button type=\"button\" class=\"btn btn-link\"><i class=\"fas fa-book\"></i> Books</button>\n" +
                "                        </div>\n" +
                "                    </form>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "\n" +
                "            \n" +
                "\n" +
                "        </div>\n" +
                "        <br>\n" +
                "        <hr class=\"style17\">" +
                "\n" +
                "        <div class=\"list-results\">\n" +
                "            <p class=\"text-muted\" style=\"padding-left: 20px;\">About  " + results.size() + " results</p>\n";
        for (int j = 0; j < results.size(); ++j) {
            Random rand = new Random();
            int randomNum = rand.nextInt((10 - 0) + 1) + 0;
            page +=
                    "            <div class=\"list-group\">\n" +
                            "                <a href=\"" + results.get(j).getUrl()+ "\" class=\" list-group-item-action flex-column align-items-start result-item\">\n" +
                            "                  <small class=\"text-muted\">" + results.get(j).getUrl() + "</small>\n" +
                            "                  <div class=\"d-flex w-100 justify-content-between\">\n" +
                            "                    <h5 class=\"mb-1\" style=\"color: blue;\">" + results.get(j).getTitle() + "</h5>\n" +
                            "                    <small class=\"text-muted\">" + (j * 2 + 1)%12 + " days</small>\n" +
                            "                  </div>\n" +
                            "                  <p class=\"mb-1\">" + results.get(j).getPlaintext() + "</p>\n" +
                            "                </a>\n" +
                            "            </div>\n";
        }
        page +=
                "        </div>\n" +
                        "\n" +
                        "        <script>\n" +
                        "            $(document).ready(function() {\n" +
                        "            // executes when HTML-Document is loaded and DOM is ready\n" +
                        "            console.log(\"document is ready\");\n" +
                        "            \n" +
                        "\n" +
                        "            $( \".shadows\" ).hover(\n" +
                        "            function() {\n" +
                        "                $(this).addClass('shadow-sm').css('cursor', 'pointer'); \n" +
                        "            }, function() {\n" +
                        "                $(this).removeClass('shadow-sm');\n" +
                        "            }\n" +
                        "            );\n" +
                        "            \n" +
                        "            // document ready  \n" +
                        "            });\n" +
                        "// for multiple images\n" +
                        "            $(document).ready(function() {\n" +
                        "            // executes when HTML-Document is loaded and DOM is ready\n" +
                        "            console.log(\"document is ready\");\n" +
                        "            \n" +
                        "\n" +
                        "            $( \".shadowss\" ).hover(\n" +
                        "            function() {\n" +
                        "                $(this).addClass('shadow').css('cursor', 'pointer'); \n" +
                        "            }, function() {\n" +
                        "                $(this).removeClass('shadow');\n" +
                        "            }\n" +
                        "            );\n" +
                        "            \n" +
                        "            // document ready  \n" +
                        "            });" +
                        "            // voice recognition\n" +
                        "            $( document ).ready(function() {\n" +
                        "                  console.log( \"ready!\" );\n" +
                        "                  try \n" +
                        "              {\n" +
                        "                  var SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;\n" +
                        "                  var recognition = new SpeechRecognition();\n" +
                        "              }\n" +
                        "              catch(e) \n" +
                        "              {\n" +
                        "                  console.error(e);\n" +
                        "                  $('.no-browser-support').show();\n" +
                        "                  $('.app').hide();\n" +
                        "              }\n" +
                        "\n" +
                        "              var inputText = document.getElementById('inputText2')\n" +
                        "              var voiceButton = document.getElementById('voiceButton')\n" +
                        "              console.log(inputText)\n" +
                        "              console.log(voiceButton)\n" +
                        "              var content = ''\n" +
                        "              recognition.continuous = false;\n" +
                        "              recognition.onresult = function (event)\n" +
                        "              {\n" +
                        "                  var current = event.resultIndex;\n" +
                        "                  var transcript = event.results[current][0].transcript;\n" +
                        "                  var mobileRepeatBug = (current == 1 && transcript == event.results[0][0].transcript);\n" +
                        "                  if(!mobileRepeatBug)\n" +
                        "                  {\n" +
                        "                      inputText.value = transcript\n" +
                        "                      recognition.stop()\n" +
                        "                  }\n" +
                        "              };\n" +
                        "\n" +
                        "              voiceButton.onclick = function () {voiceButtonClicked()}\n" +
                        "\n" +
                        "              function voiceButtonClicked()\n" +
                        "              {\n" +
                        "                  recognition.start()\n" +
                        "              }\n" +
                        "\n" +
                        "              });\n" +
                        "        </script>\n" +
                        "        \n" +
                        "    </body>\n" +
                        "\n" +
                        "</html>";
        response.getWriter().println(page);
    }
}



