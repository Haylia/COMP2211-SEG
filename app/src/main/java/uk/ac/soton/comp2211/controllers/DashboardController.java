package uk.ac.soton.comp2211.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import uk.ac.soton.comp2211.logic.SQLExecutor;

public class DashboardController implements Initializable {

  @FXML
  private LineChart<String, Number> lineGraph;

  @FXML
  private CategoryAxis x;

  @FXML
  private NumberAxis y;

  @FXML
  private Button costBut;

  @FXML
  private DatePicker start_date;

  @FXML
  private DatePicker end_date;

  @FXML
  private CheckBox bounceCountCheckbox;

  @FXML
  private CheckBox bounceRateCheckbox;

  @FXML
  private CheckBox clickCountCheckbox;

  @FXML
  private CheckBox conversionCountCheckbox;

  @FXML
  private CheckBox cpaCheckbox;

  @FXML
  private CheckBox cpcCheckbox;

  @FXML
  private CheckBox cpmCheckbox;

  @FXML
  private CheckBox ctrCheckbox;

  @FXML
  private CheckBox impressionNumberCheckbox;

  @FXML
  private CheckBox totalCostCheckbox;

  @FXML
  private CheckBox uniquesCountCheckbox;

  @FXML
  private VBox metricsVBox;

  private ArrayList<String> metricsSelected = new ArrayList<>();

  private ArrayList<CheckBox> allMetrics = new ArrayList<>();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    start_date.setValue(LocalDate.of(2015,1,1));
    end_date.setValue(LocalDate.of(2015,1,14));

    //selects all the checkboxes that control the metrics
    for (Node node : metricsVBox.getChildren()) {
      if (node instanceof CheckBox) {
        CheckBox checkBox = (CheckBox) node;
        allMetrics.add(checkBox);
        Tooltip tooltip = new Tooltip();
        String text = switch (checkBox.getId()) {
          case "bounceCountCheckbox" -> "The number of single-page visits without any further action.";
          case "bounceRateCheckbox" -> "The percentage of single-page visits divided by the total number of sessions.";
          case "clickCountCheckbox" -> "The total number of times users clicked on an advertisement.";
          case "conversionCountCheckbox" -> "The total number of desired actions completed by users after clicking on an advertisement.";
          case "cpaCheckbox" -> "(Cost per Action): The average cost an advertiser pays for each desired action (e.g., a purchase or signup) completed.";
          case "cpcCheckbox" -> "(Cost per Click): The average cost an advertiser pays for each click on an advertisement.";
          case "cpmCheckbox" -> "(Cost per Mille): The cost an advertiser pays for one thousand views (impressions) of an advertisement.";
          case "ctrCheckbox" -> "(Click-Through Rate): The percentage of ad impressions that result in clicks, calculated as clicks divided by impressions.";
          case "impressionNumberCheckbox" -> "The total number of times an advertisement has been displayed.";
          case "totalCostCheckbox" -> "The cumulative cost of an advertising campaign.";
          case "uniquesCountCheckbox" -> "The number of unique visitors who have viewed an advertisement.";
          default -> ""; // Replace with the text you want to display
        };

        tooltip.setText(text);
        Tooltip.install(checkBox, tooltip);
      }
    }
  }


  @FXML
  private ArrayList<String> updateMetrics() {

    metricsSelected.clear();

    for (CheckBox c : allMetrics) {
      if (c.selectedProperty().get()) {
        metricsSelected.add(c.textProperty().get());
      }
    }

    loadData();

    return metricsSelected;
  }

  public void loadData() {
    lineGraph.getData().clear();

    var dates = SQLExecutor.getDates(start_date.getValue().toString(),end_date.getValue().toString());


    for (String metric : metricsSelected) {
      XYChart.Series<String, Number> series = new XYChart.Series<>();
      series.setName(metric);

      for (LocalDate date : dates) {
        var value = SQLExecutor.executeSQL(date.toString(), metric)[0];
        series.getData().add(new Data<>(date.toString(), Double.parseDouble(value)));
      }
      lineGraph.getData().add(series);

      for (XYChart.Data<String,Number> data : series.getData()){
        data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
          Tooltip.install(data.getNode(),new Tooltip(data.getXValue() + ", " + data.getYValue().toString()));
        });
      }

    }

//    //sample data
//    XYChart.Series<String, Number> impressions = new XYChart.Series<>();
//    impressions.setName("Impressions");
//    impressions.getData().add(new XYChart.Data<>("2022-01-01", 10));
//    impressions.getData().add(new XYChart.Data<>("2022-02-02", 3));
//    impressions.getData().add(new XYChart.Data<>("2022-03-03", 14));
//    impressions.getData().add(new XYChart.Data<>("2022-04-04", 222));
//    impressions.getData().add(new XYChart.Data<>("2022-05-05", 4));
//    lineGraph.getData().addAll(impressions, clicks);

  }

  @FXML
  private void buttonAction(ActionEvent event) {
    //Load total click cost histogram
    if (event.getSource().equals(costBut)) {
      openHistogram();
    }
  }

  private void openHistogram() {

    try {
      FXMLLoader newPane = new FXMLLoader(getClass().getResource("/fxml/Histogram.fxml"));
      Parent root1 = newPane.load();
      Stage stage = new Stage();
      stage.setTitle("Total Click Cost");
      stage.setScene(new Scene(root1));
      stage.show();
    } catch (Exception ignored) {
    }
  }


}