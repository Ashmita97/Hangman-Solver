import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.*;

public class WebsiteInterface{
  public static Data Info(String arg){

    Data newInfo = new Data();
    try{
      BufferedReader br = new BufferedReader(new InputStreamReader(
      new URL("http://gallows.hulu.com/play?code=chatterjeeashmita97@gmail.com" + arg).openStream()));

      String input = br.readLine();


      Matcher m1 = Pattern.compile("(ALIVE|DEAD|FREE)").matcher(input);
      Matcher m2 = Pattern.compile("(\\d+)").matcher(input);
      Matcher m3 = Pattern.compile("(\\d)(,)").matcher(input);
      Matcher m4 = Pattern.compile("([A-Z_'\\s]+)(\"})").matcher(input);

      if(m1.find() && m2.find() && m3.find() && m4.find()){
        newInfo.status = m1.group();
        newInfo.token = m2.group();
        newInfo.remaining_guesses = Integer.parseInt(m3.group(1));
        newInfo.state = m4.group(1);
      }
    }catch(IOException error){
			System.err.println(error);
    }
    return newInfo;
  }
  public static Data newgame(){
    return Info("");
  }
  public static Data newguess(Data current, char g){
    return Info(String.format("&token=%s&guess=%s", current.token, g));
  }
}
