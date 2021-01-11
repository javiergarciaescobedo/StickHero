package es.javiergarciaescobedo.stickhero;

import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;


public class App extends Application {

    final double POS_Y_SUELO = 600;

    final double BARRA_ANCHO = 10;
    final double BARRA_TAM_INICIO = BARRA_ANCHO;
    final double BARRA_VELOCIDAD_GIRO = 2.5;
    double barraVelocidadCrece = 2;
    double inicioBarra;
    double tamanoBarra;
    double anguloGiroBarra = 0;
    Rectangle rectBarra;

    final double VELOCIDAD_CORRER_PERSONAJE = 5;
    double posXPersonaje = 0;
    double incXPersonaje = 0;
    double posYPersonaje = 500;
    double incYPersonaje = 0;

    final double ANCHO_PLATAFORMA_1 = 100;
    double inicioMinimoPlataformas = 150;
    double anchoMinimoPlataformas = 50;

    double posXHueco;
    double posXNuevaPlataforma;
    double anchoNuevaPlataforma;

    Group groupPersonaje;
    Canvas canvasBarraActual;
    Rotate rotateBarraActual;

    int puntuacion = 0;
    Label labelMarcador;
    Label labelGameover;    
    boolean gameOver = false;

    Timeline timelineCreceBarra;
    Timeline timelineGiraBarra;
    Timeline timelineCorrePersonaje;
    Timeline timelineMuerePersonaje;

