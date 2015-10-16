package example.jocgeo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
/**
 * Created by ricardo on 29/04/15.
 */

public class PlayGame extends Activity  {

    private RadioGroup radioPaisesGroup;
    private RadioButton radioPaisesButton;
    private int idx;

    private int i=0;
    TextView textViewTime;
    CounterClass timer;

    public class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval){
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis=millisUntilFinished;
            String hms=String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            System.out.println(hms);
            textViewTime.setText(hms);
        }
        @Override
        public void onFinish() {
            textViewTime.setText("Tiempo agotado");

            setHighScore();
            //incorrect
           // Toast.makeText(PlayGame.this, "TIEMPO AGOTADO", Toast.LENGTH_SHORT).show();
            scoreTxt.setText("Score: 0");
            finish();
            response.setImageResource(R.drawable.cross);
            response.setVisibility(View.VISIBLE);

        }
    }

    private int level=0;

    private String[] paises2 = {"Inicio","Francia","Portugal","España","Reino Unido","Irlanda","Islandia","Belgica","Holanda", "Alemania", "Polonia", "Bielorrusia", "Rusia", "Finlandia","Suecia", "Noruega", "Ucrania", "Moldavia","Bulgaria", "Grecia","Macedonia", "Croacia", "Hungria","Rep Checa", "Austria", "Suiza", "Italia","Fin, volver a empezar"};
    private String[] paises={"Inicio","Francia","Portugal","España","Reino Unido","Irlanda","Islandia","Belgica","Holanda", "Alemania", "Polonia","Italia","Fin, volver a empezar"};
    private String[] paises3=  {"Inicio","Francia","Portugal","España","Reino Unido","Irlanda","Islandia","Belgica","Holanda", "Alemania", "Polonia", "Bielorrusia", "Rusia", "Estonia", "Letonia","Lituania","Finlandia","Suecia", "Noruega", "Ucrania", "Moldavia", "Romania", "Bulgaria", "Grecia", "Albania", "Macedonia", "Serbia", "Montenegro", "Bosnia", "Croacia", "Eslovenia", "Hungria", "Eslovaquia", "Rep Checa", "Austria", "Suiza", "Italia","Fin, volver a empezar"};
    //random number generator
    private Random random;
    //ui elements
    private TextView question, answerTxt, scoreTxt;
    private ImageView response;
    private Button      enterBtn;

    //shared preferences
    private SharedPreferences gamePrefs;
    public static final String GAME_PREFS = "JuegoGeografia";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playgame);

        addListenerOnButton();

        if(savedInstanceState!=null){
            //saved instance state data
            level=savedInstanceState.getInt("level");
            int exScore = savedInstanceState.getInt("score");
            scoreTxt.setText("Score: "+exScore);

        }
        else{
            //get passed level number
            Bundle extras = getIntent().getExtras();
            if(extras !=null)
            {
                int passedLevel = extras.getInt("level", -1);
                if(passedLevel>=0) level = passedLevel;
            }
        }

        textViewTime=(TextView) findViewById(R.id.textViewTime);

        if(level==0){

            timer=new CounterClass(10000,1000);
            textViewTime.setText("00:00:10");
        }
        else if(level==1){
            timer=new CounterClass(7000,1000);
            textViewTime.setText("00:00:7");
            paises=paises2;
        }

        else if(level==2){
            timer=new CounterClass(4000,1000);
            textViewTime.setText("00:00:4");
            paises=paises3;
        }

        timer.start();

        //initiate shared prefs
        gamePrefs = getSharedPreferences(GAME_PREFS, 0);

        //text and image views
        question = (TextView)findViewById(R.id.question);
        answerTxt = (TextView)findViewById(R.id.answer);
        response = (ImageView)findViewById(R.id.response);
        scoreTxt = (TextView)findViewById(R.id.score);

        //hide tick cross initially
        response.setVisibility(View.INVISIBLE);

        enterBtn = (Button)findViewById(R.id.enter);

        //initialize random
        random = new Random();
        //play
        timer.start();
        chooseQuestion();
    }

    public void addListenerOnButton() {

        radioPaisesGroup = (RadioGroup) findViewById(R.id.radioPaises);
        enterBtn = (Button) findViewById(R.id.enter);

        enterBtn.setOnClickListener(new OnClickListener() {


            @Override
            public void onClick(View view) {

                // get selected radio button from radioGroup
                int selectedId = radioPaisesGroup.getCheckedRadioButtonId();

                int exScore = getScore();

                // find the radiobutton by returned id
                radioPaisesButton = (RadioButton) findViewById(selectedId);

                if (view.getId() == R.id.enter) {
                    //enter button
                    //get answer

                    // Toast.makeText(PlayGame.this,radioPaisesButton.getText(), Toast.LENGTH_SHORT).show();
                    // Toast.makeText(PlayGame.this,paises[idx],Toast.LENGTH_SHORT).show();
                    if(radioPaisesButton!=null) {
                        if (radioPaisesButton.getText().equals(paises[i])) {
                            scoreTxt.setText("Score: " + (exScore + 1));
                            response.setImageResource(R.drawable.tick);
                            response.setVisibility(View.VISIBLE);
                            if (radioPaisesButton != null) radioPaisesGroup.clearCheck();
                        }

                           else {
                            //set high score
                            setHighScore();
                            Toast.makeText(PlayGame.this, "HAS FALLADO VUELVE A EMPEZAR", Toast.LENGTH_SHORT).show();
                            //incorrect
                            scoreTxt.setText("Score: 0");
                            finish();
                            if (radioPaisesButton != null) {
                                radioPaisesGroup.clearCheck();
                                }

                            response.setImageResource(R.drawable.cross);
                            response.setVisibility(View.VISIBLE);

                        }
                        timer.start();
                        chooseQuestion();
                    }
                    else{
                        Toast.makeText(PlayGame.this, "Elige una respuesta", Toast.LENGTH_SHORT).show();
                      //  finish();
                    }

                }

            }
        });
    }

    //method retrieves score
    private int getScore(){
        String scoreStr = scoreTxt.getText().toString();
        return Integer.parseInt(scoreStr.substring(scoreStr.lastIndexOf(" ")+1));
    }

    //method generates questions
    private void chooseQuestion(){
        //reset answer text
        answerTxt.setText("?");

        if(i<paises.length){

            i++;

        }


        String  random2 = (paises[i]);
if(random2=="Fin, volver a empezar"){

    Toast.makeText(PlayGame.this, "HAS COMPLETADO EL NIVEL", Toast.LENGTH_SHORT).show();
    finish();
}
        question.setText(random2);

        //idx = new Random().nextInt(paises.length);

       // String  random2 = (paises[idx]);
        //question.setText(random2);


            //show question


    }

    //set high score
    private void setHighScore(){
        int exScore = getScore();
        if(exScore>0){
            //we have a valid score
            SharedPreferences.Editor scoreEdit = gamePrefs.edit();
            DateFormat dateForm = new SimpleDateFormat("dd MMMM yyyy");
            String dateOutput = dateForm.format(new Date());
            //get existing scores
            String scores = gamePrefs.getString("highScores", "");
            //check for scores
            if(scores.length()>0){
                //we have existing scores
                List<Score> scoreStrings = new ArrayList<Score>();
                //split scores
                String[] exScores = scores.split("\\|");
                //add score object for each
                for(String eSc : exScores){
                    String[] parts = eSc.split(" - ");
                    scoreStrings.add(new Score(parts[0], Integer.parseInt(parts[1])));
                }
                //new score
                Score newScore = new Score(dateOutput, exScore);
                scoreStrings.add(newScore);
                //sort
                Collections.sort(scoreStrings);
                //get top ten
                StringBuilder scoreBuild = new StringBuilder("");
                for(int s=0; s<scoreStrings.size(); s++){
                    if(s>=10) break;
                    if(s>0) scoreBuild.append("|");
                    scoreBuild.append(scoreStrings.get(s).getScoreText());
                }
                //write to prefs
                scoreEdit.putString("highScores", scoreBuild.toString());
                scoreEdit.commit();

            }
            else{
                //no existing scores
                scoreEdit.putString("highScores", ""+dateOutput+" - "+exScore);
                scoreEdit.commit();
            }
        }
    }

    //set high score if activity destroyed
    protected void onDestroy(){
        setHighScore();
        super.onDestroy();
    }

    //save instance state
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //save score and level
        int exScore = getScore();
        savedInstanceState.putInt("score", exScore);
        //  savedInstanceState.putInt("level", level);
        //superclass method
        super.onSaveInstanceState(savedInstanceState);


    }
} 
