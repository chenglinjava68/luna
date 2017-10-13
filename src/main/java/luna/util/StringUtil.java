package luna.util;

public class StringUtil {

    public static String stripEscape(String input){
        input=input
                .replaceAll("\\r", "\\\\r")
                .replaceAll("\\t", "\\\\t")
                .replaceAll("\\\\", "\\\\\\\\")
                .replaceAll("\\n", "\\\\\\\\n");
//        input = input.replaceAll("[\\s!$%^*(+\"\']+|[+——！，。？、~#￥%……&*（）]+","");
        return input;
    }

    public static String stripControl(String input){
        return input
                .replaceAll("\\p{Cntrl}","")
                .replaceAll("\\p{InGreek}","");
    }
}