    @Override
    public void start(Stage primaryStage) {
       
        Pane paneRoot = new Pane();  
        Scene scene = new Scene(paneRoot, 480, 800, Color.LIGHTGRAY);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stick Hero FX");
        primaryStage.show();
        
        // Imagen de fondo
        Image img = new Image(getClass().getResourceAsStream("/images/background.png"));
        ImageView imgView = new ImageView(img);
        paneRoot.getChildren().add(imgView);
        
        // Panel contenedor para los elementos animados del juego
        Pane paneScrollJuego = new Pane();
        paneRoot.getChildren().add(paneScrollJuego);
        
        
        /* --- DIBUJO DEL PERSONAJE --- */
        groupPersonaje = new Group();
        paneScrollJuego.getChildren().add(groupPersonaje);

        // Cuerpo
        Rectangle cuerpo = new Rectangle(48, 60, Color.BLACK);
        cuerpo.setX(20);
        cuerpo.setY(0);
        cuerpo.setArcWidth(30);
        cuerpo.setArcHeight(30);
        groupPersonaje.getChildren().add(cuerpo);
        
        // Piernas
        Rectangle piernaIzq = new Rectangle(10, 20, Color.BLACK);
        piernaIzq.setX(21);
        piernaIzq.setY(50);
        piernaIzq.setArcWidth(10);
        piernaIzq.setArcHeight(10);
        groupPersonaje.getChildren().add(piernaIzq);
        
        Rectangle piernaDer = new Rectangle(10, 20, Color.BLACK);
        piernaDer.setX(57);
        piernaDer.setY(50);
        piernaDer.setArcWidth(10);
        piernaDer.setArcHeight(10);
        groupPersonaje.getChildren().add(piernaDer);
        
        // Ojo
        Circle ojo = new Circle(5, Color.WHITE);
        ojo.setCenterX(57);
        ojo.setCenterY(22);
        groupPersonaje.getChildren().add(ojo);
        
        // Pañuelo cabeza
        Rectangle panuelo = new Rectangle(50, 13, Color.RED);
        panuelo.setX(19);
        panuelo.setY(7);
        groupPersonaje.getChildren().add(panuelo);
        
        // Picos del pañuelo
        Polygon pico1 = new Polygon(new double[]{
            20.0, 15.0,
            10.0, 32.0,
            25.0, 30.0 
        });
        pico1.setFill(Color.RED);
        groupPersonaje.getChildren().add(pico1);
        
        Polygon pico2 = new Polygon(new double[]{
            20.0, 13.0,
            0.0, 10.0,
            7.0, 25.0 
        });
        pico2.setFill(Color.RED);
        groupPersonaje.getChildren().add(pico2);
             
        // Colocar personaje en su posición de inicio
        posXPersonaje = ANCHO_PLATAFORMA_1 - BARRA_ANCHO - groupPersonaje.getLayoutBounds().getWidth();
        posYPersonaje = POS_Y_SUELO-groupPersonaje.getLayoutBounds().getHeight();
        groupPersonaje.setLayoutX(posXPersonaje);
        groupPersonaje.setLayoutY(posYPersonaje);
                
        VBox paneInfo = new VBox();
        paneInfo.setPrefWidth(scene.getWidth());
        paneInfo.setAlignment(Pos.CENTER);
        // Marcador de puntuación
        labelMarcador = new Label(String.valueOf(puntuacion));
        labelMarcador.setFont(new Font(60));
        // Texto de fin de partida
        labelGameover = new Label("Fin de partida");
        labelGameover.setVisible(false);
        labelGameover.setFont(new Font(60));
        
        paneInfo.getChildren().add(labelMarcador);
        paneInfo.getChildren().add(labelGameover);
        paneRoot.getChildren().add(paneInfo);

        // Primera plataforma de inicio
        Rectangle recPlataforma1 = new Rectangle(0, POS_Y_SUELO, ANCHO_PLATAFORMA_1, 200);
        recPlataforma1.setFill(Color.web("#424242"));
        paneScrollJuego.getChildren().add(recPlataforma1);
        
        // Segunda plataforma de inicio
        Random random = new Random();
        posXNuevaPlataforma = inicioMinimoPlataformas + random.nextInt(250);
        anchoNuevaPlataforma = anchoMinimoPlataformas + random.nextInt(25);
        Rectangle recPlataforma2 = new Rectangle(posXNuevaPlataforma, POS_Y_SUELO, anchoNuevaPlataforma, 200);
        recPlataforma2.setFill(Color.web("#424242"));
        paneScrollJuego.getChildren().add(recPlataforma2);
                
        // Preparar barra inicial
        posXHueco = ANCHO_PLATAFORMA_1;        
        tamanoBarra = BARRA_TAM_INICIO;
        rectBarra = new Rectangle(
                posXHueco - BARRA_ANCHO, POS_Y_SUELO, 
                BARRA_ANCHO, tamanoBarra);            
        rectBarra.setFill(Color.web("#DEB887"));
        anguloGiroBarra = 0;
        rotateBarraActual = new Rotate(anguloGiroBarra, posXHueco, POS_Y_SUELO+BARRA_TAM_INICIO);
        rectBarra.getTransforms().add(rotateBarraActual);  
        paneScrollJuego.getChildren().add(rectBarra);
        // Poner el personaje por delante de la barra
        groupPersonaje.toFront();

        // Animación de crecimiento de la barra
        timelineCreceBarra = new Timeline(new KeyFrame(Duration.seconds(0.017), (ActionEvent ae) -> {
            tamanoBarra += barraVelocidadCrece;
            rectBarra.setY(POS_Y_SUELO+BARRA_TAM_INICIO-tamanoBarra);
            rectBarra.setHeight(tamanoBarra);
        }));
        timelineCreceBarra.setCycleCount(Timeline.INDEFINITE);

        // Animación de giro de la barra
        timelineGiraBarra = new Timeline(new KeyFrame(Duration.seconds(0.017), (ActionEvent ae) -> {
            anguloGiroBarra += BARRA_VELOCIDAD_GIRO;
            rotateBarraActual.setAngle(anguloGiroBarra);
            if(anguloGiroBarra >= 90) {
                timelineGiraBarra.stop();
                timelineCorrePersonaje.play();
            }
        }));
        timelineGiraBarra.setCycleCount(Timeline.INDEFINITE);

        // ANIMACIÓN DE MOVIMIENTO DEL PERSONAJE
        timelineCorrePersonaje = new Timeline(new KeyFrame(Duration.seconds(0.017), (ActionEvent ae) -> {
            groupPersonaje.setLayoutX(posXPersonaje + incXPersonaje);
            incXPersonaje += VELOCIDAD_CORRER_PERSONAJE;
            if(incXPersonaje >= tamanoBarra) {
                double tamanoHueco = posXNuevaPlataforma - posXHueco;
                // Si la barra se ha quedado corta o larga
                if(tamanoBarra < tamanoHueco || tamanoBarra > tamanoHueco + anchoNuevaPlataforma) {
                    if(incXPersonaje >= tamanoBarra + groupPersonaje.getLayoutBounds().getWidth() * 0.5) {
                        timelineMuerePersonaje.play();
                        timelineCorrePersonaje.stop();
                        gameOver = true;
                        labelGameover.setVisible(true);
                        // Volver a nivel de dificultad inicial
                        barraVelocidadCrece = BARRA_VELOCIDAD_GIRO;
                    }
                } else { // LA BARRA TIENE TAMAÑO ADECUADO
                    // Si el personaje ha llegado al siguiente borde 
                    if(incXPersonaje >= tamanoHueco + anchoNuevaPlataforma) {
                        // Parar de correr el personaje
                        timelineCorrePersonaje.stop();
                        // Generar una nueva barra
                        posXHueco = posXNuevaPlataforma + anchoNuevaPlataforma;

                        // Preparar siguiente barra
                        tamanoBarra = BARRA_TAM_INICIO;
                        rectBarra = new Rectangle(
                                posXHueco - BARRA_ANCHO, POS_Y_SUELO,
                                BARRA_ANCHO, tamanoBarra);
                        rectBarra.setFill(Color.web("#DEB887"));
                        anguloGiroBarra = 0;
                        rotateBarraActual = new Rotate(anguloGiroBarra, posXHueco, POS_Y_SUELO+BARRA_TAM_INICIO);
                        rectBarra.getTransforms().add(rotateBarraActual);
                        // Poner el personaje por delante de la barra
                        groupPersonaje.toFront();

                        // Desplaza toda la pantalla a la izquierda
                        TranslateTransition translateTransition =
                                new TranslateTransition(Duration.millis(1000), paneScrollJuego);
                        translateTransition.setByX(-tamanoHueco-anchoNuevaPlataforma);
                        translateTransition.play();

                        // Crear una nueva plataforma
                        posXNuevaPlataforma += inicioMinimoPlataformas + random.nextInt(250);
                        anchoNuevaPlataforma = anchoMinimoPlataformas + random.nextInt(25);
                        Rectangle recPlataformaX = new Rectangle(posXNuevaPlataforma, POS_Y_SUELO, anchoNuevaPlataforma, 200);
                        recPlataformaX.setFill(Color.web("#424242"));

                        // Mostrar nueva plataforma y barra, con personaje delante
                        paneScrollJuego.getChildren().add(recPlataformaX);
                        paneScrollJuego.getChildren().add(rectBarra);
                        groupPersonaje.toFront();

                        posXPersonaje += incXPersonaje;
                        incXPersonaje = 0;

                        // Actualizar marcador
                        puntuacion++;
                        labelMarcador.setText(String.valueOf(puntuacion));
                        
                        // cada 5 puntos incrementar la velocidad de la barra
                        if(puntuacion % 5 == 0) {
                            barraVelocidadCrece++;
                        }
                    }
                }
            }
        }));
        timelineCorrePersonaje.setCycleCount(Timeline.INDEFINITE);

        timelineMuerePersonaje = new Timeline(new KeyFrame(Duration.seconds(0.017), (ActionEvent ae) -> {
            groupPersonaje.setLayoutY(posYPersonaje + incYPersonaje);
            incYPersonaje += 5;
            if(posYPersonaje + incYPersonaje > scene.getHeight()) {
                timelineMuerePersonaje.stop();
            }
        }));
        timelineMuerePersonaje.setCycleCount(Timeline.INDEFINITE);

        // Detectar ratón pulsado
        scene.setOnMousePressed((MouseEvent mouseEvent) -> {
            if(!gameOver) {
                tamanoBarra = BARRA_TAM_INICIO;
                timelineCreceBarra.play();
            }
        });
        
        // Detectar ratón soltado
        scene.setOnMouseReleased((MouseEvent mouseEvent) -> {
            if(!gameOver) {
                timelineCreceBarra.stop();
                timelineGiraBarra.play();
            } else { // REINICIAR PARTIDA
                gameOver = false;
                labelGameover.setVisible(false);
                puntuacion=0;
                labelMarcador.setText(String.valueOf(puntuacion));
                // Recolocar personaje en el borde de la plataforma
                groupPersonaje.setLayoutY(posYPersonaje);
                groupPersonaje.setLayoutX(posXPersonaje);
                // Reiniciar última barra
                tamanoBarra = BARRA_TAM_INICIO;
                rectBarra.setHeight(tamanoBarra);
                rectBarra.setY(POS_Y_SUELO+BARRA_TAM_INICIO-tamanoBarra);
                anguloGiroBarra = 0;
                rotateBarraActual.setAngle(anguloGiroBarra);
            }
        });
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }   
    
}
