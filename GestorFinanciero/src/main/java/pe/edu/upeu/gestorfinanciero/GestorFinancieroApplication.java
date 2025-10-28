package pe.edu.upeu.gestorfinanciero;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GestorFinancieroApplication extends Application {
    private ConfigurableApplicationContext context;
    private Parent parent;

    public static void main(String[] args) {
        //SpringApplication.run(GestorFinancieroApplication.class, args);
        launch(args);
    }


    @Override
    public void init() throws Exception {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(GestorFinancieroApplication.class);
        builder.application().setWebApplicationType(WebApplicationType.NONE);
        context = builder.run(getParameters().getRaw().toArray(new String[0]));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_meta.fxml"));
        loader.setControllerFactory(context::getBean);
        parent = loader.load();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        //stage.setScene(new Scene(parent, bounds.getWidth(),bounds.getHeight()-100));
        Scene scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Gestor-Finanzas");
        stage.show();
    }
}
