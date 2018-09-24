/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundshuffle;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author ryan
 */
public class SoundShuffle extends Application {
    public Button play;
    public static MediaPlayer player;
    public static ArrayList<File> songs = new ArrayList<File>();
    public static ArrayList<String> artists = new ArrayList<String>();
    public static ArrayList<String> genres = new ArrayList<String>();
    public static ArrayList<String> song_names = new ArrayList<String>();
    public static ArrayList<File> bad_songs = new ArrayList<File>();
    public static ArrayList<File> good_songs = new ArrayList<File>();
    
    public static Random rand = new Random();
    public static int next = 1;
    public static int index = 0;

    /**
     *
     */
    @FXML
    public Label songName;
    public Label artistName;
    public Label genreName;
    public TextField search;
    public static ArrayList<Integer> order = new ArrayList<Integer>();
    
    @Override
    public void start(Stage stage) throws Exception {
        order.add(next);
        // loads all avaibale music files
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
             File[] files = new File("resource/my_songs/").listFiles();
        // picks out the files with .m4a and mp3 endings
        File temp;
        System.out.println("All Files in Music Directory:");
        for (File i: files) {
            System.out.println(i);
            String name = i.toString();
            String ending = name.substring(name.length() - 3);
            
            if (ending.equals("m4a") || ending.equals("mp3") || ending.equals("wav")){
                if(i.toString().contains(" ")){
                    temp=new File(i.toString().replaceAll(" ",""));
                    i.renameTo(temp);
                }
                songs.add(i);
            }
        }
        
        // gets author and genre meta data for songs
        for (int i = 0; i < songs.size(); i++){
            artists.add(getMeta(songs.get(i), "mdls -name kMDItemAuthors resource/my_songs/"));
            artists.set(i, artists.get(i).replaceAll("\"", ""));
            genres.add(getMeta(songs.get(i), "mdls -name kMDItemMusicalGenre resource/my_songs/"));
            song_names.add(getMeta(songs.get(i), "mdls -name kMDItemDisplayName resource/my_songs/"));
            
        }
        
        for (String i:song_names) System.out.print(i);
        
        // prints out artists and songs
        System.out.println("\nArtists: ");
        for (String a : artists) System.out.print(" " + a);
        for (String a : genres) System.out.println(" " + a);
        System.out.println("\nUseable files:");
        songs.forEach((i) -> {
            System.out.println(i);
            
        });
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    public void good(ActionEvent e){
        good_songs.add(songs.get(order.get(index)));
    }
    
    public boolean searchVis(ActionEvent e){
        
        for (String name : song_names ){
            if (name.equals(search.getText())){
                File song = songs.get(song_names.indexOf(name));
                order.add(songs.indexOf(song));
                index=order.size()-1;
                search.setText("");
                
                if (player != null) player.stop();
                play_new_song();
                return true;
            }
            
        }
        search.setText("UNAVAIABLE");
        search.selectAll();
        return false;
    }
    
    public void updateName(){
        //System.out.println("dasdf");
       String temp="";
       temp=songs.get((order.get(index))).toString().replaceAll("%20", " ");
       songName.setText("Now Playing\n\"" + getMeta(songs.get(order.get(index)), "mdls -name kMDItemDisplayName resource/my_songs/") + "\"");
       updateArtist();
       updateGenre();
    }
    public void updateArtist(){
       artistName.setText("By: "+artists.get(order.get(index)));
    }
    public void updateGenre(){
        genreName.setText("Genre:"+genres.get(order.get(index)));
    }
    
    public void play_trigger(ActionEvent e){
           if(play.getText().equals("Play")){
               play_new_song();
               play.setText("Pause");
           }
           else if(play.getText().equals("Pause")){
               pause_song();
               play.setText("Resume");
           }
           else{
               resume_song();
               play.setText("Pause");
           }
    }
    public void play_new_song() {
       
       // player.stop();
        Media song = new Media("file://" + songs.get(order.get(index)).getAbsolutePath().replaceAll(" ", "%20"));
        player = new MediaPlayer(song);
        player.play();
        updateName();
        player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                player.stop();
                next();
            }
        });
    }
    public void pause_song()
    {
        //System.out.println(player.getCurrentTime() + " " + player.getTotalDuration());
        
 
        
        
        player.pause();
    }
    public void resume_song(){
        player.play();
    }
    
    public void next_song(ActionEvent e){
       next();
  
    }
    public void next(){
              // they are equal but it doesnt recognize this
       if ( !( player.getCurrentTime().equals(player.getTotalDuration() ))){
            //System.out.println("BAAAAAAAaAAAAAad");
            bad_songs.add(songs.get(order.get(index)));
       }
       else{
           System.out.println("Goooooooood");
           good_songs.add(songs.get(order.get(index)));
       }
       for (File i : bad_songs) System.out.print(" " + genres.get(songs.indexOf(i)));
       System.out.println();
       for (File i : good_songs) System.out.print(" " + genres.get(songs.indexOf(i)));
        
       if(player.getStatus() == MediaPlayer.Status.PLAYING){
           player.stop();
       }
       boolean good_to_play = false;
       if ( order.size() == songs.size() ) {
           int temp = order.get(order.size()-1);
           order.clear(); order.add(temp);
       }
       
       while (!good_to_play)
       {
       
            next = rand.nextInt(songs.size());
            
            // fitness selector
            good_to_play = good_or_bad(songs.get(next));
            
            for (int a : order) if (next == a) good_to_play = false;
        
       }
        
        
        order.add(next);
        index++;
        play_new_song();
        play.setText("Pause");
        updateName();
    }
    // gives weird unidentifiable error the first time the button is pressed
    public void prev_song(ActionEvent e){
        if(player.getStatus() == MediaPlayer.Status.PLAYING){
            player.stop();
        }
        index--;
        if(index<0){
          index=order.size()-1;
        }
        play_new_song();
        play.setText("Pause");
        updateName();
    }
    
    public static void allSongs(){
        //String temp="";
        //for(int i=0;i<songs.size();i++){
          //  temp=songs.get(i).toString().replaceAll("%20", " ");
           // System.out.println((i+1)+")"+temp.substring(temp.lastIndexOf("/")+1,temp.indexOf(".")));
        //}
    }   
    public void import_songs(ActionEvent e){
        FileChooser chooser=new FileChooser();
        //chooser.setTitle("Open File");
        ///chooser.showOpenDialog(stage);
   // }
               }
    public static String getMeta(File song, String command)
    {
        String name = song.toString().substring(song.toString().lastIndexOf("/")+1);
        //System.out.println("Name: " + name);
        command += name;
        //System.out.println(command);
        StringBuffer output = new StringBuffer();
        try
        {
            Process p;
            try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                    output.append(line + "\n");
            }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch(Exception ex){ex.printStackTrace();}
        
        String text = output.toString();
        int size = output.toString().length();
        //System.out.println(text);
        String done = "ERROR";
        try {
            done= text.substring(text.indexOf("(") + 1, text.lastIndexOf(")")).trim();
        }
        catch (Exception ex){
            done = text.substring(text.indexOf("\"") + 1, text.lastIndexOf("\"")).trim();
        }
        return done;
    }
    
    public static boolean good_or_bad(File song)
    {   
        final int MAX_CHANCE = 64, MIN_CHANCE = 1;
        int chance = 4;
        Random rand = new Random();
        
        //System.out.println("Song genre: " + genres.get(songs.indexOf(song)) +       "\nBad Stuff: ");

        int x = 0;
        // checks to see if it has bad attributes
        for (File bad : bad_songs){
            // if song has same attribute as bad
            if (genres.get(songs.indexOf(song)).equals(genres.get(songs.indexOf(bad)))) x++;

        }
        if (chance + Math.pow(x, 2) <= MAX_CHANCE) chance += Math.pow(x, 2);
        else chance = MAX_CHANCE;
        
        // checks to see if it has good attributes
        //System.out.print("\nGood stuff: ");
        x = 0;
        for (File good : good_songs){
            // if song has same attrtibute as good
            if (genres.get(songs.indexOf(song)).equals(genres.get(songs.indexOf(good)))) x++;
        
        }
        if (chance - x >= MIN_CHANCE) chance -= x;
        else chance = MIN_CHANCE;
        
        //System.out.println("\nSong has: " + chance);
        // lower chance, means greater chance of song being chosen
        if (rand.nextInt(chance) == 0) return true;
        else return false;   
    }
}