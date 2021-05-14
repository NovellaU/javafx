package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class Main extends Application {

    private boolean Xkaik = true;
    private boolean kasLabi = true;
    private List<ManguValjad> readJaVeerud = new ArrayList<>();
    final int suurus = 4;
    private Ruudustik[][] laud = new Ruudustik[suurus][suurus];
    int kyljemoot =125;
    private Pane root =new Pane();

    //protseduur kus tekitame 4x4 ruudustiku ja lisame 2d massiivi
    private Parent Manguvali(){
        root.setPrefSize(600,600);
        for(int i=0;i<suurus; i++){
            for (int j=0;j<suurus; j++){
                Ruudustik ruudustik = new Ruudustik();
                ruudustik.setTranslateX( j* kyljemoot);
                ruudustik.setTranslateY(i* kyljemoot);

                root.getChildren().add(ruudustik);
                laud[j][i] = ruudustik;

            }
        }
        //lisame horisontaalselt asetsevad (tyhjad)liikmed
        for(int horisontaalne=0;horisontaalne<suurus; horisontaalne++){
            readJaVeerud.add(new ManguValjad(laud[0][horisontaalne], laud[1][horisontaalne], laud[2][horisontaalne],
                    laud[3][horisontaalne]));
        }
        //lisame vertikaalselt asetsevad (tyhjad)liikmed

        for(int vertikaalne=0;vertikaalne<suurus; vertikaalne++){
            readJaVeerud.add(new ManguValjad(laud[vertikaalne][0], laud[vertikaalne][1], laud[vertikaalne][2],
                    laud[vertikaalne][3]));
        }
        //lisame diagonaalid (tyhjad)liikmed

        readJaVeerud.add(new ManguValjad(laud[0][0], laud[1][1], laud[2][2], laud[3][3]));
        readJaVeerud.add(new ManguValjad(laud[3][0], laud[2][1], laud[1][2], laud[0][3]));

        return root;
    }


    @Override
    //siin teeme akna ja anname nime
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("TripsTrapsTrull: Sina vs. Arvuti");
        primaryStage.setScene(new Scene(Manguvali()));
        primaryStage.show();

    }

    //protseduur kus kontrollme kas kuskil on juba 4 sama symbolit reas/diagonaalis
    private void kontrolliKasKaib(){

        for(ManguValjad manguValjad: readJaVeerud ){
            if(manguValjad.valiOnTais()){
                kasLabi = false;
                lopeta(manguValjad);
                break;
            }
        }

    }

    //protseduur kus lopetame mangu sellisema, et kui eelmises protseduuris on 4 sama symbolit reas siis
    //siin protseduuris joonistame nende 4 joone peale pika joone (naitamaks et mang on labi, sest need 4 symbolit)
    //on reas
    private void lopeta(ManguValjad manguValjad) {
        Line line = new Line();
        line.setStartX(manguValjad.ruudustik[0].getX());
        line.setStartY(manguValjad.ruudustik[0].getY());
        line.setEndX(manguValjad.ruudustik[0].getX());
        line.setEndY(manguValjad.ruudustik[0].getY());

        root.getChildren().add(line);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.75),
                new KeyValue(line.endXProperty(), manguValjad.ruudustik[3].getX()),
                new KeyValue(line.endYProperty(), manguValjad.ruudustik[3].getY())));
        timeline.play();



        //siin alustame laua algvaartustamist, eemaldame joone ja siis kutsume valja protseduuri kus teeme laua tyhjaks
        timeline.setOnFinished(e -> {
            root.getChildren().remove(line);
            algusesse();

        });

    }

    //algvaartustame laua (teeme laua tyhjaks)
    private void algusesse() {
        kasLabi = true;
        Xkaik = true;
        for (int i = 0; i < suurus; i++) {
            for (int j = 0; j < suurus; j++) {
                laud[j][i].text.setText("");
            }
        }
    }

    private class ManguValjad{
        private  Ruudustik[] ruudustik;
        public ManguValjad(Ruudustik... ruudustik){
            this.ruudustik=ruudustik;
        }
        public boolean valiOnTais(){
            if(ruudustik[0].getValue().isEmpty()){

                return false;
            }
            return ruudustik[0].getValue().equals(ruudustik[1].getValue())
                    &&ruudustik[0].getValue().equals(ruudustik[2].getValue())
                    &&ruudustik[0].getValue().equals(ruudustik[3].getValue());

        }

    }

    //siin klassis kaib manguvaljale lisamine-eemaldamine
    private class Ruudustik extends StackPane {
        private Text text = new Text();
        public Ruudustik(){
            Rectangle piirid = new Rectangle(kyljemoot,kyljemoot);
            piirid.setFill(null);
            piirid.setStroke(Color.BLACK);
            setAlignment(Pos.CENTER);
            getChildren().addAll(piirid, text);
            text.setFont(Font.font(70));
         //kui kasutaja vjutab hiirega, läheb event tööle
            setOnMouseClicked(event -> {

                if(!kasLabi)  return;

                if(event.getButton()== MouseButton.PRIMARY){

                    if(!Xkaik) return;
                    else {
                        markX();
                        kontrolliKasKaib();
                    }
                    if(Xkaik) return;
                    else {
                        markO();
                        kontrolliKasKaib();
                        return;

                    }
                }
            });
        }

        //getter et vaadata kas massiivis kohal [i][j] on mingi liige
        public String getValue(){
            return text.getText();
        }
        //setter arvuti jaoks, kui eelnevalt kontrollitud kohas ei ole symbolit ees
        public void setValue(){
            this.text.setText("O");
        }

        //kui klikitud ruudul ei ole x voi y siis lisame sinna uue x (mangija kaik)
        private void markX(){
            if(getValue() != "O" && getValue()==""){

                text.setText("X");
                Xkaik = false;
            }
            else Xkaik = true;

        }

        //getterid x ja y vaartuste jaoks (mangu lopetamisel(laua puhastamisel))
        public double getX() {
            return getTranslateX() + 100;
        }

        public double getY() {
            return getTranslateY() + 100;
        }

        //arvuti kaik, kontrollime kas laual juhuslikul kohal on midagi, kui on siis genereerime uue juhusliku koha,
        private void markO() {

            boolean tehtud = true;
            do {

                int k = ThreadLocalRandom.current().nextInt(0, 3 + 1);
                int l= ThreadLocalRandom.current().nextInt(0, 3 + 1);

                if(laud[k][l].getValue()== ""){
                    laud[k][l].setValue();
                    tehtud=false;
                    Xkaik = true;

                }
                else Xkaik = false;
            }while(tehtud);
        }

    }


    //siit kaivitame programmi too
    public static void main(String[] args) {
        launch(args);
    }
}
