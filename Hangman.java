import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.regex.*;
import java.util.*;
import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;

public class Hangman{
  private static Map<Integer, List<String>> list;
  private static Map<Integer, List<String>> common_list;
  private static ArrayList<Character> right;
  private static ArrayList<Character> wrong;
  private static String ignore = "";

  private static void getList(){
     try{
       for(File f1 : new File("final/").listFiles()){
         BufferedReader br = new BufferedReader(new FileReader(f1));
         String st;
         while((st = br.readLine()) != null){
            st = st.trim();
            if(list.get(st.length()) == null) {
                list.put(st.length(), new ArrayList<String>());
              }
            list.get(st.length()).add(st);
         }
       }
     }catch(IOException error){
       System.out.println(error);
     }
   }
   private static void getcommonList(){
      try{
          File f1 = new File("common_words");
          BufferedReader br = new BufferedReader(new FileReader(f1));
          String st;
          while((st = br.readLine()) != null){
             st = st.replaceAll("[\\s|\\u00A0]+","");
             if(common_list.get(st.length()) == null) {
                 common_list.put(st.length(), new ArrayList<String>());
               }
             common_list.get(st.length()).add(st);
          }
        }catch(IOException error){
        System.out.println(error);
      }
    }

    private static char guessing(String s){
      List<String> maybe_uncommon = new ArrayList<String>();
      List<String> maybe_common= new ArrayList<String>();
      HashMap <Character, Integer> count = new HashMap<>();
      //List<String> state_words = new ArrayList<String>(Arrays.asList(s.split("\\s+")));
      int max = 0;
      char ret = 'e';
      boolean flag = false;
      String word_guess = null;
      String st = "";
      int _score = 0;
      int min = 10000;
      boolean present = false;

      for(int i = 0; i < s.length(); i++){
          if(s.charAt(i) == ' ' || i == s.length() - 1) {
              if(s.length() -1 == i){
                st = st + s.charAt(i);
              }
              if(_score < min && present == true){
                  min = _score;
                  word_guess = st;
                  _score = 0;
                  present = false;
                  }
              st = "";
            }
           else{
             if(s.charAt(i) == '_'){
               _score++;
               present = true;
             }
             st = st + s.charAt(i);
           }
        }
     //System.out.println(word_guess);
     if(word_guess.length() == 1){
        if(!right.contains('i') && !wrong.contains('i'))
        return 'i';
        else if(!right.contains('a') && !wrong.contains('a'))
        return 'a';
      }
      word_guess = word_guess.toLowerCase();
      Pattern p1;
      if(ignore.length() == 0)
       p1 = Pattern.compile(word_guess.replace("_", "[a-z]"));
      else
       p1 = Pattern.compile(word_guess.replace("_", String.format("[a-z&&[^%s]]",ignore)));

       if(common_list.get(word_guess.length())!= null){
         for(String str: common_list.get(word_guess.length())){
           Matcher m1 = p1.matcher(str);
           if(m1.find()){
             //System.out.println(str);
             maybe_common.add(str);
           }
         }
       }

      if(list.get(word_guess.length())!= null){
        for(String str: list.get(word_guess.length())){
          Matcher m1 = p1.matcher(str);
          if(m1.find() && maybe_common.contains(str) == false){
            maybe_uncommon.add(str);
          }
        }
      }
      for(String str: maybe_common){
        for(int i = 0; i < str.length(); i++){
          if(count.get(str.charAt(i)) == null)
          count.put(str.charAt(i), 1);
          else
          count.put(str.charAt(i), count.get(str.charAt(i))+1);
        }
      }
      for(char i = 'a'; i <= 'z'; i++){
        if(!right.contains(i) && !wrong.contains(i)){
          if(count.get(i)!= null && count.get(i) > max) {
            max = count.get(i);
            ret = i;
            flag = true;
          }
        }
      }
      if(flag == false){
      for(String str: maybe_uncommon){
        for(int i = 0; i < str.length(); i++){
          if(count.get(str.charAt(i)) == null)
          count.put(str.charAt(i), 1);
          else
          count.put(str.charAt(i), count.get(str.charAt(i))+1);
        }
      }
      for(char i = 'a'; i <= 'z'; i++){
        if(!right.contains(i) && !wrong.contains(i)){
          if(count.get(i)!= null && count.get(i) > max) {
            max = count.get(i);
            ret = i;
            flag = true;
          }
        }
      }
    }
    else if(flag == false){
      for(char i = 'a'; i <= 'z'; i++){
        if(!right.contains(i) && !wrong.contains(i)) {
          ret = i;
          break;
        }
      }
    }
    return ret;
    }
  public static void main(String[] args) {

       while(true){
       Data current = WebsiteInterface.newgame();
       list = new HashMap<Integer, List<String>>();
       common_list = new HashMap<Integer, List<String>>();
       right = new ArrayList<Character>();
       wrong = new ArrayList<Character>();
       getList();
       getcommonList();
         while(current.status.equals("ALIVE")){
           char guess;
           //System.out.println("'status': " + current.status + ", 'token': " + current.token + ",'remaining_guesses': " + current.remaining_guesses + ", 'state': " + current.state);
           //do{
           guess = guessing(current.state);
           //}while(right.contains(i) && wrong.contains(i));
           System.out.println(current.state);
           Data next = WebsiteInterface.newguess(current, guess);
            if(current.state.equals(next.state) || next.status.equals("DEAD")){
                wrong.add(guess);
                ignore += guess;
                System.out.println("Character guessed "+ guess+ " is wrong.");
            }
            else{
                right.add(guess);
                System.out.println("Character guessed "+ guess+ " is right.");
              }
            current = next;

          }
          if(current.status.equals("DEAD"))
          System.out.println("FAILED");
          if(current.status.equals("FREE"))
          System.out.println("SUCCESS");

      }
    }

}
/*public static void main(String[] args)throws IOException{

  int errors = 0;
  list = new HashMap<Integer, List<String>>();
  common_list = new HashMap<Integer, List<String>>();
  right = new ArrayList<Character>();
  wrong = new ArrayList<Character>();
  getList();
  getcommonList();
  while(errors != 3){
    System.out.println("Enter Status");
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String input = br.readLine();
    char guess = guessing(input);
    System.out.println("The guess is " + guess);
    System.out.println("Enter 1 or 0");
    br = new BufferedReader(new InputStreamReader(System.in));
    input = br.readLine();
    if(input.equals("0")){
     errors++;
     ignore += guess;
     wrong.add(guess);
   }
   else if(input.equals("1")){
     right.add(guess);
   }
  }
  if(errors == 3)
  System.out.println("FAIL");
  else
  System.out.println("PASS");
}
}*/
